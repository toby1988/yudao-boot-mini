# TraceId 使用指南

## 概述

`TraceId` 是链路追踪的核心标识，用于跟踪请求在系统中的流转。

## 工作原理

```
请求进入
    │
    ↓
TraceFilter（生成 TraceId）
    │
    ↓
业务代码（获取 TraceId）
    │
    ↓
日志记录（包含 TraceId）
    │
    ↓
响应返回（Header 包含 trace-id）
```

## 使用方式

### 1. 获取 TraceId

```java
@Service
public class OrderService {
    
    public void createOrder(OrderCreateDTO dto) {
        // 获取当前 TraceId
        String traceId = TracerUtils.getTraceId();
        
        log.info("[createOrder][TraceId: {}]", traceId);
        
        // 业务逻辑
    }
}
```

### 2. 设置 TraceId

```java
// 从请求头获取 TraceId
String traceId = request.getHeader("trace-id");
if (traceId != null) {
    TracerUtils.setTraceId(traceId);
}
```

### 3. 响应头返回

框架自动在响应头中返回 `trace-id`：

```bash
# 请求
GET /api/order/1

# 响应头
trace-id: abc123def456
```

---

## 集成 SkyWalking

### 1. 启动 SkyWalking Agent

```bash
java -javaagent:/path/to/skywalking-agent.jar \
     -Dskywalking.agent.service_name=your-service \
     -Dskywalking.collector.backend_service=127.0.0.1:11800 \
     -jar your-app.jar
```

### 2. 自动追踪

SkyWalking Agent 会自动：
- 生成 TraceId
- 传递 TraceId（跨服务）
- 记录 Span 信息

### 3. 在 SkyWalking UI 查看

访问 SkyWalking UI（默认 http://localhost:8080），可以查看：
- 调用链路
- 性能指标
- 错误日志

---

## 日志集成

### Logback 配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <!-- 定义 TraceId -->
    <springProperty scope="context" name="appName" source="spring.application.name"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{tid}] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

### 输出示例

```
2024-01-01 12:00:00.000 [http-nio-8080-exec-1] [abc123def456] INFO  OrderService - 创建订单
```

---

## 使用场景

### 场景1：问题排查

```java
// 用户反馈问题时，通过 TraceId 快速定位日志
log.info("[orderCreate][TraceId: {}][userId: {}]", 
    TracerUtils.getTraceId(), userId);
```

### 场景2：跨服务调用

```java
// Feign 调用时自动传递 TraceId
@FeignClient(name = "user-service")
public interface UserFeignClient {
    
    @GetMapping("/user/{id}")
    UserVO getUser(@PathVariable Long id);
}
```

### 场景3：异步任务

```java
// 异步任务时手动传递 TraceId
@Async
public void asyncProcess(String traceId, Task task) {
    TracerUtils.setTraceId(traceId);
    // 异步处理逻辑
}
```

---

## 注意事项

### 1. 线程池传递

```java
// 使用 TransmittableThreadLocal 确保线程池传递
// 框架已自动配置
```

### 2. MQ 消息传递

```java
// MQ 消息发送时携带 TraceId
message.addHeader("trace-id", TracerUtils.getTraceId());

// MQ 消息消费时恢复 TraceId
String traceId = message.getHeader("trace-id");
TracerUtils.setTraceId(traceId);
```
