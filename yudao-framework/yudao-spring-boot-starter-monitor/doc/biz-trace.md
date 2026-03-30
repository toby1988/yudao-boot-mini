# 业务追踪指南

## 概述

`@BizTrace` 注解用于在 SkyWalking 中记录业务信息，便于按业务维度查询追踪数据。

## @BizTrace 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BizTrace {
    
    String ID_TAG = "biz.id";      // 业务编号 tag 名
    String TYPE_TAG = "biz.type";  // 业务类型 tag 名
    
    String operationName() default "";  // 操作名
    String id();                        // 业务编号
    String type();                      // 业务类型
}
```

## 使用方式

### 1. 基础使用

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @BizTrace(id = "#orderId", type = "order")
    @GetMapping("/{orderId}")
    public OrderVO getOrder(@PathVariable String orderId) {
        return orderService.get(orderId);
    }
    
    @BizTrace(id = "#dto.orderNo", type = "order")
    @PostMapping("/create")
    public Long create(@RequestBody OrderCreateDTO dto) {
        return orderService.create(dto);
    }
}
```

### 2. 使用 SpEL 表达式

```java
// 使用方法参数
@BizTrace(id = "#orderId", type = "order")
public OrderVO getOrder(String orderId) { }

// 使用 DTO 属性
@BizTrace(id = "#dto.userId", type = "user")
public void process(UserDTO dto) { }

// 使用多个参数
@BizTrace(id = "#userId + '_' + #orderId", type = "order")
public void process(String userId, String orderId) { }
```

### 3. 自定义操作名

```java
@BizTrace(id = "#orderId", type = "order", operationName = "getOrderDetail")
@GetMapping("/{orderId}")
public OrderVO getOrder(@PathVariable String orderId) {
    return orderService.get(orderId);
}
```

---

## 配置 SkyWalking

### 1. 配置 searchableTagKeys

修改 SkyWalking OAP Server 的 `application.yaml`：

```yaml
# skywalking-oap-server/config/application.yaml
storage:
  elasticsearch:
    # ...
    searchableTagKeys: ${SW_SEARCHABLE_TAG_KEYS:biz.id,biz.type}
```

### 2. 重启 OAP Server

配置修改后需要重启 SkyWalking OAP Server。

---

## 使用场景

### 场景1：订单追踪

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @BizTrace(id = "#orderId", type = "order")
    @GetMapping("/{orderId}")
    public OrderVO getOrder(@PathVariable String orderId) {
        return orderService.get(orderId);
    }
    
    @BizTrace(id = "#dto.orderNo", type = "order")
    @PostMapping("/create")
    public Long create(@RequestBody OrderCreateDTO dto) {
        return orderService.create(dto);
    }
    
    @BizTrace(id = "#orderId", type = "order")
    @PutMapping("/{orderId}/pay")
    public void pay(@PathVariable String orderId) {
        orderService.pay(orderId);
    }
}
```

### 场景2：用户追踪

```java
@Service
public class UserService {
    
    @BizTrace(id = "#userId", type = "user")
    public UserVO getUser(Long userId) {
        return userMapper.selectById(userId);
    }
    
    @BizTrace(id = "#dto.mobile", type = "user")
    public void sendCode(SmsDTO dto) {
        smsService.send(dto.getMobile());
    }
}
```

### 场景3：支付追踪

```java
@RestController
@RequestMapping("/api/pay")
public class PayController {
    
    @BizTrace(id = "#payOrderId", type = "pay")
    @PostMapping("/{payOrderId}")
    public PayOrderVO createPay(@PathVariable String payOrderId) {
        return payService.create(payOrderId);
    }
    
    @BizTrace(id = "#dto.payNo", type = "pay")
    @PostMapping("/callback")
    public void callback(@RequestBody PayCallbackDTO dto) {
        payService.callback(dto);
    }
}
```

---

## 在 SkyWalking UI 查看

### 1. 按业务类型查询

```
查询条件: biz.type = "order"
```

### 2. 按业务编号查询

```
查询条件: biz.id = "ORD20240101001"
```

### 3. 组合查询

```
查询条件: biz.type = "order" AND biz.id = "ORD20240101001"
```

---

## 注意事项

### 1. SpEL 表达式

```java
// 确保 SpEL 表达式正确
@BizTrace(id = "#orderId", type = "order")  // ✅ 正确
@BizTrace(id = "orderId", type = "order")    // ❌ 错误（字面量）
```

### 2. 性能影响

```java
// @BizTrace 会增加少量性能开销
// 建议只在关键业务方法上使用
```

### 3. 空值处理

```java
// 如果 id 为 null，不会记录到 SkyWalking
@BizTrace(id = "#dto.orderId", type = "order")
// 确保 orderId 不为 null
```
