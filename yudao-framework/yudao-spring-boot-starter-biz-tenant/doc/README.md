# 多租户模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── tenant-context.md          # 租户上下文指南
├── tenant-ignore.md           # 忽略租户指南
├── tenant-job.md              # 多租户定时任务指南
└── configuration.md           # 配置说明
```

## 模块概述

**yudao-spring-boot-starter-biz-tenant** 是 Yudao 框架的多租户模块，提供：

1. **租户上下文**：TenantContextHolder 管理当前租户
2. **数据隔离**：基于 tenant_id 字段自动过滤数据
3. **租户切换**：TenantUtils 支持切换租户执行
4. **忽略租户**：@TenantIgnore 注解跳过租户过滤
5. **多租户定时任务**：@TenantJob 自动遍历租户执行

### 核心特性

| 特性 | 说明 |
|-----|------|
| 数据隔离 | SQL 自动添加 tenant_id 条件 |
| 缓存隔离 | Redis Key 自动添加租户前缀 |
| MQ 隔离 | 消息自动携带租户信息 |
| 租户切换 | 支持指定租户执行逻辑 |
| 忽略租户 | @TenantIgnore 跳过过滤 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `TenantContextHolder` | 租户上下文 Holder |
| `TenantUtils` | 租户工具类 |
| `@TenantIgnore` | 忽略租户注解 |
| `@TenantJob` | 多租户定时任务注解 |
| `TenantBaseDO` | 租户实体基类 |
| `TenantProperties` | 租户配置属性 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手多租户模块 |
| [租户上下文](tenant-context.md) | TenantContextHolder 使用指南 |
| [忽略租户](tenant-ignore.md) | @TenantIgnore 使用指南 |
| [定时任务](tenant-job.md) | @TenantJob 使用指南 |
| [配置说明](configuration.md) | 租户配置详解 |
