# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-websocket</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
yudao:
  websocket:
    path: /ws                    # WebSocket 连接路径
    sender-type: local           # 消息发送器类型：local/redis/rocketmq/kafka/rabbitmq
```

## 3. 前端连接

```javascript
// 建立 WebSocket 连接
const ws = new WebSocket('ws://localhost:8080/ws?token=' + token);

ws.onopen = function() {
    console.log('连接成功');
    // 发送心跳
    setInterval(() => ws.send('ping'), 30000);
};

ws.onmessage = function(event) {
    if (event.data === 'pong') {
        return;  // 心跳响应
    }
    const message = JSON.parse(event.data);
    console.log('收到消息:', message);
    
    // 根据消息类型处理
    switch(message.type) {
        case 'user.online':
            // 处理上线通知
            break;
        case 'chat.message':
            // 处理聊天消息
            break;
    }
};

ws.onclose = function() {
    console.log('连接关闭');
};
```

## 4. 定义消息类

```java
@Data
public class ChatMessage {
    
    private String fromUser;
    private String toUser;
    private String content;
    private Long timestamp;
}
```

## 5. 实现消息监听器

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
        log.info("收到聊天消息: {}", message);
        // 处理消息逻辑
    }
}
```

## 6. 发送消息

```java
@RestController
@RequiredArgsConstructor
public class ChatController {
    
    private final WebSocketMessageSender messageSender;
    
    @PostMapping("/chat/send")
    public void sendMessage(@RequestBody ChatSendDTO dto) {
        // 构造消息
        ChatMessage message = new ChatMessage();
        message.setFromUser(dto.getFromUser());
        message.setToUser(dto.getToUser());
        message.setContent(dto.getContent());
        message.setTimestamp(System.currentTimeMillis());
        
        // 发送给指定用户
        messageSender.sendObject(
            UserTypeEnum.MEMBER.getValue(),  // 用户类型
            dto.getToUserId(),                // 用户ID
            "chat.message",                   // 消息类型
            message                           // 消息内容
        );
    }
}
```

## 7. 测试

### 后端启动

启动应用后，WebSocket 服务会在 `/ws` 路径监听。

### 前端测试

```javascript
const ws = new WebSocket('ws://localhost:8080/ws?token=your-token');

// 发送消息
ws.send(JSON.stringify({
    type: 'chat.message',
    content: JSON.stringify({
        toUser: 'user2',
        content: '你好'
    })
}));
```

## 消息格式

### 发送消息（客户端 → 服务端）

```json
{
    "type": "chat.message",
    "content": "{\"toUser\":\"user2\",\"content\":\"你好\"}"
}
```

### 接收消息（服务端 → 客户端）

```json
{
    "type": "chat.message",
    "content": "{\"fromUser\":\"user1\",\"toUser\":\"user2\",\"content\":\"你好\",\"timestamp\":1234567890}"
}
```
