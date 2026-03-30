# MyBatis 增强模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── base-mapper.md             # BaseMapperX 使用指南
├── query-wrapper.md           # 查询构造器指南
├── base-do.md                 # BaseDO 使用指南
├── field-handler.md           # 字段处理器指南
├── field-encrypt.md           # 字段加密指南
└── data-translate.md          # 数据翻译指南
```

## 模块概述

**yudao-spring-boot-starter-mybatis** 是 Yudao 框架的 MyBatis 增强模块，基于 MyBatis Plus 提供：

1. **增强 Mapper**：BaseMapperX 提供更多便捷方法
2. **增强查询构造器**：LambdaQueryWrapperX 支持条件值存在时才拼接
3. **基础实体**：BaseDO 定义通用字段（createTime、updateTime 等）
4. **自动填充**：DefaultDBFieldHandler 自动填充通用字段
5. **字段加密**：EncryptTypeHandler 基于 AES 加密敏感字段
6. **数据翻译**：TranslateUtils 基于 Easy-Trans 实现数据翻译

### 核心特性

| 特性 | 说明 |
|-----|------|
| 增强 Mapper | 便捷的分页、查询、批量操作 |
| 条件查询 | xxxIfPresent 方法，空值不拼接 |
| 自动填充 | 自动填充创建时间、更新时间等 |
| 字段加密 | AES 加密敏感数据 |
| 数据翻译 | 自动翻译字典、部门名称等 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `BaseMapperX<T>` | 增强的 Mapper 接口 |
| `LambdaQueryWrapperX<T>` | 增强的查询构造器 |
| `BaseDO` | 基础数据对象 |
| `DefaultDBFieldHandler` | 默认字段自动填充 |
| `EncryptTypeHandler` | 字段加密处理器 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 MyBatis 模块 |
| [BaseMapperX](base-mapper.md) | 增强 Mapper 使用指南 |
| [查询构造器](query-wrapper.md) | LambdaQueryWrapperX 使用指南 |
| [BaseDO](base-do.md) | 基础实体使用指南 |
| [字段处理器](field-handler.md) | 自动填充和字段处理 |
| [字段加密](field-encrypt.md) | 敏感字段加密 |
| [数据翻译](data-translate.md) | 数据翻译使用指南 |
