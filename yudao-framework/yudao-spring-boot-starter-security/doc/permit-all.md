# 免认证配置指南

## 概述

框架支持多种方式配置免认证的 URL。

## 配置方式

### 1. @PermitAll 注解

在 Controller 方法或类上添加 `@PermitAll` 注解：

```java
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    
    // 方法级别
    @PermitAll
    @GetMapping("/get")
    public CaptchaVO getCaptcha() {
        return captchaService.getCaptcha();
    }
}
```

```java
// 类级别：整个 Controller 的所有接口都免认证
@PermitAll
@RestController
@RequestMapping("/api/public")
public class PublicController {
    
    @GetMapping("/data")
    public DataVO getData() {
        return dataService.getPublicData();
    }
}
```

### 2. 配置文件

```yaml
yudao:
  security:
    permit-all-urls:
      - /admin-api/system/captcha/**
      - /admin-api/system/auth/login
      - /admin-api/system/auth/refresh-token
      - /admin-api/infra/file/get/**
```

### 3. 自定义配置器

实现 `AuthorizeRequestsCustomizer` 接口：

```java
@Component
public class CustomAuthorizeRequestsCustomizer implements AuthorizeRequestsCustomizer {
    
    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/api/public/**").permitAll();
    }
}
```

---

## 配置优先级

```
1. @PermitAll 注解
   ↓
2. yudao.security.permit-all-urls 配置
   ↓
3. AuthorizeRequestsCustomizer 自定义
   ↓
4. 兜底规则：anyRequest().authenticated()
```

---

## 使用场景

### 场景1：登录接口

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PermitAll
    @PostMapping("/login")
    public TokenVO login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
    
    @PermitAll
    @PostMapping("/refresh-token")
    public TokenVO refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }
}
```

### 场景2：验证码接口

```java
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    
    @PermitAll
    @GetMapping("/get")
    public CaptchaVO getCaptcha() {
        return captchaService.getCaptcha();
    }
}
```

### 场景3：第三方回调

```java
@RestController
@RequestMapping("/api/third-party")
public class ThirdPartyController {
    
    @PermitAll
    @PostMapping("/callback/{type}")
    public void callback(@PathVariable String type, @RequestBody CallbackDTO dto) {
        thirdPartyService.handleCallback(type, dto);
    }
}
```

### 场景4：静态资源

```yaml
yudao:
  security:
    permit-all-urls:
      - /statics/**
      - /doc.html
      - /swagger-ui/**
      - /webjars/**
```

---

## 注意事项

### 1. 路径匹配

```yaml
# 支持 Ant 风格路径
yudao:
  security:
    permit-all-urls:
      - /api/public/**        # 匹配所有子路径
      - /api/user/list        # 精确匹配
      - /api/user/*/detail    # 匹配单层路径
```

### 2. 请求方法

```java
// @PermitAll 默认支持所有请求方法
@PermitAll
@RequestMapping("/api/data")  // GET, POST, PUT, DELETE 都免认证
public void handleData() { }
```

### 3. 优先级

```java
// 配置文件的优先级高于 @PermitAll
// 如果配置文件中排除了某个路径，即使有 @PreAuthorize 也会跳过认证
```
