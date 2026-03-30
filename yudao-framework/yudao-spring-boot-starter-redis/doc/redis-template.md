# RedisTemplate 使用指南

## 概述

框架提供的 `RedisTemplate` 使用 JSON 序列化方式，解决了默认 JDK 序列化不可读的问题。

## 配置

框架自动配置了 `RedisTemplate<String, Object>` Bean：

- **Key 序列化**：String 序列化
- **Value 序列化**：Jackson JSON 序列化
- **支持 LocalDateTime**：已注册 JavaTimeModule

## 基础操作

### String 操作

```java
@Service
@RequiredArgsConstructor
public class StringService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 设置值
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    // 设置值（带过期时间）
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    
    // 设置值（带过期时间，秒）
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    // 获取值
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }
    
    // 设置值（如果不存在）
    public boolean setIfAbsent(String key, Object value, Duration timeout) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout));
    }
    
    // 自增
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
    
    // 删除
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    // 设置过期时间
    public Boolean expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout);
    }
    
    // 判断 key 是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
```

### Hash 操作

```java
@Service
@RequiredArgsConstructor
public class HashService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 设置 hash 值
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
    
    // 获取 hash 值
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }
    
    // 获取所有 hash 值
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    // 删除 hash 值
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }
    
    // 判断 hash key 是否存在
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }
}
```

### List 操作

```java
@Service
@RequiredArgsConstructor
public class ListService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 左侧插入
    public Long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    
    // 右侧插入
    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    // 获取列表
    public List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    // 获取列表长度
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }
}
```

### Set 操作

```java
@Service
@RequiredArgsConstructor
public class SetService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 添加元素
    public Long add(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    // 获取所有元素
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    // 判断元素是否存在
    public Boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    // 移除元素
    public Long remove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }
}
```

### ZSet 操作

```java
@Service
@RequiredArgsConstructor
public class ZSetService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 添加元素
    public Boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    // 获取排名
    public Long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }
    
    // 获取范围内的元素
    public Set<Object> range(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }
    
    // 获取分数
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }
}
```

---

## 使用场景

### 场景1：用户登录 Token

```java
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_KEY = "token:";
    
    public void saveToken(String token, LoginUser loginUser, Duration expireTime) {
        redisTemplate.opsForValue().set(TOKEN_KEY + token, loginUser, expireTime);
    }
    
    public LoginUser getLoginUser(String token) {
        return (LoginUser) redisTemplate.opsForValue().get(TOKEN_KEY + token);
    }
    
    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_KEY + token);
    }
}
```

### 场景2：验证码存储

```java
@Service
@RequiredArgsConstructor
public class CaptchaService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CAPTCHA_KEY = "captcha:";
    
    public void saveCaptcha(String uuid, String code) {
        redisTemplate.opsForValue().set(CAPTCHA_KEY + uuid, code, Duration.ofMinutes(5));
    }
    
    public String getCaptcha(String uuid) {
        return (String) redisTemplate.opsForValue().get(CAPTCHA_KEY + uuid);
    }
    
    public void deleteCaptcha(String uuid) {
        redisTemplate.delete(CAPTCHA_KEY + uuid);
    }
}
```

### 场景3：计数器

```java
@Service
@RequiredArgsConstructor
public class CounterService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String COUNTER_KEY = "counter:";
    
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(COUNTER_KEY + key);
    }
    
    public Long get(String key) {
        Long value = (Long) redisTemplate.opsForValue().get(COUNTER_KEY + key);
        return value != null ? value : 0L;
    }
}
```

---

## 注意事项

### 1. 类型转换

```java
// 获取时需要强转
UserVO user = (UserVO) redisTemplate.opsForValue().get(key);

// 或使用泛型方法
@SuppressWarnings("unchecked")
public <T> T get(String key) {
    return (T) redisTemplate.opsForValue().get(key);
}
```

### 2. 序列化问题

```java
// 确保存储的对象可以被 JSON 序列化
@Data
public class UserVO {
    private Long id;
    private String name;
    private LocalDateTime createTime;  // 支持
}
```

### 3. 空值处理

```java
Object value = redisTemplate.opsForValue().get(key);
if (value != null) {
    // 处理逻辑
}
```
