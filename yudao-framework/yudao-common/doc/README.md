# Yudao Common 公共模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── collection-utils.md        # Collection 工具类
├── json-utils.md              # JSON 工具类
├── bean-utils.md              # Bean 工具类
├── date-utils.md              # 日期工具类
├── exception.md               # 异常体系
└── pojo.md                    # 通用 POJO
```

## 模块概述

**yudao-common** 是 Yudao 框架的基础公共模块，提供：

1. **工具类**：Collection、JSON、Bean、日期、IO 等
2. **异常体系**：统一的业务异常和错误码
3. **通用 POJO**：CommonResult、PageResult 等
4. **枚举基类**：ArrayValuable 等

### 核心包结构

```
cn.iocoder.yudao.framework.common
├── enums/                     # 枚举
├── exception/                 # 异常
│   ├── ServiceException.java
│   └── enums/
├── pojo/                      # POJO
│   ├── CommonResult.java
│   ├── PageResult.java
│   └── PageParam.java
└── util/                      # 工具类
    ├── collection/            # 集合工具
    ├── json/                  # JSON 工具
    ├── object/                # 对象工具
    ├── date/                  # 日期工具
    ├── io/                    # IO 工具
    └── ...
```

## 核心工具类

| 工具类 | 说明 |
|-------|------|
| `CollectionUtils` | 集合操作（转换、过滤、去重） |
| `JsonUtils` | JSON 序列化/反序列化 |
| `BeanUtils` | Bean 对象转换 |
| `DateUtils` | 日期时间操作 |
| `ObjectUtils` | 对象工具 |
| `HttpUtils` | HTTP 请求 |
| `ServletUtils` | Servlet 工具 |
| `SpringUtils` | Spring 容器工具 |
| `IoUtils` | IO 流工具 |
| `FileUtils` | 文件工具 |

## 通用 POJO

| 类 | 说明 |
|---|------|
| `CommonResult<T>` | 统一返回结果 |
| `PageResult<T>` | 分页结果 |
| `PageParam` | 分页参数 |
| `SortParam` | 排序参数 |

## 异常体系

| 类 | 说明 |
|---|------|
| `ServiceException` | 业务异常 |
| `ErrorCode` | 错误码接口 |
| `GlobalErrorCodeConstants` | 全局错误码 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手公共模块 |
| [Collection 工具类](collection-utils.md) | 集合操作详解 |
| [JSON 工具类](json-utils.md) | JSON 操作详解 |
| [Bean 工具类](bean-utils.md) | Bean 转换详解 |
| [日期工具类](date-utils.md) | 日期操作详解 |
| [异常体系](exception.md) | 异常和错误码 |
| [通用 POJO](pojo.md) | CommonResult 等 |
