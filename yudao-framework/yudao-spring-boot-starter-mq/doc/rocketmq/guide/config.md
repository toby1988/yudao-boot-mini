# RocketMQ 配置说明

## application.yml 配置

```yaml
# RocketMQ 基础配置
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: my-producer-group
    send-message-timeout: 3000          # 发送超时时间（毫秒）
    retry-times-when-send-failed: 2     # 发送失败重试次数
    retry-times-when-send-async-failed: 2  # 异步发送失败重试次数
    compress-message-body-threshold: 4096  # 消息体压缩阈值（字节）
    max-message-size: 4194304           # 最大消息大小（4MB）

# 框架封装配置
yudao:
  rocketmq:
    enable: true  # 是否启用 RocketMQ（默认 true）
```

## 多环境配置

### 开发环境

```yaml
# application-dev.yml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: dev-producer-group
```

### 生产环境

```yaml
# application-prod.yml
rocketmq:
  name-server: 192.168.1.100:9876;192.168.1.101:9876  # 集群部署
  producer:
    group: prod-producer-group
    send-message-timeout: 5000
    retry-times-when-send-failed: 3
```

## 事务消息配置

```yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: transaction-producer-group
    # 事务回查配置
    transaction-check-thread-pool-size: 2     # 回查线程池大小
    transaction-check-interval: 60000         # 回查间隔（毫秒）
```

## 消费者配置

### 注解配置

```java
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",                  // 主题
    consumerGroup = "order-consumer-group", // 消费者组
    messageModel = MessageModel.CLUSTERING, // 消费模式
    consumeMode = ConsumeMode.CONCURRENTLY, // 消费方式
    consumeThreadMax = 20,                  // 最大消费线程数
    maxReconsumeTimes = 16                  // 最大重试次数
)
```

### 配置说明

| 参数 | 说明 | 默认值 |
|-----|------|-------|
| topic | 主题名称 | - |
| consumerGroup | 消费者组 | - |
| messageModel | 消费模式 | CLUSTERING |
| consumeMode | 消费方式 | CONCURRENTLY |
| consumeThreadMax | 最大消费线程数 | 20 |
| maxReconsumeTimes | 最大重试次数 | 16 |

## 拦截器配置

拦截器会自动注册到容器中，无需额外配置。

```java
@Component
public class TenantInterceptor implements RocketMQMessageInterceptor {
    
    @Override
    public void sendMessageBefore(AbstractRocketMQMessage message) {
        message.addHeader("tenant-id", TenantContext.getTenantId());
    }
}
```

## 日志配置

```yaml
# logback-spring.xml
<logger name="cn.iocoder.yudao.framework.mq.rocketmq" level="DEBUG"/>
```
