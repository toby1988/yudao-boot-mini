# 数据权限模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── core-concepts.md           # 核心概念
├── dept-permission.md         # 部门数据权限
├── custom-rule.md             # 自定义规则
├── annotation.md              # 注解使用
└── api-reference.md           # API 参考
```

## 模块概述

**yudao-spring-boot-starter-biz-data-permission** 是 Yudao 框架的数据权限模块，提供 SQL 级别的数据权限控制。

### 核心特性

1. **基于 MyBatis Plus 数据权限插件**：在 SQL 执行前自动添加权限条件
2. **注解驱动**：通过 `@DataPermission` 注解控制数据权限
3. **可扩展**：支持自定义数据权限规则
4. **内置部门权限**：提供基于部门的数据权限实现

### 工作原理

```
┌─────────────────────────────────────────────────────────────┐
│                    SQL 执行流程                              │
└─────────────────────────────────────────────────────────────┘

业务代码调用 Mapper
        │
        ↓
┌───────────────────────┐
│   DataPermission      │
│   AnnotationAdvisor   │  ← 检查 @DataPermission 注解
└───────────────────────┘
        │
        ↓
┌───────────────────────┐
│   MyBatis Plus        │
│   DataPermission      │  ← 拦截 SQL
│   Interceptor         │
└───────────────────────┘
        │
        ↓
┌───────────────────────┐
│   DataPermission      │
│   RuleHandler         │  ← 获取权限规则
└───────────────────────┘
        │
        ↓
┌───────────────────────┐
│   DataPermissionRule  │  ← 生成过滤条件
│   getExpression()     │
└───────────────────────┘
        │
        ↓
重写 SQL，添加 WHERE 条件
```

### 核心类

| 类名 | 说明 |
|-----|------|
| `DataPermissionRule` | 数据权限规则接口 |
| `DeptDataPermissionRule` | 部门数据权限规则实现 |
| `DataPermissionRuleFactory` | 规则工厂接口 |
| `DataPermissionRuleHandler` | MyBatis Plus 处理器 |
| `DataPermission` | 数据权限注解 |
| `DataPermissionUtils` | 工具类（忽略权限执行） |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手数据权限 |
| [核心概念](core-concepts.md) | 理解数据权限的工作原理 |
| [部门数据权限](dept-permission.md) | 基于部门的数据权限配置 |
| [自定义规则](custom-rule.md) | 如何自定义数据权限规则 |
| [注解使用](annotation.md) | @DataPermission 注解详解 |
| [API 参考](api-reference.md) | 完整 API 列表 |
