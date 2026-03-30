# Key 解析器说明

## 概述

Key 解析器用于生成限流、幂等、锁的 Key，支持多种维度的控制。

## 限流 Key 解析器

### DefaultRateLimiterKeyResolver

全局级别限流，所有请求共享一个计数器。

```java
// Key 格式: rate_limiter:类名#方法名
@RateLimiter(keyResolver = DefaultRateLimiterKeyResolver.class)
@GetMapping("/api")
public void api() { }
```

### UserRateLimiterKeyResolver

用户级别限流，每个用户独立计数。

```java
// Key 格式: rate_limiter:用户ID
@RateLimiter(keyResolver = UserRateLimiterKeyResolver.class)
@GetMapping("/api")
public void api() { }
```

### ClientIpRateLimiterKeyResolver

IP 级别限流，每个 IP 独立计数。

```java
// Key 格式: rate_limiter:客户端IP
@RateLimiter(keyResolver = ClientIpRateLimiterKeyResolver.class)
@PostMapping("/login")
public void login() { }
```

### ServerNodeRateLimiterKeyResolver

服务器节点级别限流，每个节点独立计数。

```java
// Key 格式: rate_limiter:服务器IP
@RateLimiter(keyResolver = ServerNodeRateLimiterKeyResolver.class)
@GetMapping("/api")
public void api() { }
```

### ExpressionRateLimiterKeyResolver

自定义表达式，通过 SpEL 计算 Key。

```java
// Key 格式: rate_limiter:表达式值
@RateLimiter(keyResolver = ExpressionRateLimiterKeyResolver.class, 
             keyArg = "#mobile")
@PostMapping("/send-code")
public void sendCode(@RequestParam String mobile) { }
```

## 幂等 Key 解析器

### DefaultIdempotentKeyResolver

全局级别幂等，根据方法签名和参数生成 Key。

```java
// Key 格式: idempotent:方法签名:参数哈希
@Idempotent(keyResolver = DefaultIdempotentKeyResolver.class)
@PostMapping("/create")
public void create(@RequestBody CreateDTO dto) { }
```

### UserIdempotentKeyResolver

用户级别幂等，同一用户不能重复提交。

```java
// Key 格式: idempotent:用户ID:方法签名
@Idempotent(keyResolver = UserIdempotentKeyResolver.class)
@PostMapping("/submit")
public void submit(@RequestBody SubmitDTO dto) { }
```

### ExpressionIdempotentKeyResolver

自定义表达式，根据业务标识生成 Key。

```java
// Key 格式: idempotent:表达式值
@Idempotent(keyResolver = ExpressionIdempotentKeyResolver.class, 
            keyArg = "#dto.orderNo")
@PostMapping("/pay")
public void pay(@RequestBody PayDTO dto) { }
```

## 自定义 Key 解析器

### 创建解析器

```java
@Component
public class CustomRateLimiterKeyResolver implements RateLimiterKeyResolver {
    
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        
        // 自定义 Key 生成逻辑
        String businessKey = extractBusinessKey(args);
        
        return "rate_limiter:custom:" + businessKey;
    }
    
    private String extractBusinessKey(Object[] args) {
        // 自定义提取逻辑
        for (Object arg : args) {
            if (arg instanceof CustomDTO) {
                return ((CustomDTO) arg).getBusinessId();
            }
        }
        throw new IllegalArgumentException("无法提取业务Key");
    }
}
```

### 使用自定义解析器

```java
@RateLimiter(keyResolver = CustomRateLimiterKeyResolver.class)
@PostMapping("/process")
public void process(@RequestBody CustomDTO dto) {
    // 处理逻辑
}
```

## 解析器对比

| 解析器 | 维度 | Key 格式 | 适用场景 |
|-------|------|---------|---------|
| Default | 全局 | `prefix:类名#方法名` | 全局限制 |
| User | 用户 | `prefix:用户ID` | 用户级限制 |
| ClientIp | IP | `prefix:客户端IP` | IP级限制 |
| ServerNode | 节点 | `prefix:服务器IP` | 节点级限制 |
| Expression | 自定义 | `prefix:表达式值` | 业务级限制 |

## SpEL 表达式示例

```java
// 提取请求参数
@RateLimiter(keyArg = "#mobile")
public void sendCode(@RequestParam String mobile) { }

// 提取 DTO 属性
@RateLimiter(keyArg = "#dto.orderNo")
public void pay(@RequestBody PayDTO dto) { }

// 提取路径变量
@RateLimiter(keyArg = "#orderId")
public void process(@PathVariable Long orderId) { }

// 组合多个属性
@RateLimiter(keyArg = "#userId + ':' + #orderId")
public void process(@RequestParam Long userId, @RequestParam Long orderId) { }

// 调用方法
@RateLimiter(keyArg = "#dto.getUniqueId()")
public void process(@RequestBody OrderDTO dto) { }
```
