# 服务保护模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── rate-limiter.md            # 限流使用指南
├── idempotent.md              # 幂等使用指南
├── distributed-lock.md        # 分布式锁使用指南
├── api-signature.md           # API 签名使用指南
└── key-resolver.md            # Key 解析器说明
```

## 模块概述

**yudao-spring-boot-starter-protection** 是 Yudao 框架的服务保护模块，提供：

1. **限流**：控制接口访问频率，防止系统被压垮
2. **幂等**：防止重复提交，保证操作的幂等性
3. **分布式锁**：保证同一时间只有一个线程执行
4. **API 签名**：防止请求被篡改，保证数据安全

### 核心特性

| 特性 | 说明 | 注解 |
|-----|------|------|
| 限流 | 控制接口访问频率 | `@RateLimiter` |
| 幂等 | 防止重复提交 | `@Idempotent` |
| 分布式锁 | 互斥执行 | `@Lock4j` |
| API 签名 | 防篡改验证 | `@ApiSignature` |

### 核心类

| 模块 | 类名 | 说明 |
|-----|------|------|
| 限流 | `@RateLimiter` | 限流注解 |
| 限流 | `RateLimiterAspect` | 限流切面 |
| 限流 | `RateLimiterKeyResolver` | Key 解析器接口 |
| 幂等 | `@Idempotent` | 幂等注解 |
| 幂等 | `IdempotentAspect` | 幂等切面 |
| 幂等 | `IdempotentKeyResolver` | Key 解析器接口 |
| 签名 | `@ApiSignature` | API 签名注解 |
| 签名 | `ApiSignatureAspect` | 签名切面 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手服务保护 |
| [限流](rate-limiter.md) | 限流使用详解 |
| [幂等](idempotent.md) | 幂等使用详解 |
| [分布式锁](distributed-lock.md) | 分布式锁使用详解 |
| [API 签名](api-signature.md) | API 签名使用详解 |
| [Key 解析器](key-resolver.md) | Key 解析器说明 |

## 使用场景

| 场景 | 推荐方案 | 说明 |
|-----|---------|------|
| 防止接口被刷 | 限流 | 如：发送验证码、登录接口 |
| 防止重复提交 | 幂等 | 如：订单创建、支付回调 |
| 保证互斥执行 | 分布式锁 | 如：定时任务、库存扣减 |
| 接口安全验证 | API 签名 | 如：第三方回调、开放API |
