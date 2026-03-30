# RocketMQ 快速开始

## 1. 配置

```yaml
# application.yml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: my-producer-group
```

## 2. 定义消息

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderMessage extends AbstractRocketMQMessage {
    
    private Long orderId;
    private Long userId;
    
    @Override
    public String getTopic() {
        return "ORDER_TOPIC";
    }
}
```

## 3. 发送消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final YudaoRocketMQTemplate rocketMQTemplate;
    
    public void sendMessage() {
        OrderMessage message = new OrderMessage();
        message.setOrderId(1L);
        message.setUserId(1001L);
        message.setKeys("ORDER_1");
        message.setTags("ORDER_CREATE");
        
        // 同步发送
        SendResult result = rocketMQTemplate.syncSend(message);
    }
}
```

## 4. 消费消息

```java
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",
    consumerGroup = "order-consumer-group"
)
public class OrderMessageListener extends AbstractRocketMQMessageListener<OrderMessage> {
    
    @Override
    public void onMessage(OrderMessage message) {
        log.info("收到消息: orderId={}", message.getOrderId());
    }
}
```

## 5. 完成

启动项目即可开始使用 RocketMQ 消息队列。
