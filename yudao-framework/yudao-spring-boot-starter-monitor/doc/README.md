# 监控模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── trace-id.md                # TraceId 使用指南
├── biz-trace.md               # 业务追踪指南
└── skywalking.md              # SkyWalking 集成指南
```

## 模块概述

**yudao-spring-boot-starter-monitor** 是 Yudao 框架的监控模块，提供：

1. **链路追踪**：TraceId 生成和传递
2. **业务追踪**：@BizTrace 注解记录业务信息
3. **Metrics 监控**：集成 Micrometer/Prometheus

### 核心特性

| 特性 | 说明 |
|-----|------|
| TraceId | 自动生成和传递链路追踪 ID |
| 业务追踪 | 记录业务类型和业务编号 |
| SkyWalking | 支持 SkyWalking APM 集成 |
| Prometheus | 支持 Prometheus Metrics |

### 核心类

| 类名 | 说明 |
|-----|------|
| `TracerUtils` | TraceId 工具类 |
| `TracerFrameworkUtils` | 链路追踪工具类 |
| `@BizTrace` | 业务追踪注解 |
| `TraceFilter` | TraceId 过滤器 |
| `BizTraceAspect` | 业务追踪切面 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手监控模块 |
| [TraceId](trace-id.md) | TraceId 使用指南 |
| [业务追踪](biz-trace.md) | @BizTrace 使用指南 |
| [SkyWalking](skywalking.md) | SkyWalking 集成指南 |
