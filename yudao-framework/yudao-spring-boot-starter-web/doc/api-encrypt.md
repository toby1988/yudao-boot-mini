# API 加解密指南

## 概述

API 加解密模块用于对请求和响应数据进行加密，保护敏感数据传输安全。

## @ApiEncrypt 注解

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEncrypt {
    
    boolean request() default true;   // 是否解密请求
    boolean response() default true;  // 是否加密响应
}
```

## 配置

```yaml
yudao:
  api:
    encrypt:
      enable: true                    # 是否启用
      secret-key: your-secret-key     # 密钥
      aes-key: your-aes-key          # AES 密钥
```

## 使用方式

### 1. 接口级别

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @ApiEncrypt
    @PostMapping("/create")
    public void create(@RequestBody UserCreateDTO dto) {
        // 请求体会自动解密
        userService.create(dto);
    }
    
    @ApiEncrypt
    @GetMapping("/{id}")
    public UserVO detail(@PathVariable Long id) {
        // 响应体会自动加密
        return userService.get(id);
    }
}
```

### 2. 控制器级别

```java
@ApiEncrypt  // 整个控制器的接口都加密
@RestController
@RequestMapping("/api/sensitive")
public class SensitiveController {
    
    @PostMapping("/data")
    public DataVO getData(@RequestBody DataDTO dto) {
        return dataService.get(dto);
    }
}
```

### 3. 仅加密响应

```java
@ApiEncrypt(request = false, response = true)
@GetMapping("/public-data")
public PublicDataVO getPublicData() {
    return dataService.getPublic();
}
```

### 4. 仅解密请求

```java
@ApiEncrypt(request = true, response = false)
@PostMapping("/submit")
public void submit(@RequestBody SubmitDTO dto) {
    service.submit(dto);
}
```

## 加密流程

```
客户端
    │
    ├── 1. 准备原始请求体
    │   { "name": "张三", "mobile": "13248765917" }
    │
    ├── 2. 加密请求体
    │   { "data": "加密后的字符串" }
    │
    └── 3. 发送请求 ───────────────────────────> 服务端
                                                      │
                                                      ├── 4. 解密请求体
                                                      ├── 5. 处理业务
                                                      ├── 6. 加密响应体
                                                      │
    <────────────────────────────────────────────────  │
    │
    ├── 7. 接收加密响应
    │
    └── 8. 解密响应
```

## 完整示例

```java
@ApiEncrypt
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @PostMapping("/create")
    public PaymentVO create(@RequestBody PaymentCreateDTO dto) {
        // 请求体自动解密
        // dto 中的 cardNo, cvv 等敏感字段已解密
        return paymentService.create(dto);
    }
    
    @GetMapping("/{id}")
    public PaymentVO get(@PathVariable Long id) {
        // 响应体自动加密
        PaymentVO payment = paymentService.get(id);
        // 客户端收到的是加密后的数据
        return payment;
    }
}
```

## 注意事项

### 1. 性能考虑

加解密会增加一定的处理时间，建议只对敏感数据接口启用。

### 2. 前端配合

前端需要实现对应的加解密逻辑，与后端使用相同的密钥和算法。

### 3. 调试问题

```java
// 开发环境可以临时关闭
yudao:
  api:
    encrypt:
      enable: false  # 开发时关闭
```
