# Session 管理指南

## 概述

`WebSocketSessionManager` 接口用于管理用户的 WebSocket 连接。

## 接口定义

```java
public interface WebSocketSessionManager {
    
    // 添加 Session
    void addSession(WebSocketSession session);
    
    // 移除 Session
    void removeSession(WebSocketSession session);
    
    // 获取指定 Session
    WebSocketSession getSession(String id);
    
    // 获取指定用户类型的所有 Session
    Collection<WebSocketSession> getSessionList(Integer userType);
    
    // 获取指定用户的 Session 列表
    Collection<WebSocketSession> getSessionList(Integer userType, Long userId);
}
```

## 使用方式

### 1. 获取 Session

```java
@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 获取指定用户的 Session
     */
    public Collection<WebSocketSession> getUserSessions(Long userId) {
        return sessionManager.getSessionList(
            UserTypeEnum.MEMBER.getValue(), 
            userId
        );
    }
    
    /**
     * 获取所有管理员 Session
     */
    public Collection<WebSocketSession> getAdminSessions() {
        return sessionManager.getSessionList(UserTypeEnum.ADMIN.getValue());
    }
    
    /**
     * 判断用户是否在线
     */
    public boolean isOnline(Integer userType, Long userId) {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType, userId);
        return !sessions.isEmpty();
    }
}
```

### 2. 直接发送消息到 Session

```java
@Service
@RequiredArgsConstructor
public class DirectMessageService {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 直接向 Session 发送消息
     */
    public void sendToSession(String sessionId, String message) throws IOException {
        WebSocketSession session = sessionManager.getSession(sessionId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
    
    /**
     * 向用户的所有 Session 发送消息
     */
    public void sendToUser(Integer userType, Long userId, String message) throws IOException {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType, userId);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
```

### 3. 获取在线用户

```java
@Service
@RequiredArgsConstructor
public class OnlineUserService {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 获取在线管理员数量
     */
    public int getOnlineAdminCount() {
        return sessionManager.getSessionList(UserTypeEnum.ADMIN.getValue()).size();
    }
    
    /**
     * 获取在线会员数量
     */
    public int getOnlineMemberCount() {
        return sessionManager.getSessionList(UserTypeEnum.MEMBER.getValue()).size();
    }
    
    /**
     * 获取所有在线用户的 ID
     */
    public Set<Long> getOnlineUserIds(Integer userType) {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType);
        return sessions.stream()
            .map(session -> WebSocketFrameworkUtils.getLoginUser(session))
            .filter(Objects::nonNull)
            .map(LoginUser::getId)
            .collect(Collectors.toSet());
    }
}
```

## Session 信息

### 获取用户信息

```java
// 获取登录用户
LoginUser loginUser = WebSocketFrameworkUtils.getLoginUser(session);
if (loginUser != null) {
    Long userId = loginUser.getId();
    Integer userType = loginUser.getUserType();
    String nickname = loginUser.getNickname();
}

// 获取租户 ID
Long tenantId = WebSocketFrameworkUtils.getTenantId(session);
```

### Session 属性

```java
// 获取 Session ID
String sessionId = session.getId();

// 获取远程地址
InetSocketAddress remoteAddress = session.getRemoteAddress();

// 判断是否打开
boolean isOpen = session.isOpen();

// 获取自定义属性
Map<String, Object> attributes = session.getAttributes();
```

## 使用场景

### 场景1：在线用户统计

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/online")
public class OnlineController {
    
    private final WebSocketSessionManager sessionManager;
    
    @GetMapping("/count")
    public OnlineCountVO getOnlineCount() {
        OnlineCountVO vo = new OnlineCountVO();
        vo.setAdminCount(sessionManager.getSessionList(UserTypeEnum.ADMIN.getValue()).size());
        vo.setMemberCount(sessionManager.getSessionList(UserTypeEnum.MEMBER.getValue()).size());
        return vo;
    }
    
    @GetMapping("/users")
    public List<OnlineUserVO> getOnlineUsers() {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(
            UserTypeEnum.MEMBER.getValue()
        );
        
        return sessions.stream()
            .map(session -> {
                LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
                if (user == null) return null;
                return new OnlineUserVO(user.getId(), user.getNickname());
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
```

### 场景2：强制下线

```java
@Service
@RequiredArgsConstructor
public class UserKickService {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 强制用户下线
     */
    public void kickUser(Integer userType, Long userId) throws IOException {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType, userId);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                // 发送下线通知
                session.sendMessage(new TextMessage(
                    JsonUtils.toJsonString(new ErrorMessage("您已被强制下线"))
                ));
                // 关闭连接
                session.close();
            }
        }
    }
}
```

### 场景3：Session 数量限制

```java
@Component
@RequiredArgsConstructor
public class SessionLimitChecker {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 检查用户 Session 数量
     */
    public boolean checkSessionLimit(Integer userType, Long userId, int maxCount) {
        Collection<WebSocketSession> sessions = sessionManager.getSessionList(userType, userId);
        return sessions.size() < maxCount;
    }
}
```

## 注意事项

### 1. Session 线程安全

```java
// Session 不是线程安全的，发送消息时需要同步
WebSocketSession session = sessionManager.getSession(sessionId);
if (session != null && session.isOpen()) {
    synchronized (session) {
        session.sendMessage(new TextMessage(message));
    }
}
```

### 2. Session 关闭处理

```java
// 发送前检查 Session 状态
WebSocketSession session = sessionManager.getSession(sessionId);
if (session == null || !session.isOpen()) {
    log.warn("Session 已关闭: {}", sessionId);
    return;
}
```

### 3. 异常处理

```java
try {
    session.sendMessage(new TextMessage(message));
} catch (IOException e) {
    log.error("发送消息失败: sessionId={}", sessionId, e);
    // 移除失效的 Session
    sessionManager.removeSession(session);
}
```
