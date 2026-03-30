# Web 框架模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── exception-handler.md       # 全局异常处理
├── api-log.md                 # API 访问日志
├── desensitize.md             # 数据脱敏
├── xss.md                     # XSS 防护
├── api-encrypt.md             # API 加解密
└── swagger.md                 # Swagger 文档
```

## 模块概述

**yudao-spring-boot-starter-web** 是 Yudao 框架的 Web 基础模块，提供：

1. **全局异常处理**：统一异常响应格式
2. **API 访问日志**：记录接口访问日志
3. **数据脱敏**：敏感数据自动脱敏
4. **XSS 防护**：防止跨站脚本攻击
5. **API 加解密**：请求响应加解密
6. **Swagger 文档**：API 文档自动生成

### 核心特性

| 特性 | 包路径 | 说明 |
|-----|--------|------|
| 全局异常 | `web.core.handler` | `GlobalExceptionHandler` |
| API 日志 | `apilog` | `@ApiAccessLog` |
| 数据脱敏 | `desensitize` | `@MobileDesensitize` 等 |
| XSS 防护 | `xss` | `XssFilter` |
| API 加密 | `encrypt` | `@ApiEncrypt` |
| Swagger | `swagger` | Knife4j 集成 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 Web 模块 |
| [全局异常处理](exception-handler.md) | 异常处理详解 |
| [API 访问日志](api-log.md) | 接口日志记录 |
| [数据脱敏](desensitize.md) | 敏感数据脱敏 |
| [XSS 防护](xss.md) | XSS 攻击防护 |
| [API 加解密](api-encrypt.md) | 请求响应加密 |
| [Swagger](swagger.md) | API 文档配置 |
