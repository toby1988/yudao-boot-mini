# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-monitor</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
yudao:
  tracer:
    # 链路追踪配置（如有）
```

## 3. 使用 TraceId

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

## 4. 业务追踪

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @BizTrace(id = "#orderId", type = "order")
    @GetMapping("/{orderId}")
    public OrderVO getOrder(@PathVariable String orderId) {
        return orderService.get(orderId);
    }
}
```

## 5. 验证

### 查看 TraceId

```bash
# 响应头包含 trace-id
curl -i http://localhost:8080/api/order/1

# 响应头
HTTP/1.1 200 OK
trace-id: abc123def456
```

### SkyWalking 集成

启动 SkyWalking Agent 后，可以在 SkyWalking UI 中查看链路追踪信息。
