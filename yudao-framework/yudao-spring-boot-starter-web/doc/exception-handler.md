# 全局异常处理指南

## 概述

`GlobalExceptionHandler` 统一处理所有异常，返回 `CommonResult` 格式的响应。

## 处理的异常类型

| 异常类型 | 状态码 | 说明 |
|---------|--------|------|
| `ServiceException` | 业务错误码 | 业务异常 |
| `MethodArgumentNotValidException` | 400 | 参数校验失败 |
| `BindException` | 400 | 参数绑定失败 |
| `ConstraintViolationException` | 400 | 参数约束违反 |
| `MissingServletRequestParameterException` | 400 | 参数缺失 |
| `HttpRequestMethodNotSupportedException` | 405 | 请求方法不支持 |
| `NoHandlerFoundException` | 404 | 请求地址不存在 |
| `AccessDeniedException` | 403 | 权限不足 |
| `MaxUploadSizeExceededException` | 400 | 文件过大 |
| `Exception` | 500 | 系统异常 |

## 使用方式

### 1. 抛出业务异常

```java
@Service
public class UserService {
    
    public UserVO getUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(USER_NOT_FOUND);
        }
        return BeanUtils.toBean(user, UserVO.class);
    }
}
```

### 2. 参数校验异常

```java
@Data
public class UserCreateDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}

@PostMapping("/user/create")
public void create(@RequestBody @Valid UserCreateDTO dto) {
    userService.create(dto);
}
```

### 3. 自定义错误码

```java
public interface UserErrorCodeConstants {
    
    ErrorCode USER_NOT_FOUND = new ErrorCode(1002001000, "用户不存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1002001001, "手机号已存在");
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1002001002, "用户名已存在");
}
```

## 响应格式

### 成功响应

```json
{
    "code": 0,
    "msg": "ok",
    "data": {
        "id": 1,
        "username": "admin"
    }
}
```

### 异常响应

```json
{
    "code": 1002001000,
    "msg": "用户不存在",
    "data": null
}
```

### 参数校验异常响应

```json
{
    "code": 400,
    "msg": "请求参数不正确:用户名不能为空",
    "data": null
}
```

## 常见问题

### Q: 如何忽略某些异常的日志？

```java
// 在 GlobalExceptionHandler.IGNORE_ERROR_MESSAGES 中添加
public static final Set<String> IGNORE_ERROR_MESSAGES = SetUtils.asSet(
    "无效的刷新令牌",
    "你的异常信息"
);
```

### Q: 如何自定义异常处理？

```java
@RestControllerAdvice
public class CustomExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public CommonResult<?> handleCustomException(CustomException e) {
        return CommonResult.error(e.getCode(), e.getMessage());
    }
}
```
