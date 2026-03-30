# RocketMQ 消息类型

## 支持的消息发送方式

### 1. 同步发送

等待 Broker 返回发送结果，适用于重要消息。

```java
SendResult result = yudaoRocketMQTemplate.syncSend(message);
```

### 2. 异步发送

不等待返回结果，通过回调处理，适用于对响应时间敏感的场景。

```java
yudaoRocketMQTemplate.asyncSend(message, new SendCallback() {
    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("发送成功: {}", sendResult.getMsgId());
    }
    
    @Override
    public void onException(Throwable e) {
        log.error("发送失败", e);
    }
});
```

### 3. 单向发送

不关心发送结果，适用于日志收集等场景。

```java
yudaoRocketMQTemplate.sendOneWay(message);
```

### 4. 延迟消息

指定延迟级别，适用于订单超时取消等场景。

```java
// 延迟等级: 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
// 等级值:   1  2  3   4  5  6  7  8  9 10 11 12 13 14  15  16 17 18

SendResult result = yudaoRocketMQTemplate.syncSendDelayMessage(message, 16); // 30分钟
```

### 5. 顺序消息

保证消息按顺序消费，适用于订单状态流转等场景。

```java
// 使用订单ID作为哈希键，同一订单的消息发送到同一队列
SendResult result = yudaoRocketMQTemplate.syncSendOrderly(message, "ORDER_123");
```

### 6. 事务消息

保证本地事务和消息发送的一致性，详见 [事务消息指南](../transaction/guide.md)。

```java
TransactionSendResult result = transactionTemplate.sendTransactionMessage(message);
```

## 消息消费模式

### 集群消费

同一消费者组内，一条消息只能被一个消费者消费。

```java
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",
    consumerGroup = "order-consumer-group",
    messageModel = MessageModel.CLUSTERING  // 默认值
)
```

### 广播消费

同一消费者组内，所有消费者都会收到消息。

```java
@RocketMQMessageListener(
    topic = "CACHE_REFRESH_TOPIC",
    consumerGroup = "cache-consumer-group",
    messageModel = MessageModel.BROADCASTING
)
```

### 顺序消费

按消息发送顺序消费。

```java
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",
    consumerGroup = "order-sequential-consumer-group",
    consumeMode = ConsumeMode.ORDERLY
)
```

## 延迟等级表

| 等级 | 延迟时间 |
|-----|---------|
| 1 | 1秒 |
| 2 | 5秒 |
| 3 | 10秒 |
| 4 | 30秒 |
| 5 | 1分钟 |
| 6 | 2分钟 |
| 7 | 3分钟 |
| 8 | 4分钟 |
| 9 | 5分钟 |
| 10 | 6分钟 |
| 11 | 7分钟 |
| 12 | 8分钟 |
| 13 | 9分钟 |
| 14 | 10分钟 |
| 15 | 20分钟 |
| 16 | 30分钟 |
| 17 | 1小时 |
| 18 | 2小时 |
