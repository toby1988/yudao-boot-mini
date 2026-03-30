# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-security</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
yudao:
  security:
    token-header: Authorization    # Token 请求头
    token-parameter: token         # Token 参数名
    permit-all-urls:               # 免认证 URL
      - /admin-api/system/captcha/**
      - /admin-api/system/auth/login
      - /admin-api/system/auth/refresh-token
    password-encoder-length: 4     # BCrypt 密码加密复杂度
```

## 3. 获取当前用户

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/info")
    public UserVO getCurrentUser() {
        // 获取当前登录用户 ID
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        
        // 获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        
        return userService.getUser(userId);
    }
}
```

## 4. 权限校验

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    // 需要 user:list 权限
    @PreAuthorize("@ss.hasPermission('user:list')")
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
    
    // 需要 user:create 权限
    @PreAuthorize("@ss.hasPermission('user:create')")
    @PostMapping("/create")
    public void create(@RequestBody UserCreateDTO dto) {
        userService.create(dto);
    }
}
```

## 5. 免认证配置

```java
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    
    // 使用 @PermitAll 注解，无需认证
    @PermitAll
    @GetMapping("/get")
    public CaptchaVO getCaptcha() {
        return captchaService.getCaptcha();
    }
}
```

## 6. 操作日志

```java
@OperateLog(module = "用户管理", type = OperateTypeEnum.CREATE)
@PostMapping("/create")
public void create(@RequestBody UserCreateDTO dto) {
    userService.create(dto);
}
```
