# Redis 缓存模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── redis-template.md          # RedisTemplate 使用指南
├── spring-cache.md            # Spring Cache 集成指南
└── cache-manager.md           # 自定义缓存管理器指南
```

## 模块概述

**yudao-spring-boot-starter-redis** 是 Yudao 框架的 Redis 缓存模块，基于 Redisson 和 Spring Cache 实现，提供：

1. **RedisTemplate**：JSON 序列化的 Redis 模板
2. **Spring Cache**：注解驱动的缓存机制
3. **自定义过期时间**：支持为不同缓存设置独立的过期时间

### 核心特性

| 特性 | 说明 |
|-----|------|
| JSON 序列化 | Value 使用 Jackson JSON 序列化 |
| 自定义过期时间 | `cacheNames#ttl` 格式 |
| Spring Cache 集成 | 支持 @Cacheable 等注解 |
| Redisson 集成 | 分布式锁、队列等 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `RedisTemplate<String, Object>` | JSON 序列化的 Redis 模板 |
| `TimeoutRedisCacheManager` | 支持自定义过期时间的缓存管理器 |
| `YudaoCacheProperties` | 缓存配置属性 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 Redis 模块 |
| [RedisTemplate](redis-template.md) | RedisTemplate 使用指南 |
| [Spring Cache](spring-cache.md) | Spring Cache 注解使用指南 |
| [缓存管理器](cache-manager.md) | 自定义过期时间配置 |
