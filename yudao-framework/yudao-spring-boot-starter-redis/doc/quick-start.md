# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-redis</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0

yudao:
  cache:
    redis-scan-batch-size: 30  # Redis scan 批量返回数量
```

## 3. 使用 RedisTemplate

```java
@Service
@RequiredArgsConstructor
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 设置缓存
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    // 设置缓存（带过期时间）
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    
    // 获取缓存
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    // 删除缓存
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
```

## 4. 使用 Spring Cache

```java
@Service
public class UserService {
    
    // 使用缓存
    @Cacheable(cacheNames = "user", key = "#id")
    public UserVO getUser(Long id) {
        return userMapper.selectById(id);
    }
    
    // 清除缓存
    @CacheEvict(cacheNames = "user", key = "#id")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    // 更新缓存
    @CachePut(cacheNames = "user", key = "#user.id")
    public UserVO updateUser(User user) {
        userMapper.updateById(user);
        return BeanUtils.toBean(user, UserVO.class);
    }
}
```

## 5. 自定义过期时间

```java
@Service
public class TokenService {
    
    // 7天过期
    @Cacheable(cacheNames = "token#7d", key = "#token")
    public LoginUser getLoginUser(String token) {
        return tokenStore.get(token);
    }
    
    // 30分钟过期
    @Cacheable(cacheNames = "sms#30m", key = "#mobile")
    public String getSmsCode(String mobile) {
        return smsStore.get(mobile);
    }
    
    // 1小时过期
    @Cacheable(cacheNames = "session#1h", key = "#sessionId")
    public Session getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }
}
```

## 6. 验证

启动应用后，查看 Redis 中的缓存数据：

```bash
# 连接 Redis
redis-cli

# 查看所有 key
KEYS *

# 查看缓存值
GET user::1
```
