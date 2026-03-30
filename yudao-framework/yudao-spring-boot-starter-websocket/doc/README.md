# WebSocket 模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── message-sender.md          # 消息发送指南
├── message-listener.md        # 消息监听指南
├── session-manager.md         # Session 管理指南
└── cluster-support.md         # 集群支持指南
```

## 模块概述

**yudao-spring-boot-starter-websocket** 是 Yudao 框架的 WebSocket 模块，提供：

1. **Session 管理**：管理用户的 WebSocket 连接
2. **消息发送**：支持向指定用户、用户类型、Session 发送消息
3. **消息处理**：基于消息类型分发到不同的监听器
4. **集群支持**：支持多节点的 WebSocket 广播

### 核心特性

| 特性 | 说明 |
|-----|------|
| 多种发送器 | 支持 Local/Redis/RocketMQ/Kafka/RabbitMQ |
| 消息类型分发 | 基于 type 字段自动分发到监听器 |
| 集群广播 | 多节点间的消息同步 |
| 安全认证 | 支持 Token 认证 |
| 租户隔离 | 支持多租户场景 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `WebSocketMessageSender` | 消息发送接口 |
| `WebSocketMessageListener` | 消息监听接口 |
| `WebSocketSessionManager` | Session 管理接口 |
| `JsonWebSocketMessage` | 消息帧定义 |
| `JsonWebSocketMessageHandler` | 消息处理器 |

## 消息发送器类型

| 类型 | 说明 | 适用场景 |
|-----|------|---------|
| local | 本地发送 | 单机部署 |
| redis | Redis Pub/Sub | 集群部署（轻量） |
| rocketmq | RocketMQ | 集群部署（高可靠） |
| kafka | Kafka | 集群部署（高吞吐） |
| rabbitmq | RabbitMQ | 集群部署（企业级） |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 WebSocket |
| [消息发送](message-sender.md) | 如何发送消息 |
| [消息监听](message-listener.md) | 如何处理消息 |
| [Session 管理](session-manager.md) | Session 管理详解 |
| [集群支持](cluster-support.md) | 多节点部署指南 |
