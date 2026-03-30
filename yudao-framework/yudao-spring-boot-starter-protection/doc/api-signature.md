# API 签名使用指南

## 概述

API 签名用于验证请求的完整性和真实性，防止请求被篡改或重放。

## 签名原理

```
客户端                              服务端
  │                                   │
  ├─ 1. 准备请求参数                    │
  ├─ 2. 生成签名                       │
  │   sign = SHA256(参数 + 请求体       │
  │          + 签名头 + appSecret)      │
  ├─ 3. 发送请求 + 签名 ───────────────>│
  │                                   ├─ 4. 验证 Header
  │                                   ├─ 5. 获取 appSecret
  │                                   ├─ 6. 计算签名
  │                                   ├─ 7. 比对签名
  │                                   ├─ 8. 验证 nonce 唯一性
  │   <─────────────────────────────────┤
  │                                   │
```

## @ApiSignature 注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignature {
    
    int timeout() default 60;                       // 请求有效期（秒）
    TimeUnit timeUnit() default TimeUnit.SECONDS;   // 时间单位
    String message() default "签名不正确";            // 错误信息
    
    String appId() default "appId";     // Header: 应用ID
    String timestamp() default "timestamp";  // Header: 时间戳
    String nonce() default "nonce";          // Header: 随机数
    String sign() default "sign";            // Header: 签名
}
```

## 使用方式

### 1. 基础使用

```java
@RestController
@RequestMapping("/api/callback")
public class CallbackController {
    
    /**
     * 第三方回调接口 - 签名验证
     */
    @ApiSignature
    @PostMapping("/payment")
    public void paymentCallback(@RequestBody PaymentCallbackDTO dto) {
        paymentService.handleCallback(dto);
    }
}
```

### 2. 自定义超时时间

```java
// 请求有效期 5 分钟
@ApiSignature(timeout = 5, timeUnit = TimeUnit.MINUTES)
@PostMapping("/callback")
public void callback(@RequestBody CallbackDTO dto) {
    service.handle(dto);
}
```

### 3. 类级别注解

```java
// 整个 Controller 的所有接口都需要签名
@ApiSignature
@RestController
@RequestMapping("/api/open")
public class OpenApiController {
    
    @GetMapping("/data")
    public DataVO getData() {
        return dataService.get();
    }
    
    @PostMapping("/submit")
    public void submit(@RequestBody SubmitDTO dto) {
        service.submit(dto);
    }
}
```

## 客户端签名

### 签名步骤

```java
public class ApiSignatureClient {
    
    private String appId = "your-app-id";
    private String appSecret = "your-app-secret";
    
    public void sendRequest() {
        // 1. 准备参数
        long timestamp = System.currentTimeMillis();
        String nonce = generateNonce();  // 10位以上随机数
        
        // 2. 构建签名字符串
        SortedMap<String, String> params = new TreeMap<>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        
        SortedMap<String, String> headers = new TreeMap<>();
        headers.put("appId", appId);
        headers.put("timestamp", String.valueOf(timestamp));
        headers.put("nonce", nonce);
        
        String requestBody = "{\"data\":\"test\"}";
        String signString = MapUtil.join(params, "&", "=") 
                + requestBody 
                + MapUtil.join(headers, "&", "=") 
                + appSecret;
        
        // 3. 计算签名
        String sign = DigestUtil.sha256Hex(signString);
        
        // 4. 发送请求
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("appId", appId);
        httpHeaders.set("timestamp", String.valueOf(timestamp));
        httpHeaders.set("nonce", nonce);
        httpHeaders.set("sign", sign);
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(url, new HttpEntity<>(requestBody, httpHeaders), String.class);
    }
    
