# 消息监听指南

## 概述

`WebSocketMessageListener` 接口用于处理客户端发送的消息。

## 接口定义

```java
public interface WebSocketMessageListener<T> {
    
    /**
     * 处理消息
     */
    void onMessage(WebSocketSession session, T message);
    
    /**
     * 获得消息类型
     */
    String getType();
}
```

## 使用方式

### 1. 基础示例

```java
@Slf4j
@Component
public class ChatMessageListener implements WebSocketMessageListener<ChatMessage> {
    
    @Override
    public String getType() {
        return "chat.message";  // 消息类型
    }
    
    @Override
    public void onMessage(WebSocketSession session, ChatMessage message) {
        log.info("收到聊天消息: from={}, to={}, content={}", 
                message.getFromUser(), message.getToUser(), message.getContent());
        
        // 处理消息逻辑
    }
}
```

### 2. 获取用户信息

```java
@Component
public class OrderMessageListener implements WebSocketMessageListener<OrderMessage> {
    
    @Override
    public String getType() {
        return "order.query";
    }
    
    @Override
    public void onMessage(WebSocketSession session, OrderMessage message) {
        // 获取当前登录用户
        LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
        if (loginUser == null) {
            log.warn("用户未登录");
            return;
        }
        
        Long userId = loginUser.getId();
        Long tenantId = WebSocketFrameworkUtils.getTenantId(session);
        
        // 处理业务逻辑
        log.info("用户 {} 查询订单: {}", userId, message.getOrderId());
    }
}
```

### 3. 回复消息

```java
@Component
@RequiredArgsConstructor
public class PingMessageListener implements WebSocketMessageListener<PingMessage> {
    
    private final WebSocketMessageSender messageSender;
    
    @Override
    public String getType() {
        return "custom.ping";
    }
    
    @Override
    public void onMessage(WebSocketSession session, PingMessage message) {
        // 回复消息给当前用户
        LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
        if (loginUser != null) {
            messageSender.sendObject(
                loginUser.getUserType(),
                loginUser.getId(),
                "custom.pong",
                new PongMessage("pong", System.currentTimeMillis())
            );
        }
    }
}
```

### 4. 广播消息

```java
@Component
@RequiredArgsConstructor
public class BroadcastMessageListener implements WebSocketMessageListener<BroadcastMessage> {
    
    private final WebSocketMessageSender messageSender;
    
    @Override
    public String getType() {
        return "chat.broadcast";
    }
    
    @Override
    public void onMessage(WebSocketSession session, BroadcastMessage message) {
        // 获取当前用户
        LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
        if (loginUser == null) {
            return;
        }
        
        // 广播给所有用户
        messageSender.sendObject(
            loginUser.getUserType(),
            "chat.broadcast.receive",
            new BroadcastReceiveMessage(
                loginUser.getId(),
                loginUser.getNickname(),
                message.getContent(),
                System.currentTimeMillis()
            )
        );
    }
}
```

## 消息类型定义

### 消息帧格式

```json
{
    "type": "chat.message",
    "content": "{\"fromUser\":\"user1\",\"content\":\"hello\"}"
}
```

| 字段 | 类型 | 说明 |
|-----|------|------|
| type | String | 消息类型，用于分发到监听器 |
| content | String | 消息内容，JSON 格式 |

### 定义消息类

```java
// 消息内容类
@Data
public class ChatMessage {
    private String fromUser;
    private String toUser;
    private String content;
    private Long timestamp;
}

// 监听器
@Component
public class ChatMessageListener implements WebSocketMessageListener<ChatMessage> {
    
    @Override
    public String getType() {
        return "chat.message";
    }
    
    @Override
    public void onMessage(WebSocketSession session, ChatMessage message) {
        // 处理消息
    }
}
```

## 使用场景

### 场景1：心跳检测

```java
@Slf4j
@Component
public class HeartbeatListener implements WebSocketMessageListener<Object> {
    
    @Override
    public String getType() {
        return "ping";
    }
    
    @Override
    public void onMessage(WebSocketSession session, Object message) {
        // 心跳消息，由 JsonWebSocketMessageHandler 自动处理
        // 这里不需要额外逻辑
        log.debug("收到心跳: sessionId={}", session.getId());
    }
}
```

### 场景2：用户上线/下线

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class UserStatusListener implements WebSocketMessageListener<UserStatusMessage> {
    
    private final WebSocketMessageSender messageSender;
    private final WebSocketSessionManager sessionManager;
    
    @Override
    public String getType() {
        return "user.status";
    }
    
    @Override
    public void onMessage(WebSocketSession session, UserStatusMessage message) {
        LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
        if (loginUser == null) {
            return;
        }
        
        // 广播用户状态变更
        messageSender.sendObject(
            loginUser.getUserType(),
            "user.status.changed",
            new UserStatusChangedMessage(
                loginUser.getId(),
                message.getStatus(),
                System.currentTimeMillis()
            )
        );
    }
}
```

### 场景3：聊天消息

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatListener implements WebSocketMessageListener<ChatSendMessage> {
    
    private final WebSocketMessageSender messageSender;
    private final ChatService chatService;
    
    @Override
    public String getType() {
        return "chat.send";
    }
    
    @Override
    public void onMessage(WebSocketSession session, ChatSendMessage message) {
        LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
        if (loginUser == null) {
            return;
        }
        
        // 保存消息
        ChatMessage chatMessage = chatService.saveMessage(
            loginUser.getId(),
            message.getToUserId(),
            message.getContent()
        );
        
        // 发送给目标用户
        messageSender.sendObject(
            loginUser.getUserType(),
            message.getToUserId(),
            "chat.receive",
            chatMessage
        );
    }
}
```

## 内置消息类型

框架内置了一些消息类型：

| 类型 | 说明 | 处理方式 |
|-----|------|---------|
| ping | 心跳 | 自动回复 pong |

## 注意事项

### 1. 消息类型唯一

```java
// ✅ 正确：类型唯一
@Component
public class ChatListener implements WebSocketMessageListener<ChatMessage> {
    public String getType() { return "chat.message"; }
}

// ❌ 错误：类型重复会覆盖
@Component
public class ChatListener2 implements WebSocketMessageListener<ChatMessage> {
    public String getType() { return "chat.message"; }  // 重复！
}
```

### 2. 异常处理

```java
@Override
public void onMessage(WebSocketSession session, ChatMessage message) {
    try {
        // 业务逻辑
    } catch (Exception e) {
        log.error("处理消息异常", e);
        // 发送错误消息给客户端
        try {
            session.sendMessage(new TextMessage(
                JsonUtils.toJsonString(new ErrorMessage("处理失败", e.getMessage()))
            ));
        } catch (IOException ex) {
            log.error("发送错误消息失败", ex);
        }
    }
}
```

### 3. 线程安全

```java
// 如果需要共享状态，使用线程安全的类
@Component
public class OnlineUserListener implements WebSocketMessageListener<Object> {
    
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();
    
    @Override
    public String getType() { return "user.online"; }
    
    @Override
    public void onMessage(WebSocketSession session, Object message) {
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user != null) {
            onlineUsers.add(user.getId());
        }
    }
}
```
