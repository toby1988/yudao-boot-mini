# RocketMQ 封装文档

## 目录结构

```
doc/
└── rocketmq/
    ├── README.md              # 本文档
    ├── guide/                 # 使用指南
    │   ├── quick-start.md     # 快速开始
    │   ├── message-types.md   # 消息类型
    │   └── config.md          # 配置说明
    ├── transaction/           # 事务消息
    │   ├── guide.md           # 事务消息使用指南
    │   └── example.md         # 事务消息示例
    └── architecture/          # 架构设计
        ├── design.md          # 设计说明
        └── comparison.md      # 与 Redis MQ 对比
```

## 快速导航

| 功能 | 文档链接 | 说明 |
|-----|---------|------|
| 快速开始 | [guide/quick-start.md](guide/quick-start.md) | 5分钟上手 |
| 消息类型 | [guide/message-types.md](guide/message-types.md) | 同步/异步/单向/延迟/顺序 |
| 配置说明 | [guide/config.md](guide/config.md) | application.yml 配置 |
| 事务消息 | [transaction/guide.md](transaction/guide.md) | 分布式事务解决方案 |
| 架构设计 | [architecture/design.md](architecture/design.md) | 类图和流程说明 |
| 对比分析 | [architecture/comparison.md](architecture/comparison.md) | 与 Redis MQ 对比 |
