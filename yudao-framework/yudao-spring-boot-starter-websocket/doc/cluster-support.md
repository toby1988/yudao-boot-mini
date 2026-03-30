# 集群支持指南

## 概述

WebSocket 模块支持多节点部署，通过消息队列实现节点间的消息同步。

## 架构

```
┌─────────────────────────────────────────────────────────────┐
│                    集群部署架构                              │
└─────────────────────────────────────────────────────────────┘

                      ┌─────────────┐
                      │   消息队列   │
                      │ (Redis/MQ)  │
                      └─────────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ 节点 1   │   │ 节点 2   │   │ 节点 3   │
        │          │   │          │   │          │
        │ WS 用户A │   │ WS 用户B │   │ WS 用户C │
        └──────────┘   └──────────┘   └──────────┘

流程：
1. 节点1 发送消息给用户B
2. 消息发布到消息队列
3. 所有节点消费消息
4. 节点2 发现用户B 在本地，发送消息
5. 用户B 收到消息
```

## 消息发送器

### Local

本地发送器，适合单机部署。

```yaml
yudao:
  websocket:
    sender-type: local
```

**特点：**
- 只能发送给本节点的用户
- 性能最高
- 不适合集群

### Redis

基于 Redis Pub/Sub，适合轻量级集群。

```yaml
yudao:
  websocket:
    sender-type: redis
```

**特点：**
- 支持多节点广播
- 实时性高
- 消息可能丢失

### RocketMQ

基于 RocketMQ，适合高可靠场景。

```yaml
yudao:
  websocket:
    sender-type: rocketmq
```

**特点：**
- 消息可靠投递
- 支持消息重试
- 性能较高

### Kafka

基于 Kafka，适合高吞吐场景。

```yaml
yudao:
  websocket:
    sender-type: kafka
```

**特点：**
- 吞吐量最高
- 消息持久化
- 实时性稍低

### RabbitMQ

基于 RabbitMQ，适合企业级场景。

```yaml
yudao:
  websocket:
    sender-type: rabbitmq
```

**特点：**
- 功能最丰富
- 支持多种消息模式
- 性能适中

## 选型建议

| 场景 | 推荐 | 理由 |
|-----|------|------|
| 单机开发 | local | 简单方便 |
| 小型集群 | redis | 轻量级，部署简单 |
| 生产环境 | rocketmq | 可靠性高 |
| 高吞吐场景 | kafka | 吞吐量最高 |
| 企业级需求 | rabbitmq | 功能丰富 |

## 配置示例

### Redis 集群

```yaml
# application.yml
spring:
  redis:
    host: 127.0.0.1
    port: 6379

yudao:
  websocket:
    path: /ws
    sender-type: redis
```

### RocketMQ 集群

```yaml
# application.yml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: websocket-producer-group

yudao:
  websocket:
    path: /ws
    sender-type: rocketmq
```

### Kafka 集群

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092
    consumer:
      group-id: websocket-consumer-group

yudao:
  websocket:
    path: /ws
    sender-type: kafka
```

## 消息流向

```
┌─────────────────────────────────────────────────────────────┐
│                    消息流向                                  │
└─────────────────────────────────────────────────────────────┘

1. 后端服务调用 messageSender.send()
        │
        ↓
2. WebSocketMessageSender 实现类
        │
        ├── Local: 直接查找本地 Session 发送
        │
        └── 集群模式:
                │
                ↓
           发布消息到消息队列
                │
                ↓
           所有节点消费消息
                │
                ├→ 节点1: 没有目标用户，忽略
                ├→ 节点2: 有目标用户，发送
                └→ 节点3: 没有目标用户，忽略
```

## 消息格式

### 内部消息格式

```java
// Redis
@Data
public class RedisWebSocketMessage {
    private Integer userType;
    private Long userId;
    private String sessionId;
    private String messageType;
    private String messageContent;
}

// RocketMQ
@Data
public class RocketMQWebSocketMessage {
    private Integer userType;
    private Long userId;
    private String sessionId;
    private String messageType;
    private String messageContent;
}
```

## 注意事项

### 1. 消息过滤

集群模式下，每个节点都会收到消息，但只有包含目标用户的节点会实际发送：

```java
// 框架内部处理逻辑
Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType, userId);
if (sessions.isEmpty()) {
    return;  // 本节点没有目标用户，跳过
}
// 发送消息
```

### 2. 消息重复

同一个用户可能在多个节点都有 Session（多设备登录），消息会发送到所有 Session。

### 3. 消息顺序

- Redis: 不保证顺序
- RocketMQ: 保证同一 Key 的顺序
- Kafka: 保证同一 Partition 的顺序

### 4. 性能优化

```yaml
# 调整消息队列的消费线程数
spring:
  kafka:
    consumer:
      concurrency: 3  # 消费者并发数
```

## 常见问题

### Q: 消息发送失败？

A: 检查以下几点：
1. 消息队列连接是否正常
2. 消息队列 Topic 是否创建
3. 消费者组是否配置正确

### Q: 消息延迟高？

A: 可能原因：
1. 消息队列负载过高
2. 网络延迟
3. 消费者处理慢

### Q: 如何查看在线用户？

A: 需要自行实现在线用户统计，因为每个节点只知道本地的 Session。
