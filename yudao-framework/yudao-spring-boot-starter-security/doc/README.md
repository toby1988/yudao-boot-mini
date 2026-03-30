# 安全认证模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── login-user.md              # LoginUser 使用指南
├── security-utils.md          # SecurityFrameworkUtils 使用指南
├── permission.md              # 权限校验指南
├── permit-all.md              # 免认证配置指南
└── operate-log.md             # 操作日志指南
```

## 模块概述

**yudao-spring-boot-starter-security** 是 Yudao 框架的安全认证模块，基于 Spring Security 实现，提供：

1. **Token 认证**：基于 Token 的无状态认证
2. **权限校验**：支持功能权限和数据权限
3. **登录用户管理**：LoginUser 存储用户上下文
4. **免认证配置**：支持 @PermitAll 和配置文件
5. **操作日志**：基于 bizlog-sdk 记录操作日志

### 核心特性

| 特性 | 说明 |
|-----|------|
| Token 认证 | 无状态 Token 认证机制 |
| 权限校验 | @PreAuthorize 注解驱动 |
| 免认证 | @PermitAll 注解 + 配置文件 |
| 登录上下文 | SecurityContextHolder |
| 操作日志 | @OperateLog 注解 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `SecurityFrameworkUtils` | 安全工具类 |
| `LoginUser` | 登录用户信息 |
| `TokenAuthenticationFilter` | Token 认证过滤器 |
| `SecurityProperties` | 安全配置属性 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手安全模块 |
| [LoginUser](login-user.md) | 登录用户信息详解 |
| [安全工具类](security-utils.md) | SecurityFrameworkUtils API |
| [权限校验](permission.md) | 权限校验使用指南 |
| [免认证配置](permit-all.md) | 免认证 URL 配置 |
| [操作日志](operate-log.md) | 操作日志记录 |
