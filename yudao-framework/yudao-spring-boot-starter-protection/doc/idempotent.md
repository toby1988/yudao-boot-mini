# 幂等使用指南

## 概述

幂等用于防止重复提交，保证同一操作即使执行多次，结果也只执行一次。

## @Idempotent 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    
    int timeout() default 1;                          // 超时时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;     // 时间单位
    String message() default "重复请求，请稍后重试";     // 提示信息
    Class<? extends IdempotentKeyResolver> keyResolver() default DefaultIdempotentKeyResolver.class;
    String keyArg() default "";                       // Key 参数
    boolean deleteKeyWhenException() default true;    // 异常时删除 Key
}
```

## 使用方式

### 1. 全局幂等（默认）

```java
// 1 秒内不能重复提交
@Idempotent
@PostMapping("/create")
public OrderVO create(@RequestBody OrderCreateDTO dto) {
    return orderService.create(dto);
}

// 30 秒内不能重复提交
@Idempotent(timeout = 30, timeUnit = TimeUnit.SECONDS)
@PostMapping("/pay")
public void pay(@RequestBody PayDTO dto) {
    paymentService.pay(dto);
}
```

### 2. 用户级幂等

```java
// 同一用户 10 秒内不能重复提交
@Idempotent(timeout = 10, keyResolver = UserIdempotentKeyResolver.class)
@PostMapping("/submit")
public void submit(@RequestBody SubmitDTO dto) {
    service.submit(dto);
}
```

### 3. 自定义表达式幂等

```java
// 根据订单号幂等
@Idempotent(keyResolver = ExpressionIdempotentKeyResolver.class, 
            keyArg = "#dto.orderNo")
@PostMapping("/pay")
public void pay(@RequestBody PayDTO dto) {
    paymentService.pay(dto);
}

// 根据业务ID幂等
@Idempotent(keyResolver = ExpressionIdempotentKeyResolver.class, 
            keyArg = "#orderId")
@PutMapping("/audit/{orderId}")
public void audit(@PathVariable Long orderId) {
    auditService.audit(orderId);
}
```

## Key 解析器

| 解析器 | 说明 | Key 格式 |
|-------|------|---------|
| `DefaultIdempotentKeyResolver` | 全局级别（默认） | `idempotent:{方法签名}:{参数}` |
| `UserIdempotentKeyResolver` | 用户 ID 级别 | `idempotent:{用户ID}:{方法签名}` |
| `ExpressionIdempotentKeyResolver` | 自定义表达式 | `idempotent:{表达式值}` |

## 使用场景

### 场景1：订单创建

```java
// 防止重复创建订单
// 同一用户 30 秒内不能重复提交
@Idempotent(timeout = 30, keyResolver = UserIdempotentKeyResolver.class)
@PostMapping("/order/create")
public OrderVO createOrder(@RequestBody OrderCreateDTO dto) {
    return orderService.create(dto);
}
```

### 场景2：支付回调

```java
// 防止支付回调重复处理
// 根据支付单号幂等
@Idempotent(keyResolver = ExpressionIdempotentKeyResolver.class, 
            keyArg = "#dto.paymentNo")
@PostMapping("/callback/payment")
public void paymentCallback(@RequestBody PaymentCallbackDTO dto) {
    paymentService.handleCallback(dto);
}
```

### 场景3：库存扣减

```java
// 防止重复扣减库存
// 根据订单号幂等
@Idempotent(keyResolver = ExpressionIdempotentKeyResolver.class, 
            keyArg = "#orderId")
public void deductStock(Long orderId) {
    // 扣减库存逻辑
}
```

### 场景4：数据提交

```java
// 防止表单重复提交
@Idempotent(timeout = 5, message = "请勿重复提交")
@PostMapping("/form/submit")
public void submit(@RequestBody FormDTO dto) {
    formService.submit(dto);
}
```

## deleteKeyWhenException 参数

```java
// 默认：异常时删除 Key，允许重试
@Idempotent(deleteKeyWhenException = true)
@PostMapping("/pay")
public void pay(@RequestBody PayDTO dto) {
    // 如果抛出异常，Key 会被删除，下次请求可以正常执行
}

// 异常时不删除 Key，不允许重试（在超时时间内）
@Idempotent(deleteKeyWhenException = false, timeout = 60)
@PostMapping("/dangerous")
public void dangerous(@RequestBody DangerousDTO dto) {
    // 即使抛出异常，Key 也不会删除，60秒内不能重复执行
}
```

## 实现原理

```
请求到达
    │
    ↓
@Idempotent 注解被识别
    │
    ↓
IdempotentAspect 拦截（@Around）
    │
    ├→ 获取 KeyResolver
    ├→ 解析 Key
    │
    ↓
IdempotentRedisDAO.setIfAbsent()
    │
    ├── 成功（Key 不存在）
    │       │
    │       ↓
    │   执行业务逻辑
    │       │
    │       ├→ 成功：保留 Key（超时后自动删除）
    │       └→ 异常：删除 Key（允许重试）
    │
    └── 失败（Key 已存在）
            │
            ↓
        抛出 REPEATED_REQUESTS 异常
```

## 幂等 vs 分布式锁

| 特性 | 幂等 @Idempotent | 分布式锁 @Lock4j |
|-----|-----------------|-----------------|
| 目的 | 防止重复请求 | 保证互斥执行 |
| 行为 | 直接拒绝重复请求 | 等待锁释放 |
| 适用场景 | 前端重复提交 | 并发控制 |
| Key 保留 | 超时后自动删除 | 执行完立即释放 |

```java
// 幂等：重复请求直接拒绝
@Idempotent(timeout = 10)
@PostMapping("/create")
public void create() { }

// 分布式锁：等待锁释放后执行
@Lock4j(keys = "#id", expire = 10000)
public void process(Long id) { }
```

## 常见问题

### Q: 幂等不生效？

A: 检查以下几点：
1. 确认 Redis 连接正常
2. 确认注解在 public 方法上
3. 确认没有内部调用（AOP 限制）

### Q: 如何延长幂等时间？

```java
// 延长到 60 秒
@Idempotent(timeout = 60, timeUnit = TimeUnit.SECONDS)
@PostMapping("/long-process")
public void longProcess() { }
```

### Q: 异常后是否允许重试？

```java
// 允许重试（默认）
@Idempotent(deleteKeyWhenException = true)

// 不允许重试
@Idempotent(deleteKeyWhenException = false)
```
