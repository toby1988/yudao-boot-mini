# Spring Cache 使用指南

## 概述

框架集成了 Spring Cache，支持使用注解驱动缓存机制。

## 核心注解

### @Cacheable

查询时使用，如果缓存存在则直接返回，否则执行方法并缓存结果。

```java
@Cacheable(cacheNames = "user", key = "#id")
public UserVO getUser(Long id) {
    return userMapper.selectById(id);
}
```

### @CachePut

更新时使用，无论缓存是否存在，都会执行方法并更新缓存。

```java
@CachePut(cacheNames = "user", key = "#user.id")
public UserVO updateUser(User user) {
    userMapper.updateById(user);
    return BeanUtils.toBean(user, UserVO.class);
}
```

### @CacheEvict

删除时使用，清除指定的缓存。

```java
@CacheEvict(cacheNames = "user", key = "#id")
public void deleteUser(Long id) {
    userMapper.deleteById(id);
}
```

### @Caching

组合多个缓存操作。

```java
@Caching(
    put = @CachePut(cacheNames = "user", key = "#user.id"),
    evict = @CacheEvict(cacheNames = "user:list", allEntries = true)
)
public UserVO updateUser(User user) {
    userMapper.updateById(user);
    return BeanUtils.toBean(user, UserVO.class);
}
```

---

## 使用方式

### 1. 基础使用

```java
@Service
public class UserService {
    
    // 查询缓存
    @Cacheable(cacheNames = "user", key = "#id")
    public UserVO getUser(Long id) {
        return userMapper.selectById(id);
    }
    
    // 更新缓存
    @CachePut(cacheNames = "user", key = "#user.id")
    public UserVO updateUser(User user) {
        userMapper.updateById(user);
        return BeanUtils.toBean(user, UserVO.class);
    }
    
    // 删除缓存
    @CacheEvict(cacheNames = "user", key = "#id")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}
```

### 2. 自定义 Key

```java
// 使用方法参数
@Cacheable(cacheNames = "user", key = "#id")
public UserVO getUser(Long id) { }

// 使用方法参数的属性
@Cacheable(cacheNames = "order", key = "#order.userId")
public OrderVO getOrder(Order order) { }

// 使用多个参数
@Cacheable(cacheNames = "user", key = "#id + '_' + #type")
public UserVO getUser(Long id, String type) { }

// 使用表达式
@Cacheable(cacheNames = "user", key = "#root.methodName + '_' + #id")
public UserVO getUser(Long id) { }
```

### 3. 条件缓存

```java
// 只有当条件为 true 时才缓存
@Cacheable(cacheNames = "user", key = "#id", condition = "#id > 0")
public UserVO getUser(Long id) { }

// 除非条件为 true，否则缓存
@Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
public UserVO getUser(Long id) { }
```

### 4. 清除所有缓存

```java
// 清除指定 cacheNames 的所有缓存
@CacheEvict(cacheNames = "user", allEntries = true)
public void clearAllUserCache() { }
```

---

## 自定义过期时间

框架支持通过 `cacheNames#ttl` 格式设置自定义过期时间。

### 时间单位

| 后缀 | 单位 | 示例 |
|-----|------|-----|
| d | 天 | `7d` = 7天 |
| h | 小时 | `24h` = 24小时 |
| m | 分钟 | `30m` = 30分钟 |
| s | 秒 | `60s` = 60秒 |

### 使用示例

```java
// 7天过期
@Cacheable(cacheNames = "user#7d", key = "#id")
public UserVO getUser(Long id) { }

// 30分钟过期
@Cacheable(cacheNames = "sms#30m", key = "#mobile")
public String getSmsCode(String mobile) { }

// 1小时过期
@Cacheable(cacheNames = "token#1h", key = "#token")
public LoginUser getLoginUser(String token) { }

// 5分钟过期
@Cacheable(cacheNames = "captcha#5m", key = "#uuid")
public String getCaptcha(String uuid) { }
```

---

## 完整示例

### 用户服务

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    // 缓存7天
    @Cacheable(cacheNames = "user#7d", key = "#id")
    public UserVO getUser(Long id) {
        log.info("查询用户: {}", id);
        return BeanUtils.toBean(userMapper.selectById(id), UserVO.class);
    }
    
    // 更新缓存
    @CachePut(cacheNames = "user#7d", key = "#user.id")
    public UserVO updateUser(User user) {
        userMapper.updateById(user);
        return BeanUtils.toBean(user, UserVO.class);
    }
    
    // 删除缓存
    @CacheEvict(cacheNames = "user#7d", key = "#id")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    // 清除所有用户缓存
    @CacheEvict(cacheNames = "user#7d", allEntries = true)
    public void clearUserCache() {
        log.info("清除所有用户缓存");
    }
}
```

### 验证码服务

```java
@Service
@RequiredArgsConstructor
public class CaptchaService {
    
    // 验证码5分钟过期
    @Cacheable(cacheNames = "captcha#5m", key = "#uuid")
    public String getCaptcha(String uuid) {
        return generateCaptcha();
    }
    
    // 验证成功后删除
    @CacheEvict(cacheNames = "captcha#5m", key = "#uuid")
    public void verifyCaptcha(String uuid, String code) {
        // 验证逻辑
    }
}
```

### 登录 Token 服务

```java
@Service
@RequiredArgsConstructor
public class TokenService {
    
    // Token 7天过期
    @Cacheable(cacheNames = "token#7d", key = "#token")
    public LoginUser getLoginUser(String token) {
        return tokenStore.get(token);
    }
    
    // 更新 Token 缓存
    @CachePut(cacheNames = "token#7d", key = "#token")
    public LoginUser updateLoginUser(String token, LoginUser loginUser) {
        tokenStore.put(token, loginUser);
        return loginUser;
    }
    
    // 删除 Token
    @CacheEvict(cacheNames = "token#7d", key = "#token")
    public void deleteToken(String token) {
        tokenStore.remove(token);
    }
}
```

---

## 注意事项

### 1. 自调用问题

```java
@Service
public class UserService {
    
    public UserVO getUserById(Long id) {
        return this.internalGetUser(id);  // 自调用，缓存不生效
    }
    
    @Cacheable(cacheNames = "user", key = "#id")
    public UserVO internalGetUser(Long id) {
        return userMapper.selectById(id);
    }
}
```

解决方案：
```java
@Autowired
private ApplicationContext applicationContext;

public UserVO getUserById(Long id) {
    UserService userService = applicationContext.getBean(UserService.class);
    return userService.internalGetUser(id);  // 通过代理调用，缓存生效
}
```

### 2. 返回值必须可序列化

```java
// ✅ 正确：返回值可以被 JSON 序列化
@Cacheable(cacheNames = "user", key = "#id")
public UserVO getUser(Long id) { }

// ❌ 错误：返回值包含不可序列化的对象
@Cacheable(cacheNames = "user", key = "#id")
public User getUserWithSession(Long id) { }  // 如果 User 包含 Session 等不可序列化对象
```

### 3. 异常处理

```java
// 如果方法抛出异常，不会缓存结果
@Cacheable(cacheNames = "user", key = "#id")
public UserVO getUser(Long id) {
    User user = userMapper.selectById(id);
    if (user == null) {
        throw new RuntimeException("用户不存在");  // 异常不缓存
    }
    return BeanUtils.toBean(user, UserVO.class);
}
```
