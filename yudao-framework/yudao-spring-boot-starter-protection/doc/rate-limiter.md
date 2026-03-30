# 限流使用指南

## 概述

限流用于控制接口的访问频率，防止系统被恶意请求或突发流量压垮。

## @RateLimiter 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    
    int time() default 1;                          // 时间窗口
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
    int count() default 100;                       // 允许次数
    String message() default "";                   // 提示信息
    Class<? extends RateLimiterKeyResolver> keyResolver() default DefaultRateLimiterKeyResolver.class;
    String keyArg() default "";                    // Key 参数
}
```

## 使用方式

### 1. 全局限流（默认）

```java
// 1 秒内最多访问 100 次
@RateLimiter
@GetMapping("/list")
public List<UserVO> list() {
    return userService.list();
}

// 1 秒内最多访问 10 次
@RateLimiter(time = 1, count = 10)
@GetMapping("/detail")
public UserVO detail(Long id) {
    return userService.get(id);
}
```

### 2. 用户级限流

```java
// 每个用户 1 分钟内最多访问 5 次
@RateLimiter(time = 1, timeUnit = TimeUnit.MINUTES, count = 5, 
             keyResolver = UserRateLimiterKeyResolver.class)
@PostMapping("/send-code")
public void sendCode(@RequestBody SendCodeDTO dto) {
    smsService.sendCode(dto.getMobile());
}
```

### 3. IP 级限流

```java
// 每个 IP 1 秒内最多访问 10 次
@RateLimiter(time = 1, count = 10, 
             keyResolver = ClientIpRateLimiterKeyResolver.class)
@PostMapping("/login")
public TokenVO login(@RequestBody LoginDTO dto) {
    return authService.login(dto);
}
```

### 4. 自定义表达式限流

```java
// 根据手机号限流
@RateLimiter(time = 1, timeUnit = TimeUnit.MINUTES, count = 5, 
             keyResolver = ExpressionIdempotentKeyResolver.class,
             keyArg = "#dto.mobile")
@PostMapping("/send-code")
public void sendCode(@RequestBody SendCodeDTO dto) {
    smsService.sendCode(dto.getMobile());
}
```

## Key 解析器

| 解析器 | 说明 | Key 格式 |
|-------|------|---------|
| `DefaultRateLimiterKeyResolver` | 全局级别（默认） | `rate_limiter:{方法签名}` |
| `UserRateLimiterKeyResolver` | 用户 ID 级别 | `rate_limiter:{用户ID}` |
| `ClientIpRateLimiterKeyResolver` | 用户 IP 级别 | `rate_limiter:{客户端IP}` |
| `ServerNodeRateLimiterKeyResolver` | 服务器节点级别 | `rate_limiter:{服务器IP}` |
| `ExpressionIdempotentKeyResolver` | 自定义表达式 | `rate_limiter:{表达式值}` |

## 使用场景

### 场景1：短信验证码

```java
// 同一手机号 1 分钟内只能发送 1 条
@RateLimiter(time = 1, timeUnit = TimeUnit.MINUTES, count = 1, 
             keyResolver = ExpressionIdempotentKeyResolver.class,
             keyArg = "#dto.mobile")
@PostMapping("/sms/send")
public void sendSms(@RequestBody SmsSendDTO dto) {
    smsService.send(dto.getMobile());
}
```

### 场景2：登录接口

```java
// 同一 IP 1 秒内最多 10 次
@RateLimiter(time = 1, count = 10, 
             keyResolver = ClientIpRateLimiterKeyResolver.class)
@PostMapping("/auth/login")
public TokenVO login(@RequestBody LoginDTO dto) {
    return authService.login(dto);
}
```

### 场景3：开放 API

```java
// 同一应用 1 分钟内最多 1000 次
@RateLimiter(time = 1, timeUnit = TimeUnit.MINUTES, count = 1000, 
             keyResolver = ExpressionIdempotentKeyResolver.class,
             keyArg = "#appId")
@GetMapping("/openapi/data")
public DataVO getData(@RequestParam String appId) {
    return dataService.getData();
}
```

### 场景4：导出接口

```java
// 同一用户 1 小时内最多导出 5 次
@RateLimiter(time = 1, timeUnit = TimeUnit.HOURS, count = 5, 
             keyResolver = UserRateLimiterKeyResolver.class)
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    exportService.export(response);
}
```

## 实现原理

```
请求到达
    │
    ↓
@RateLimiter 注解被识别
    │
    ↓
RateLimiterAspect 拦截
    │
    ├→ 获取 KeyResolver
    ├→ 解析 Key（如: rate_limiter:user:1001）
    │
    ↓
RateLimiterRedisDAO.tryAcquire()
    │
    ├→ Redis INCR key
    ├→ 设置过期时间
    │
    ↓
    ├── 成功：执行业务逻辑
    └── 失败：抛出 TOO_MANY_REQUESTS 异常
```

## 常见问题

### Q: 限流不生效？

A: 检查以下几点：
1. 确认 Redis 连接正常
2. 确认注解在 public 方法上
3. 确认没有内部调用（AOP 限制）

### Q: 如何自定义错误信息？

```java
@RateLimiter(count = 5, message = "请求过于频繁，请稍后再试")
@GetMapping("/api")
public void api() { }
```

### Q: 如何处理分布式场景？

A: 框架基于 Redis 实现分布式限流，多实例共享限流计数。
