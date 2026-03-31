# 定时任务模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── job-handler.md             # JobHandler 使用指南
├── cron-utils.md              # CronUtils 工具类指南
└── async-task.md              # 异步任务指南
```

## 模块概述

**yudao-spring-boot-starter-job** 是 Yudao 框架的任务模块，提供：

1. **定时任务**：基于 Quartz 实现定时任务调度
2. **异步任务**：基于 Spring Async 实现异步执行
3. **Cron 工具**：Cron 表达式解析和验证

### 核心特性

| 特性 | 说明 |
|-----|------|
| Quartz 集成 | 基于 Quartz 的定时任务调度 |
| JobHandler | 统一的任务处理器接口 |
| Cron 工具 | Cron 表达式验证和时间计算 |
| 异步任务 | Spring Async 增强 |
| 日志记录 | 任务执行日志 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `JobHandler` | 任务处理器接口 |
| `JobHandlerInvoker` | 任务调用器 |
| `SchedulerManager` | 调度管理器 |
| `CronUtils` | Cron 工具类 |
| `JobLogFrameworkService` | 任务日志服务 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手定时任务模块 |
| [JobHandler](job-handler.md) | 任务处理器使用指南 |
| [CronUtils](cron-utils.md) | Cron 表达式工具指南 |
| [异步任务](async-task.md) | 异步任务使用指南 |
