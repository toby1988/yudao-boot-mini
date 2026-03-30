# SkyWalking 集成指南

## 概述

框架支持与 SkyWalking APM 集成，实现分布式链路追踪和性能监控。

## 集成方式

### 1. 下载 SkyWalking Agent

```bash
# 下载 SkyWalking Agent
wget https://archive.apache.org/dist/skywalking/9.7.0/apache-skywalking-apm-9.7.0.tar.gz

# 解压
tar -zxvf apache-skywalking-apm-9.7.0.tar.gz
```

### 2. 启动应用

```bash
java -javaagent:/path/to/skywalking-agent/agent/skywalking-agent.jar \
     -Dskywalking.agent.service_name=yudao-server \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar yudao-server.jar
```

### 3. Spring Boot 配置

```yaml
# application.yml
skywalking:
  agent:
    service_name: yudao-server
  collector:
    backend_service: 127.0.0.1:11800
```

---

## 功能特性

### 1. 自动追踪

SkyWalking Agent 自动追踪：
- HTTP 请求
- 数据库调用
- Redis 调用
- MQ 调用
- RPC 调用（Dubbo/gRPC）

### 2. 性能指标

- 请求响应时间
- 吞吐量（TPS）
- 错误率
- JVM 指标

### 3. 业务追踪

使用 `@BizTrace` 注解记录业务信息：

```java
@BizTrace(id = "#orderId", type = "order")
public OrderVO getOrder(String orderId) {
    return orderService.get(orderId);
}
```

---

## SkyWalking 配置

### OAP Server 配置

```yaml
# skywalking-oap-server/config/application.yaml
storage:
  elasticsearch:
    clusterNodes: 127.0.0.1:9200
    searchableTagKeys: ${SW_SEARCHABLE_TAG_KEYS:biz.id,biz.type}
```

### Agent 配置

```properties
# skywalking-agent/config/agent.config

# 服务名
agent.service_name=${SW_AGENT_NAME:yudao-server}

# OAP 地址
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}

# 采样率（0-100）
agent.sample_n_per_3_secs=${SW_AGENT_SAMPLE:100}

# 忽略路径
agent.ignore_suffix=${SW_AGENT_IGNORE_SUFFIX:.jpg,.jpeg,.js,.css,.png,.gif,.ico,.woff,.pom}
```

---

## 使用场景

### 场景1：性能分析

通过 SkyWalking UI 查看：
- 慢请求（响应时间 > 1s）
- 热点接口
- JVM 内存使用

### 场景2：故障排查

1. 通过 TraceId 查找链路
2. 定位耗时最长的 Span
3. 查看异常信息

### 场景3：依赖分析

查看服务依赖关系图：
- 服务间调用关系
- 数据库依赖
- 缓存依赖

---

## 最佳实践

### 1. 合理设置采样率

```properties
# 生产环境建议降低采样率
agent.sample_n_per_3_secs=10
```

### 2. 忽略静态资源

```properties
agent.ignore_suffix=.jpg,.jpeg,.js,.css,.png,.gif,.ico
```

### 3. 使用业务追踪

```java
// 关键业务方法添加 @BizTrace
@BizTrace(id = "#orderNo", type = "order")
public void processOrder(String orderNo) {
    // 业务逻辑
}
```

### 4. 记录异常信息

```java
try {
    // 业务逻辑
} catch (Exception e) {
    log.error("[processOrder][TraceId: {}]", TracerUtils.getTraceId(), e);
    throw e;
}
```

---

## 常见问题

### Q: SkyWalking 无法连接？

A: 检查以下配置：
1. OAP Server 是否启动
2. Agent 配置的 backend_service 是否正确
3. 网络是否可达

### Q: 链路不完整？

A: 可能原因：
1. 采样率过低
2. 跨服务 TraceId 未传递
3. 异步调用未处理

### Q: 性能影响？

A: SkyWalking Agent 对性能影响很小（< 5%），可通过降低采样率进一步优化。
