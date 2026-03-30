# 自定义缓存管理器指南

## 概述

`TimeoutRedisCacheManager` 扩展了 Spring 的 `RedisCacheManager`，支持为不同缓存设置独立的过期时间。

## 工作原理

```
@Cacheable(cacheNames = "user#7d", key = "#id")
                                │
                                ↓
                TimeoutRedisCacheManager.createRedisCache()
                                │
                                ↓
                    解析 cacheNames 中的过期时间
                                │
                    "user#7d" → name="user", ttl=7天
                                │
                                ↓
                    创建带有自定义过期时间的 RedisCache
```

## 配置格式

### cacheNames 格式

```
cacheNames#ttl
```

| 部分 | 说明 | 示例 |
|-----|------|-----|
| cacheNames | 缓存名称 | `user`, `token`, `sms` |
| ttl | 过期时间 | `7d`, `30m`, `1h`, `60s` |

### 时间单位

| 后缀 | 单位 | 示例 |
|-----|------|-----|
| d | 天 | `7d` = 7天 |
| h | 小时 | `24h` = 24小时 |
| m | 分钟 | `30m` = 30分钟 |
| s | 秒 | `60s` = 60秒 |
| 无后缀 | 秒 | `60` = 60秒 |

## 使用示例

### 用户缓存（7天）

```java
@Cacheable(cacheNames = "user#7d", key = "#id")
public UserVO getUser(Long id) {
    return userMapper.selectById(id);
}
```

### 验证码缓存（5分钟）

```java
@Cacheable(cacheNames = "captcha#5m", key = "#uuid")
public String getCaptcha(String uuid) {
    return generateCaptcha();
}
```

### Token 缓存（24小时）

```java
@Cacheable(cacheNames = "token#24h", key = "#token")
public LoginUser getLoginUser(String token) {
    return tokenStore.get(token);
}
```

### 短信验证码缓存（30分钟）

```java
@Cacheable(cacheNames = "sms#30m", key = "#mobile")
public String getSmsCode(String mobile) {
    return smsStore.get(mobile);
}
```

### Session 缓存（2小时）

```java
@Cacheable(cacheNames = "session#2h", key = "#sessionId")
public Session getSession(String sessionId) {
    return sessionStore.get(sessionId);
}
```

---

## 常见过期时间设置

| 场景 | 推荐过期时间 | cacheNames |
|-----|------------|-----------|
| 用户信息 | 7天 | `user#7d` |
| 登录 Token | 7天 | `token#7d` |
| 短信验证码 | 5-10分钟 | `sms#5m` |
| 图形验证码 | 5分钟 | `captcha#5m` |
| Session | 30分钟 | `session#30m` |
| 配置信息 | 24小时 | `config#24h` |
| 字典数据 | 24小时 | `dict#24h` |
| 菜单数据 | 1小时 | `menu#1h` |

---

## 配置属性

```yaml
yudao:
  cache:
    redis-scan-batch-size: 30  # Redis scan 批量返回数量
```

---

## 注意事项

### 1. 不设置过期时间

```java
// 不设置过期时间（使用默认值）
@Cacheable(cacheNames = "user", key = "#id")
public UserVO getUser(Long id) { }
```

### 2. 过期时间格式

```java
// ✅ 正确格式
@Cacheable(cacheNames = "user#7d", key = "#id")
@Cacheable(cacheNames = "user#24h", key = "#id")
@Cacheable(cacheNames = "user#30m", key = "#id")
@Cacheable(cacheNames = "user#60s", key = "#id")

// ❌ 错误格式
@Cacheable(cacheNames = "user#7", key = "#id")      // 缺少单位
@Cacheable(cacheNames = "user#7days", key = "#id")   // 单位错误
```

### 3. 缓存名称特殊字符

```java
// 如果缓存名称包含 : 会自动处理
@Cacheable(cacheNames = "user#7d:detail", key = "#id")
// 实际缓存 key: user:detail::{id}
```
