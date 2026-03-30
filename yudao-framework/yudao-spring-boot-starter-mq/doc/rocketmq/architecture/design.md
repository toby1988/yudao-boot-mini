# RocketMQ 架构设计

## 类图

### 消息类层次

```
AbstractRocketMQMessage
├── headers: Map<String, String>
├── keys: String
├── tags: String
├── getTopic(): String
├── getHeader(key): String
└── addHeader(key, value): void

AbstractRocketMQTransactionMessage extends AbstractRocketMQMessage
└── transactionId: String
```

### 模板类

```
YudaoRocketMQTemplate
├── rocketMQTemplate: RocketMQTemplate
├── interceptors: List<RocketMQMessageInterceptor>
├── syncSend(message): SendResult
├── asyncSend(message, callback): void
├── sendOneWay(message): void
├── syncSendDelayMessage(message, delayLevel): SendResult
├── syncSendOrderly(message, hashKey): SendResult
└── addInterceptor(interceptor): void

RocketMQTransactionTemplate
├── rocketMQTemplate: RocketMQTemplate
├── interceptors: List<RocketMQMessageInterceptor>
├── sendTransactionMessage(message, arg): TransactionSendResult
├── sendTransactionMessage(message): TransactionSendResult
└── addInterceptor(interceptor): void
```

### 监听器类

```
AbstractRocketMQMessageListener<T extends AbstractRocketMQMessage>
├── messageType: Class<T>
├── yudaoRocketMQTemplate: YudaoRocketMQTemplate (注入)
├── onMessage(MessageExt): void
└── onMessage(T): void (抽象方法)

AbstractRocketMQTransactionListener<T extends AbstractRocketMQTransactionMessage>
├── messageType: Class<T>
├── executeLocalTransaction(T): TransactionStatus (抽象方法)
├── checkLocalTransaction(T): TransactionStatus
├── executeLocalTransaction(Message, Object): LocalTransactionState
└── checkLocalTransaction(MessageExt): LocalTransactionState
```

### 拦截器类

```
RocketMQMessageInterceptor (接口)
├── sendMessageBefore(AbstractRocketMQMessage): void
├── sendMessageAfter(AbstractRocketMQMessage): void
├── consumeMessageBefore(AbstractRocketMQMessage, MessageExt): void
└── consumeMessageAfter(AbstractRocketMQMessage, MessageExt): void

DefaultRocketMQMessageInterceptor implements RocketMQMessageInterceptor
├── 日志记录

TenantRocketMQMessageInterceptor implements RocketMQMessageInterceptor
├── 租户ID传递

TraceRocketMQMessageInterceptor implements RocketMQMessageInterceptor
└── 链路追踪ID传递
```

## 核心流程

### 普通消息发送流程

```
1. 业务代码调用 send()
        │
2. YudaoRocketMQTemplate.sendMessageBefore()
        │
3. 构建 Message 对象
        │
4. 调用 RocketMQTemplate 发送
        │
5. YudaoRocketMQTemplate.sendMessageAfter()
        │
6. 返回 SendResult
```

### 事务消息发送流程

```
1. 业务代码调用 sendTransactionMessage()
        │
2. RocketMQTransactionTemplate.sendMessageBefore()
        │
3. 构建事务消息对象
        │
4. 调用 RocketMQTemplate.sendMessageInTransaction()
        │
5. RocketMQ 存储半消息
        │
6. 回调 executeLocalTransaction()
        │
7. 执行本地事务
        │
8. 返回 COMMIT/ROLLBACK/UNKNOWN
        │
9. RocketMQ 提交/回滚消息
        │
10. 如果 UNKNOWN，后续回调 checkLocalTransaction()
```

### 消息消费流程

```
1. RocketMQ 推送消息
        │
2. AbstractRocketMQMessageListener.onMessage(MessageExt)
        │
3. 反序列化消息体
        │
4. consumeMessageBefore()
        │
5. 子类 onMessage(T)
        │
6. consumeMessageAfter()
        │
7. 如果异常，抛出让 RocketMQ 重试
```

## 设计模式

### 模板方法模式

`AbstractRocketMQMessageListener` 和 `AbstractRocketMQTransactionListener` 使用模板方法模式，定义了消息处理的骨架流程，子类只需实现具体的业务逻辑。

### 拦截器模式

`RocketMQMessageInterceptor` 提供了拦截点，允许在消息发送和消费前后执行自定义逻辑。

### 自动配置模式

通过 Spring Boot AutoConfiguration 自动注册组件，减少配置代码。

## 组件关系

```
Spring 容器
    │
    ├── YudaoRocketMQTemplate (Bean)
    │       └── 持有 List<RocketMQMessageInterceptor>
    │
    ├── RocketMQTransactionTemplate (Bean)
    │       └── 持有 List<RocketMQMessageInterceptor>
    │
    ├── AbstractRocketMQMessageListener (Bean)
    │       └── 通过 @Autowired 注入 YudaoRocketMQTemplate
    │
    ├── AbstractRocketMQTransactionListener (Bean)
    │
    └── RocketMQMessageInterceptor 实现类 (Bean)
            ├── DefaultRocketMQMessageInterceptor
            ├── TenantRocketMQMessageInterceptor
            └── TraceRocketMQMessageInterceptor
```
