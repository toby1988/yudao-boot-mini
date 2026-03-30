# RocketMQ 封装文档目录

## 目录结构

```
yudao-spring-boot-starter-mq/
├── doc/
│   └── rocketmq/
│       ├── README.md                    # 文档导航
│       ├── guide/                       # 使用指南
│       │   ├── quick-start.md           # 快速开始
│       │   ├── message-types.md         # 消息类型详解
│       │   └── config.md                # 配置说明
│       ├── transaction/                 # 事务消息
│       │   ├── guide.md                 # 事务消息使用指南
│       │   └── example.md               # 事务消息完整示例
│       └── architecture/                # 架构设计
│           ├── design.md                # 架构设计说明
│           └── comparison.md            # 与 Redis MQ 对比
├── src/
│   └── main/java/.../rocketmq/
│       ├── config/
│       ├── core/
│       │   ├── interceptor/
│       │   ├── listener/
│       │   ├── message/
│       │   └── transaction/
│       └── example/
└── pom.xml
```

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](doc/rocketmq/guide/quick-start.md) | 5分钟上手 RocketMQ |
| [消息类型](doc/rocketmq/guide/message-types.md) | 同步/异步/单向/延迟/顺序/事务 |
| [配置说明](doc/rocketmq/guide/config.md) | application.yml 配置详解 |
| [事务消息指南](doc/rocketmq/transaction/guide.md) | 分布式事务解决方案 |
| [事务消息示例](doc/rocketmq/transaction/example.md) | 订单创建+库存扣减完整示例 |
| [架构设计](doc/rocketmq/architecture/design.md) | 类图和流程说明 |
| [对比分析](doc/rocketmq/architecture/comparison.md) | 与 Redis MQ 对比 |
