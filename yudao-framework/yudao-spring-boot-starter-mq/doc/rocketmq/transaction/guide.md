# RocketMQ 事务消息使用指南

## 什么是事务消息？

事务消息是 RocketMQ 提供的一种分布式事务解决方案，用于保证**本地事务**和**消息发送**的一致性。

## 应用场景

| 场景 | 本地事务 | 消息消费 |
|-----|---------|---------|
| 订单创建 | 创建订单 | 扣减库存 |
| 支付成功 | 更新订单状态 | 发放优惠券 |
| 用户注册 | 创建用户 | 发送欢迎邮件 |
| 积分兑换 | 扣减积分 | 发放礼品 |

## 核心流程

```
1. 发送半消息（Half Message）
   Producer → RocketMQ（消息不可消费）

2. 执行本地事务
   RocketMQ → Callback → 执行本地事务

3. 提交或回滚
   本地事务成功 → COMMIT（消息可消费）
   本地事务失败 → ROLLBACK（消息删除）

4. 事务回查
   RocketMQ → 回查本地事务状态（长时间未响应时）
```

## 使用步骤

### 1. 定义事务消息

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderTransactionMessage extends AbstractRocketMQTransactionMessage {
    
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    
    @Override
    public String getTopic() {
        return "ORDER_TRANSACTION_TOPIC";
    }
}
```

### 2. 实现事务监听器

```java
@Slf4j
@Component
public class OrderTransactionListener 
        extends AbstractRocketMQTransactionListener<OrderTransactionMessage> {
    
    @Autowired
    private OrderService orderService;
    
    @Override
    protected TransactionStatus executeLocalTransaction(OrderTransactionMessage message) {
        try {
            // 执行本地事务
            orderService.createOrder(message);
            return TransactionStatus.COMMIT;
        } catch (Exception e) {
            log.error("订单创建失败", e);
            return TransactionStatus.ROLLBACK;
        }
    }
    
    @Override
    protected TransactionStatus checkLocalTransaction(OrderTransactionMessage message) {
        // 事务回查：查询订单是否创建成功
        Order order = orderService.getByOrderId(message.getOrderId());
        return order != null ? TransactionStatus.COMMIT : TransactionStatus.ROLLBACK;
    }
}
```

### 3. 发送事务消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final RocketMQTransactionTemplate transactionTemplate;
    
    public void createOrder(CreateOrderDTO dto) {
        OrderTransactionMessage message = new OrderTransactionMessage();
        message.setOrderId(IdUtil.getSnowflakeNextId());
        message.setUserId(dto.getUserId());
        message.setProductId(dto.getProductId());
        message.setTransactionId("TX_" + message.getOrderId());
        
        // 发送事务消息
        TransactionSendResult result = transactionTemplate.sendTransactionMessage(message);
        
        if (LocalTransactionState.COMMIT_MESSAGE == result.getLocalTransactionState()) {
            log.info("订单创建成功");
        }
    }
}
```

### 4. 消费消息

```java
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "ORDER_TRANSACTION_TOPIC",
    consumerGroup = "inventory-consumer-group"
)
public class InventoryListener 
        extends AbstractRocketMQMessageListener<OrderTransactionMessage> {
    
    @Autowired
    private InventoryService inventoryService;
    
    @Override
    public void onMessage(OrderTransactionMessage message) {
        // 事务消息提交后才会消费
        inventoryService.deductStock(message.getProductId(), message.getQuantity());
    }
}
```

## 事务状态说明

| 状态 | 说明 | 场景 |
|-----|------|-----|
| COMMIT | 提交消息，消费者可以消费 | 本地事务执行成功 |
| ROLLBACK | 回滚消息，消息会被删除 | 本地事务执行失败 |
| UNKNOWN | 未知状态，等待回查 | 系统异常时 |

## 事务回查

当本地事务返回 `UNKNOWN` 或长时间未响应时，RocketMQ 会定期调用 `checkLocalTransaction()` 查询事务状态。

```java
@Override
protected TransactionStatus checkLocalTransaction(OrderTransactionMessage message) {
    // 查询业务数据
    Order order = orderService.getByOrderId(message.getOrderId());
    
    if (order == null) {
        return TransactionStatus.UNKNOWN;  // 等待下次回查
    }
    
    return TransactionStatus.COMMIT;
}
```

## 最佳实践

### 1. 事务ID设计

```java
// 推荐：使用业务唯一标识
message.setTransactionId("TX_ORDER_" + orderId + "_" + timestamp);

// 不推荐：使用随机值
message.setTransactionId(UUID.randomUUID().toString());
```

### 2. 异常处理

```java
@Override
protected TransactionStatus executeLocalTransaction(OrderTransactionMessage message) {
    try {
        doBusiness(message);
        return TransactionStatus.COMMIT;
    } catch (DuplicateKeyException e) {
        // 唯一键冲突，消息已处理
        return TransactionStatus.COMMIT;
    } catch (BusinessException e) {
        // 业务异常，明确回滚
        return TransactionStatus.ROLLBACK;
    } catch (Exception e) {
        // 系统异常，返回 UNKNOWN 等待回查
        return TransactionStatus.UNKNOWN;
    }
}
```

### 3. 幂等性设计

```java
@Override
public void onMessage(OrderTransactionMessage message) {
    String messageId = message.getKeys();
    
    if (processLogService.isProcessed(messageId)) {
        log.warn("消息已处理，跳过");
        return;
    }
    
    // 处理消息
    inventoryService.deductStock(message.getProductId(), message.getQuantity());
    
    // 标记已处理
    processLogService.markProcessed(messageId);
}
```