    private String generateNonce() {
        return RandomUtil.randomString(16);
    }
}
```

### JavaScript 示例

```javascript
async function callApi() {
    const appId = 'your-app-id';
    const appSecret = 'your-app-secret';
    const timestamp = Date.now();
    const nonce = generateNonce(16);
    
    // 构建签名字符串
    const params = new URLSearchParams({key1: 'value1'}).toString();
    const body = JSON.stringify({data: 'test'});
    const signHeaders = `appId=${appId}&nonce=${nonce}&timestamp=${timestamp}`;
    const signString = params + body + signHeaders + appSecret;
    
    // 计算签名
    const sign = sha256(signString);
    
    // 发送请求
    const response = await fetch('/api/callback', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'appId': appId,
            'timestamp': timestamp.toString(),
            'nonce': nonce,
            'sign': sign
        },
        body: body
    });
    
    return response.json();
}

function generateNonce(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}
```

## 签名验证规则

### 1. Header 验证

| 检查项 | 规则 |
|-------|------|
| appId | 非空 |
| timestamp | 非空，且在允许的时间范围内 |
| nonce | 非空，10位以上，且未使用过 |
| sign | 非空 |

### 2. 时间验证

```java
// 请求必须在指定时间内到达
long expireTime = 60 * 1000;  // 60秒
long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp);
if (timestampDisparity > expireTime) {
    // 请求过期
}
```

### 3. Nonce 验证

```java
// Nonce 只能使用一次，防止重放攻击
// 存储到 Redis，设置过期时间为 timeout * 2
if (signatureRedisDAO.getNonce(appId, nonce) != null) {
    // Nonce 已使用
}
```

### 4. 签名计算

```
签名字符串 = 请求参数 + 请求体 + 签名头 + appSecret

示例：
参数: key1=value1&key2=value2
请求体: {"data":"test"}
签名头: appId=xxx&nonce=yyy&timestamp=zzz
密钥: your-secret

拼接: key1=value1&key2=value2{"data":"test"}appId=xxx&nonce=yyy&timestamp=zzzyour-secret
签名: SHA256(拼接结果)
```

## 使用场景

### 场景1：支付回调

```java
@ApiSignature(timeout = 300)  // 5分钟有效
@PostMapping("/callback/payment")
public void paymentCallback(@RequestBody PaymentCallbackDTO dto) {
    paymentService.handleCallback(dto);
}
```

### 场景2：开放 API

```java
@ApiSignature
@RestController
@RequestMapping("/openapi")
public class OpenApiController {
    
    @GetMapping("/user/info")
    public UserInfoVO getUserInfo(@RequestParam String userId) {
        return userService.getUserInfo(userId);
    }
}
```

### 场景3：第三方集成

```java
@ApiSignature(timeout = 300)
@PostMapping("/third-party/callback")
public void thirdPartyCallback(@RequestBody CallbackDTO dto) {
    thirdPartyService.handle(dto);
}
```

## 常见问题

### Q: 如何获取 appId 和 appSecret？

A: 需要在系统中注册应用，获取 appId 和 appSecret。

### Q: 如何配置 appSecret？

A: 通过 `ApiSignatureRedisDAO` 接口获取：

```java
@Service
public class ApiSignatureRedisDAOImpl implements ApiSignatureRedisDAO {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public String getAppSecret(String appId) {
        // 从数据库或配置中获取
        return appSecretService.getSecret(appId);
    }
    
    @Override
    public Boolean setNonce(String appId, String nonce, int timeout, TimeUnit timeUnit) {
        String key = "api:signature:nonce:" + appId + ":" + nonce;
        return redisTemplate.opsForValue().setIfAbsent(key, "1", timeout, timeUnit);
    }
    
    @Override
    public String getNonce(String appId, String nonce) {
        String key = "api:signature:nonce:" + appId + ":" + nonce;
        return redisTemplate.opsForValue().get(key);
    }
}
```

### Q: 签名失败怎么排查？

A: 检查以下几点：
1. Header 参数是否完整
2. 时间戳是否在有效期内
3. Nonce 是否重复使用
4. 签名字符串拼接顺序是否正确
5. appSecret 是否正确
