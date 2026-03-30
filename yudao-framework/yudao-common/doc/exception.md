# 异常体系指南

## 概述

框架提供统一的异常处理机制，包括业务异常、错误码定义等。

## 核心类

### ServiceException - 业务异常

```java
// 抛出业务异常
throw new ServiceException(ErrorCode.USER_NOT_FOUND);

// 带消息
throw new ServiceException(1002001000, "用户不存在");

// 带格式化消息
throw new ServiceException(1002001000, String.format("用户 %s 不存在", username));
```

### ErrorCode - 错误码接口

```java
public interface ErrorCode {
    
    Integer getCode();  // 错误码
    String getMsg();    // 错误信息
}
```

## 使用方式

### 1. 定义错误码

```java
public interface UserErrorCodeConstants {
    
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1002001000, "用户不存在");
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1002001001, "用户名已存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1002001002, "手机号已存在");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode(1002001003, "密码校验失败");
    ErrorCode USER_STATUS_CLOSED = new ErrorCode(1002001004, "用户已被禁用");
}
```

### 2. 抛出异常

```java
@Service
public class UserService {
    
    public void createUser(UserCreateDTO dto) {
        // 检查用户名是否重复
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new ServiceException(UserErrorCodeConstants.USER_USERNAME_EXISTS);
        }
        
        // 检查手机号是否重复
        if (userMapper.selectByMobile(dto.getMobile()) != null) {
            throw new ServiceException(UserErrorCodeConstants.USER_MOBILE_EXISTS);
        }
        
        // 创建用户
        User user = BeanUtils.toBean(dto, User.class);
        userMapper.insert(user);
    }
}
```

### 3. 全局异常处理

`GlobalExceptionHandler` 会自动捕获异常并返回统一格式：

```json
{
    "code": 1002001000,
    "msg": "用户不存在",
    "data": null
}
```

## 错误码规范

### 编码规则

```
1  0  0  2  0  0  1  0  0  0
└──┴──┴──┴──┴──┘  └──┴──┘  └──┘
   系统模块    业务模块  错误序号
```

| 位置 | 说明 |
|-----|------|
| 1-2 | 系统标识（10=通用，20=业务） |
| 3-5 | 模块标识 |
| 6-7 | 业务模块 |
| 8-10 | 错误序号 |

### 错误码示例

```java
// 通用错误码
public interface GlobalErrorCodeConstants {
    
    ErrorCode SUCCESS = new ErrorCode(0, "成功");
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "未登录");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求地址不存在");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode(429, "请求过于频繁");
    ErrorCode REPEATED_REQUESTS = new ErrorCode(429, "重复请求，请稍后重试");
}

// 用户模块错误码
public interface UserErrorCodeConstants {
    
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1002001000, "用户不存在");
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1002001001, "用户名已存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1002001002, "手机号已存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1002001003, "邮箱已存在");
}
```

## 常见问题

### Q: 如何忽略某些异常的日志？

A: 在 `GlobalExceptionHandler.IGNORE_ERROR_MESSAGES` 中添加：

```java
public static final Set<String> IGNORE_ERROR_MESSAGES = SetUtils.asSet(
    "无效的刷新令牌",
    "你的异常信息"
);
```

### Q: 如何自定义异常响应？

A: 使用 `@RestControllerAdvice` 创建自定义异常处理器：

```java
@RestControllerAdvice
public class CustomExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public CommonResult<?> handleCustomException(CustomException e) {
        return CommonResult.error(e.getCode(), e.getMessage());
    }
}
```
