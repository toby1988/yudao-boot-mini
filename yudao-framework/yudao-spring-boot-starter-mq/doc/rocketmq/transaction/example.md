# RocketMQ 事务消息示例

## 完整示例：订单创建 + 库存扣减

### 业务流程

```
用户下单
    │
    ├──> 1. 发送事务消息（半消息）
    │         └──> RocketMQ 存储半消息（不可消费）
    │
    ├──> 2. 执行本地事务
    │         ├──> 创建订单记录（INSERT INTO orders）
    │         │
    │         ├──> 本地事务成功 → COMMIT
    │         │         └──> RocketMQ 消息变为可消费
    │         │
    │         └──> 本地事务失败 → ROLLBACK
    │                   └──> RocketMQ 删除消息
    │
    └──> 3. 库存服务消费消息
              └──> 扣减库存（UPDATE inventory SET stock = stock - ?）
```

### 数据库表设计

```sql
-- 订单表
CREATE TABLE `orders` (
  `id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '购买数量',
  `amount` int NOT NULL COMMENT '订单金额',
  `status` tinyint NOT NULL COMMENT '订单状态',
  `transaction_id` varchar(64) COMMENT '事务ID',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_transaction_id` (`transaction_id`)
);

-- 库存表
CREATE TABLE `inventory` (
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `stock` int NOT NULL COMMENT '库存数量',
  PRIMARY KEY (`product_id`)
);
```

### 代码实现

#### 1. 事务消息定义

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderTransactionMessage extends AbstractRocketMQTransactionMessage {
    
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer amount;
    
    @Override
    public String getTopic() {
        return "ORDER_TRANSACTION_TOPIC";
    }
}
```

#### 2. 订单服务（发送事务消息）

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final RocketMQTransactionTemplate transactionTemplate;
    
    @Override
    public void createOrder(CreateOrderDTO dto) {
        // 生成订单ID
        Long orderId = IdUtil.getSnowflakeNextId();
        
        // 构造事务消息
        OrderTransactionMessage message = new OrderTransactionMessage();
        message.setOrderId(orderId);
        message.setUserId(dto.getUserId());
        message.setProductId(dto.getProductId());
        message.setQuantity(dto.getQuantity());
        message.setAmount(calculateAmount(dto));
        message.setTransactionId("TX_ORDER_" + orderId);
        message.setKeys("ORDER_TX_" + orderId);
        
        // 发送事务消息
        TransactionSendResult result = transactionTemplate.sendTransactionMessage(message);
        
        if (LocalTransactionState.COMMIT_MESSAGE != result.getLocalTransactionState()) {
            throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
        }
    }
    
    // 用于事务监听器调用
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(OrderTransactionMessage message) {
        Order order = new Order();
        order.setId(message.getOrderId());
        order.setUserId(message.getUserId());
        order.setProductId(message.getProductId());
        order.setQuantity(message.getQuantity());
        order.setAmount(message.getAmount());
        order.setStatus(OrderStatusEnum.CREATED.getStatus());
        order.setTransactionId(message.getTransactionId());
        
        orderMapper.insert(order);
    }
    
    // 用于事务回查
    public Order getByOrderId(Long orderId) {
        return orderMapper.selectById(orderId);
    }
}
```

#### 3. 事务监听器（执行本地事务）

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTransactionListener 
        extends AbstractRocketMQTransactionListener<OrderTransactionMessage> {
    
    private final OrderServiceImpl orderService;
    
    @Override
    protected TransactionStatus executeLocalTransaction(OrderTransactionMessage message) {
        try {
            // 执行本地事务：创建订单
            orderService.saveOrder(message);
            
            log.info("[executeLocalTransaction][订单创建成功，orderId={}]", 
                    message.getOrderId());
            
            return TransactionStatus.COMMIT;
            
        } catch (DuplicateKeyException e) {
            // 唯一键冲突，说明订单已存在
            log.warn("[executeLocalTransaction][订单已存在，orderId={}]", 
                    message.getOrderId());
            return TransactionStatus.COMMIT;
            
        } catch (Exception e) {
            log.error("[executeLocalTransaction][订单创建失败，orderId={}]", 
                    message.getOrderId(), e);
            return TransactionStatus.ROLLBACK;
        }
    }
    
    @Override
    protected TransactionStatus checkLocalTransaction(OrderTransactionMessage message) {
        // 事务回查：查询订单是否创建成功
        Order order = orderService.getByOrderId(message.getOrderId());
        
        if (order != null) {
            log.info("[checkLocalTransaction][订单存在，提交消息，orderId={}]", 
                    message.getOrderId());
            return TransactionStatus.COMMIT;
        } else {
            log.info("[checkLocalTransaction][订单不存在，回滚消息，orderId={}]", 
                    message.getOrderId());
            return TransactionStatus.ROLLBACK;
        }
    }
}
```

#### 4. 库存服务（消费消息）

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper inventoryMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryMapper.selectById(productId);
        
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        
        if (inventory.getStock() < quantity) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        
        inventory.setStock(inventory.getStock() - quantity);
        inventoryMapper.updateById(inventory);
    }
}
```

#### 5. 库存消费者

```java
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = "ORDER_TRANSACTION_TOPIC",
    consumerGroup = "inventory-consumer-group"
)
public class InventoryConsumer 
        extends AbstractRocketMQMessageListener<OrderTransactionMessage> {
    
    private final InventoryService inventoryService;
    
    @Override
    public void onMessage(OrderTransactionMessage message) {
        log.info("[onMessage][收到订单事务消息，开始扣减库存，orderId={}, productId={}, quantity={}]", 
                message.getOrderId(), message.getProductId(), message.getQuantity());
        
        // 扣减库存
        inventoryService.deductStock(message.getProductId(), message.getQuantity());
        
        log.info("[onMessage][库存扣减成功，orderId={}]", message.getOrderId());
    }
}
```

### 测试用例

```java
@SpringBootTest
public class OrderTransactionTest {
    
    @Autowired
    private OrderService orderService;
    
    @Test
    public void testCreateOrder() {
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setUserId(1001L);
        dto.setProductId(2001L);
        dto.setQuantity(2);
        
        orderService.createOrder(dto);
        
        // 验证订单创建成功
        // 验证库存扣减成功
    }
}
```

### 配置文件

```yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: order-producer-group
    transaction-check-thread-pool-size: 2
    transaction-check-interval: 60000
```
