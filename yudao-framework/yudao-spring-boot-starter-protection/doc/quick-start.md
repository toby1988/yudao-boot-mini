# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-protection</artifactId>
</dependency>
```

## 2. 基础配置

```yaml
# application.yml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: ${REDIS_PASSWORD}
```

## 3. 限流使用

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    /**
     * 限制 1 秒内最多访问 10 次
     */
    @RateLimiter(time = 1, count = 10)
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
    
    /**
     * 按用户限流：每个用户 1 分钟内最多访问 5 次
     */
    @RateLimiter(time = 1, timeUnit = TimeUnit.MINUTES, count = 5, 
                 keyResolver = UserRateLimiterKeyResolver.class)
    @PostMapping("/send-code")
    public void sendCode(@RequestBody SendCodeDTO dto) {
        smsService.sendCode(dto.getMobile());
    }
}
```

## 4. 幂等使用

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    /**
     * 幂等保护：防止重复提交订单
     */
    @Idempotent(timeout = 30, timeUnit = TimeUnit.SECONDS)
    @PostMapping("/create")
    public OrderVO create(@RequestBody OrderCreateDTO dto) {
        return orderService.create(dto);
    }
    
    /**
     * 按用户幂等：同一用户 10 秒内不能重复提交
     */
    @Idempotent(timeout = 10, keyResolver = UserIdempotentKeyResolver.class)
    @PostMapping("/pay")
    public void pay(@RequestBody PayDTO dto) {
        paymentService.pay(dto);
    }
}
```

## 5. 分布式锁使用

```java
@Service
public class StockService {
    
    /**
     * 分布式锁：保证库存扣减的原子性
     */
    @Lock4j(keys = {"#productId"}, expire = 10000)
    public void deductStock(Long productId, Integer quantity) {
        // 同一时间只有一个线程执行
        Stock stock = stockMapper.selectById(productId);
        if (stock.getQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stockMapper.updateById(stock);
    }
}
```

## 6. API 签名使用

```java
@RestController
@RequestMapping("/api/third-party")
public class ThirdPartyController {
    
    /**
     * API 签名验证：第三方回调接口
     */
    @ApiSignature
    @PostMapping("/callback")
    public void callback(@RequestBody CallbackDTO dto) {
        thirdPartyService.handleCallback(dto);
    }
}
```

## 7. 验证

### 限流验证

```bash
# 快速访问 11 次
for i in {1..11}; do
  curl http://localhost:8080/api/user/list
done
# 第 11 次会返回：请求过于频繁
```

### 幂等验证

```bash
# 快速提交两次
curl -X POST http://localhost:8080/api/order/create -d '{"goodsId":1}'
curl -X POST http://localhost:8080/api/order/create -d '{"goodsId":1}'
# 第二次会返回：重复请求，请稍后重试
```
