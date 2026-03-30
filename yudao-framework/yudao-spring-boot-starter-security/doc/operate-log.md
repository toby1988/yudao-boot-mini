# 操作日志指南

## 概述

框架基于 bizlog-sdk 实现操作日志记录，支持自动记录接口调用信息。

## @OperateLog 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {
    
    String module() default "";                    // 操作模块
    OperateTypeEnum type() default OperateTypeEnum.OTHER;  // 操作类型
    String name() default "";                      // 操作名称
    boolean logRequestParams() default true;       // 是否记录请求参数
    boolean logResponseData() default false;       // 是否记录响应数据
}
```

## 使用方式

### 1. 基础使用

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @OperateLog(module = "用户管理", type = OperateTypeEnum.LIST)
    @GetMapping("/list")
    public PageResult<UserVO> list(UserPageReqVO req) {
        return userService.page(req);
    }
    
    @OperateLog(module = "用户管理", type = OperateTypeEnum.CREATE)
    @PostMapping("/create")
    public Long create(@RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }
    
    @OperateLog(module = "用户管理", type = OperateTypeEnum.UPDATE)
    @PutMapping("/update")
    public void update(@RequestBody UserUpdateDTO dto) {
        userService.update(dto);
    }
    
    @OperateLog(module = "用户管理", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

### 2. 操作类型

```java
public enum OperateTypeEnum {
    CREATE,     // 新增
    UPDATE,     // 修改
    DELETE,     // 删除
    LIST,       // 查询
    GET,        // 查询详情
    EXPORT,     // 导出
    IMPORT,     // 导入
    OTHER,      // 其他
}
```

### 3. 记录响应数据

```java
@OperateLog(
    module = "用户管理", 
    type = OperateTypeEnum.GET,
    logResponseData = true  // 记录响应数据
)
@GetMapping("/{id}")
public UserVO get(@PathVariable Long id) {
    return userService.get(id);
}
```

---

## 日志字段

| 字段 | 说明 |
|-----|------|
| traceId | 链路追踪 ID |
| userId | 操作人 ID |
| userType | 操作人类型 |
| module | 操作模块 |
| name | 操作名称 |
| type | 操作类型 |
| requestMethod | 请求方法 |
| requestUrl | 请求地址 |
| requestParams | 请求参数 |
| responseData | 响应结果 |
| userIp | 用户 IP |
| userAgent | 用户代理 |
| startTime | 开始时间 |
| endTime | 结束时间 |
| duration | 执行时长 |
| resultCode | 结果状态码 |
| resultMsg | 结果消息 |

---

## 完整示例

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @OperateLog(module = "订单管理", type = OperateTypeEnum.LIST, name = "订单列表")
    @PreAuthorize("@ss.hasPermission('order:list')")
    @GetMapping("/list")
    public PageResult<OrderVO> list(OrderPageReqVO req) {
        return orderService.page(req);
    }
    
    @OperateLog(module = "订单管理", type = OperateTypeEnum.CREATE, name = "创建订单")
    @PreAuthorize("@ss.hasPermission('order:create')")
    @PostMapping("/create")
    public Long create(@RequestBody @Valid OrderCreateDTO dto) {
        return orderService.create(dto);
    }
    
    @OperateLog(module = "订单管理", type = OperateTypeEnum.UPDATE, name = "更新订单")
    @PreAuthorize("@ss.hasPermission('order:update')")
    @PutMapping("/update")
    public void update(@RequestBody @Valid OrderUpdateDTO dto) {
        orderService.update(dto);
    }
    
    @OperateLog(module = "订单管理", type = OperateTypeEnum.DELETE, name = "删除订单")
    @PreAuthorize("@ss.hasPermission('order:delete')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
    
    @OperateLog(module = "订单管理", type = OperateTypeEnum.EXPORT, name = "导出订单")
    @PreAuthorize("@ss.hasPermission('order:export')")
    @GetMapping("/export")
    public void export(OrderExportReqVO req, HttpServletResponse response) {
        orderService.export(req, response);
    }
}
```

---

## 注意事项

### 1. 配置启用

```yaml
yudao:
  operate-log:
    enable: true  # 启用操作日志
```

### 2. 异步记录

```java
// 操作日志异步记录，不影响接口性能
// 日志通过 MQ 异步写入数据库
```

### 3. 敏感参数

```java
// 请求参数中的敏感信息会自动脱敏
// 如：password、token 等
```
