# 通用 POJO 指南

## 概述

框架提供统一的 POJO 类，包括返回结果、分页参数等。

## CommonResult - 统一返回结果

### 定义

```java
@Data
public class CommonResult<T> implements Serializable {
    
    private Integer code;  // 错误码
    private String msg;    // 错误信息
    private T data;        // 数据
}
```

### 使用方式

```java
@RestController
public class UserController {
    
    // 成功返回
    @GetMapping("/user/{id}")
    public CommonResult<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.get(id);
        return CommonResult.success(user);
    }
    
    // 成功返回（无数据）
    @PostMapping("/user/create")
    public CommonResult<Long> createUser(@RequestBody UserCreateDTO dto) {
        Long id = userService.create(dto);
        return CommonResult.success(id);
    }
    
    // 错误返回
    @GetMapping("/user/error")
    public CommonResult<?> error() {
        return CommonResult.error(UserErrorCodeConstants.USER_NOT_EXISTS);
    }
}
```

### API 方法

```java
// 成功
CommonResult.success()                    // 无数据
CommonResult.success(data)                // 带数据

// 错误
CommonResult.error(code, msg)             // 错误码+消息
CommonResult.error(errorCode)             // ErrorCode 对象
CommonResult.error(BAD_REQUEST)           // 全局错误码
```

### 响应格式

```json
// 成功
{
    "code": 0,
    "msg": "ok",
    "data": {
        "id": 1,
        "name": "admin"
    }
}

// 错误
{
    "code": 1002001000,
    "msg": "用户不存在",
    "data": null
}
```

---

## PageResult - 分页结果

### 定义

```java
@Data
public class PageResult<T> implements Serializable {
    
    private Long total;  // 总记录数
    private List<T> list;  // 数据列表
}
```

### 使用方式

```java
@GetMapping("/user/list")
public CommonResult<PageResult<UserVO>> list(UserPageReqVO req) {
    PageResult<UserVO> result = userService.page(req);
    return CommonResult.success(result);
}
```

### API 方法

```java
// 创建分页结果
PageResult<UserVO> result = new PageResult<>(list, total);

// 获取分页参数
PageResult<User> page = userMapper.selectPage(req);
```

---

## PageParam - 分页参数

### 定义

```java
@Data
public class PageParam {
    
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = 1;  // 页码
    
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    private Integer pageSize = 10;  // 每页条数
}
```

### 使用方式

```java
@Data
public class UserPageReqVO extends PageParam {
    
    private String username;
    private Integer status;
}

@GetMapping("/user/list")
public CommonResult<PageResult<UserVO>> list(@Valid UserPageReqVO req) {
    // req 包含 pageNo、pageSize
    return CommonResult.success(userService.page(req));
}
```

---

## SortParam - 排序参数

### 定义

```java
@Data
public class SortParam {
    
    private String orderBy;   // 排序字段
    private String orderDir;  // 排序方向（ASC/DESC）
}
```

---

## 总结

| 类 | 用途 |
|---|------|
| `CommonResult<T>` | 统一返回结果 |
| `PageResult<T>` | 分页结果 |
| `PageParam` | 分页参数 |
| `SortParam` | 排序参数 |
| `ErrorCode` | 错误码接口 |
