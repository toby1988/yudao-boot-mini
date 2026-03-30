# RocketMQ vs Redis MQ 对比分析

## 架构设计对比

### 相同点

| 特性 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 消息抽象 | `AbstractRedisMessage` | `AbstractRocketMQMessage` |
| 发送模板 | `RedisMQTemplate` | `YudaoRocketMQTemplate` |
| 消费监听器 | `AbstractRedisStreamMessageListener` | `AbstractRocketMQMessageListener` |
| 拦截器 | `RedisMessageInterceptor` | `RocketMQMessageInterceptor` |
| 自动配置 | `@AutoConfiguration` | `@AutoConfiguration` |

### 不同点

| 特性 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 事务消息 | ❌ 不支持 | ✅ 支持 |
| 延迟消息 | ❌ 不支持 | ✅ 支持（18个等级） |
| 顺序消息 | ❌ 不支持 | ✅ 支持 |
| 消息重试 | 手动实现 | 自动重试16次 |
| 死信队列 | ❌ 不支持 | ✅ 支持 |

## 功能对比

### 消息发送

| 方式 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 同步发送 | ✅ `send()` | ✅ `syncSend()` |
| 异步发送 | ❌ | ✅ `asyncSend()` |
| 单向发送 | ❌ | ✅ `sendOneWay()` |
| 延迟消息 | ❌ | ✅ `syncSendDelayMessage()` |
| 顺序消息 | ❌ | ✅ `syncSendOrderly()` |
| 事务消息 | ❌ | ✅ `sendTransactionMessage()` |

### 消费模式

| 模式 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 集群消费 | ✅ Stream | ✅ CLUSTERING |
| 广播消费 | ✅ Channel | ✅ BROADCASTING |
| 顺序消费 | ❌ | ✅ ORDERLY |

### 可靠性

| 特性 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 持久化 | Redis 内存 | 磁盘文件 |
| 消息重试 | 手动 | 自动（16次） |
| 死信队列 | ❌ | ✅ |
| 事务回查 | ❌ | ✅ |
| 消息轨迹 | ❌ | ✅ |

## 性能对比

| 指标 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 吞吐量 | 5-10万/s | 10-20万/s |
| 延迟 | < 1ms | < 10ms |
| 消息堆积 | 受内存限制 | 亿级 |

## 适用场景对比

### Redis MQ 适用场景

- 日志收集
- 缓存刷新
- 简单通知
- 已有 Redis 基础设施
- 中小型项目

### RocketMQ 适用场景

- 订单系统（延迟消息、顺序消息）
- 支付系统（高可靠、事务消息）
- 秒杀系统（高并发、高可靠）
- 大中型项目

## 迁移路径

### 从 Redis MQ 迁移到 RocketMQ

```java
// Redis MQ
public class OrderMessage extends AbstractRedisStreamMessage { ... }

// RocketMQ
public class OrderMessage extends AbstractRocketMQMessage { ... }
```

```java
// Redis MQ
redisMQTemplate.send(message);

// RocketMQ
rocketMQTemplate.syncSend(message);
```

```java
// Redis MQ
public class OrderListener extends AbstractRedisStreamMessageListener<OrderMessage> {
    public void onMessage(OrderMessage message) { ... }
}

// RocketMQ
@RocketMQMessageListener(topic = "ORDER_TOPIC", consumerGroup = "order-group")
public class OrderListener extends AbstractRocketMQMessageListener<OrderMessage> {
    public void onMessage(OrderMessage message) { ... }
}
```

## 代码对比

### 发送消息

**Redis MQ:**
```java
@Autowired
private RedisMQTemplate redisMQTemplate;

public void send() {
    OrderMessage message = new OrderMessage();
    message.setOrderId(1L);
    redisMQTemplate.send(message);
}
```

**RocketMQ:**
```java
@Autowired
private YudaoRocketMQTemplate rocketMQTemplate;

public void send() {
    OrderMessage message = new OrderMessage();
    message.setOrderId(1L);
    message.setKeys("ORDER_1");
    message.setTags("ORDER_CREATE");
    rocketMQTemplate.syncSend(message);
}
```

### 消费消息

**Redis MQ:**
```java
@Component
public class OrderListener extends AbstractRedisStreamMessageListener<OrderMessage> {
    @Override
    public void onMessage(OrderMessage message) {
        // 处理消息
    }
}
```

**RocketMQ:**
```java
@Component
@RocketMQMessageListener(topic = "ORDER_TOPIC", consumerGroup = "order-group")
public class OrderListener extends AbstractRocketMQMessageListener<OrderMessage> {
    @Override
    public void onMessage(OrderMessage message) {
        // 处理消息
    }
}
```

## 总结

| 维度 | Redis MQ | RocketMQ |
|-----|---------|----------|
| 复杂度 | 简单 | 中等 |
| 可靠性 | 中等 | 高 |
| 功能丰富度 | 基础 | 丰富 |
| 性能 | 高 | 更高 |
| 适用规模 | 中小型 | 中大型 |

**建议**：根据业务需求选择，如果需要事务消息、延迟消息、高可靠性，选择 RocketMQ。
