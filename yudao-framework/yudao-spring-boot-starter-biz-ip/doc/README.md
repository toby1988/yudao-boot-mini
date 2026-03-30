# IP 地域工具模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── ip-utils.md                # IP 工具类使用指南
├── area-utils.md              # 区域工具类使用指南
├── data-model.md              # 数据模型说明
└── api-reference.md           # API 参考
```

## 模块概述

**yudao-spring-boot-starter-biz-ip** 是 Yudao 框架的 IP 地域扩展模块，提供：

1. **IP 地址查询**：根据 IP 地址查询对应的地理位置信息（城市）
2. **区域信息管理**：提供中国行政区划数据的查询和操作

### 数据来源

| 功能 | 数据源 | 说明 |
|-----|--------|------|
| IP 查询 | ip2region.xdb | 精简版，加载到内存，毫秒级查询 |
| 区域数据 | area.csv | 中国行政区划，包含省/市/区数据 |

### 核心类

| 类名 | 包名 | 说明 |
|-----|------|------|
| `IPUtils` | `cn.iocoder.yudao.framework.ip.core.utils` | IP 地址工具类 |
| `AreaUtils` | `cn.iocoder.yudao.framework.ip.core.utils` | 区域工具类 |
| `Area` | `cn.iocoder.yudao.framework.ip.core` | 区域节点实体类 |
| `AreaTypeEnum` | `cn.iocoder.yudao.framework.ip.core.enums` | 区域类型枚举 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 IP 地域工具 |
| [IP 工具类](ip-utils.md) | IPUtils 使用指南 |
| [区域工具类](area-utils.md) | AreaUtils 使用指南 |
| [数据模型](data-model.md) | Area 数据结构说明 |
| [API 参考](api-reference.md) | 完整 API 列表 |
