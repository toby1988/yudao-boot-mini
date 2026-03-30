# 消息发送指南

## 概述

`WebSocketMessageSender` 接口提供多种消息发送方式。

## 接口定义

```java
public interface WebSocketMessageSender {
    
    // 发送给指定用户
    void send(Integer userType, Long userId, String messageType, String messageContent);
    
    // 发送给指定用户类型的所有用户
    void send(Integer userType, String messageType, String messageContent);
    
    // 发送给指定 Session
    void send(String sessionId, String messageType, String messageContent);
    
    // Object 版本（自动序列化为 JSON）
    void sendObject(Integer userType, Long userId, String messageType, Object messageContent);
    void sendObject(Integer userType, String messageType, Object messageContent);
    void sendObject(String sessionId, String messageType, Object messageContent);
}
```

## 使用方式

### 1. 发送给指定用户

```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final WebSocketMessageSender messageSender;
    
    public void sendToUser(Long userId, String message) {
        messageSender.send(
            UserTypeEnum.MEMBER.getValue(),  // 用户类型
            userId,                           // 用户ID
            "notification",                   // 消息类型
            message                           // 消息内容（JSON）
        );
    }
    
    public void sendOrderNotify(Long userId, OrderVO order) {
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            userId,
            "order.notify",
            order  // 自动转为 JSON
        );
    }
}
```

### 2. 发送给指定用户类型

```java
// 发送给所有管理员
public void notifyAllAdmin(String message) {
    messageSender.send(
        UserTypeEnum.ADMIN.getValue(),
        "system.announcement",
        message
    );
}

// 发送给所有会员
public void notifyAllMember(AnnouncementVO announcement) {
    messageSender.sendObject(
        UserTypeEnum.MEMBER.getValue(),
        "announcement",
        announcement
    );
}
```

### 3. 发送给指定 Session

```java
public void sendToSession(String sessionId, String message) {
    messageSender.send(
        sessionId,
        "session.message",
        message
    );
}
```

## 发送器类型

### LocalWebSocketMessageSender

本地发送器，仅适合单机部署。

```yaml
yudao:
  websocket:
    sender-type: local
```

### RedisWebSocketMessageSender

基于 Redis Pub/Sub，适合集群部署。

```yaml
yudao:
  websocket:
    sender-type: redis
```

### RocketMQWebSocketMessageSender

基于 RocketMQ，适合高可靠场景。

```yaml
yudao:
  websocket:
    sender-type: rocketmq
```

### KafkaWebSocketMessageSender

基于 Kafka，适合高吞吐场景。

```yaml
yudao:
  websocket:
    sender-type: kafka
```

### RabbitMQWebSocketMessageSender

基于 RabbitMQ，适合企业级场景。

```yaml
yudao:
  websocket:
    sender-type: rabbitmq
```

## 使用场景

### 场景1：在线客服

```java
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final WebSocketMessageSender messageSender;
    
    /**
     * 客服发送消息给用户
     */
    public void replyToUser(Long userId, String content) {
        ChatMessage message = new ChatMessage();
        message.setFromUser("客服");
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            userId,
            "chat.message",
            message
        );
    }
    
    /**
     * 用户发送消息给客服
     */
    public void userSendMessage(Long userId, String content) {
        // 通知在线客服
        messageSender.sendObject(
            UserTypeEnum.ADMIN.getValue(),
            "chat.user.message",
            new UserMessage(userId, content)
        );
    }
}
```

### 场景2：系统通知

```java
@Service
@RequiredArgsConstructor
public class SystemNotificationService {
    
    private final WebSocketMessageSender messageSender;
    
    /**
     * 发送系统公告
     */
    public void sendAnnouncement(String content) {
        // 发送给所有用户
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            "system.announcement",
            new Announcement(content, System.currentTimeMillis())
        );
    }
    
    /**
     * 发送个人通知
     */
    public void sendPersonalNotice(Long userId, String content) {
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            userId,
            "personal.notice",
            new Notice(content, System.currentTimeMillis())
        );
    }
}
```

### 场景3：实时数据推送

```java
@Service
@RequiredArgsConstructor
public class RealtimeDataService {
    
    private final WebSocketMessageSender messageSender;
    
    /**
     * 推送实时股价
     */
    public void pushStockPrice(String stockCode, BigDecimal price) {
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            "stock.price",
            new StockPrice(stockCode, price, System.currentTimeMillis())
        );
    }
    
    /**
     * 推送实时订单状态
     */
    public void pushOrderStatus(Long userId, Long orderId, Integer status) {
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),
            userId,
            "order.status",
            new OrderStatus(orderId, status)
        );
    }
}
```

### 场景4：多人游戏

```java
@Service
@RequiredArgsConstructor
public class GameService {
    
    private final WebSocketMessageSender messageSender;
    
    /**
     * 广播游戏状态
     */
    public void broadcastGameState(GameState state) {
        // 发送给所有在线玩家
        for (Long playerId : state.getPlayerIds()) {
            messageSender.sendObject(
                UserTypeEnum.MEMBER.getValue(),
                playerId,
                "game.state",
                state
            );
        }
    }
}
```

## 注意事项

### 1. 消息格式

```java
// ✅ 正确：使用 sendObject
messageSender.sendObject(userType, userId, "type", object);

// ✅ 正确：使用 send + JSON
messageSender.send(userType, userId, "type", JsonUtils.toJsonString(object));

// ❌ 错误：发送非 JSON 字符串
messageSender.send(userType, userId, "type", "plain text");
```

### 2. 用户类型

```java
// 使用枚举值
UserTypeEnum.ADMIN.getValue()   // 1 - 管理员
UserTypeEnum.MEMBER.getValue()  // 2 - 会员
```

### 3. 异常处理

```java
try {
    messageSender.sendObject(userType, userId, "type", message);
} catch (Exception e) {
    log.error("发送消息失败: userId={}", userId, e);
    // 根据业务需求处理
}
```
