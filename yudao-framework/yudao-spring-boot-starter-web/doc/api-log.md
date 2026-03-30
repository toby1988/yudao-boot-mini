# API 访问日志指南

## 概述

`@ApiAccessLog` 注解用于记录 API 接口的访问日志。

## @ApiAccessLog 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAccessLog {
    
    boolean enable() default true;                // 是否启用
    boolean requestEnable() default true;         // 是否记录请求参数
    boolean responseEnable() default false;       // 是否记录响应结果
    String[] sanitizeKeys() default {};            // 敏感字段
    
    String operateModule() default "";            // 操作模块
    String operateName() default "";              // 操作名称
    OperateTypeEnum[] operateType() default {};   // 操作类型
}
```

## 使用方式

### 1. 基础使用

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @ApiAccessLog(operateModule = "用户管理", operateName = "用户列表")
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
}
```

### 2. 记录响应结果

```java
@ApiAccessLog(
    operateModule = "用户管理", 
    operateName = "用户详情",
    responseEnable = true  // 记录响应结果
)
@GetMapping("/{id}")
public UserVO detail(@PathVariable Long id) {
    return userService.get(id);
}
```

### 3. 忽略请求参数

```java
@ApiAccessLog(
    operateModule = "文件管理", 
    operateName = "文件上传",
    requestEnable = false  // 不记录请求参数（文件上传场景）
)
@PostMapping("/upload")
public String upload(@RequestParam MultipartFile file) {
    return fileService.upload(file);
}
```

### 4. 敏感字段脱敏

```java
@ApiAccessLog(
    operateModule = "用户管理", 
    operateName = "用户登录",
    sanitizeKeys = {"password", "captcha"}  // 不记录这些字段
)
@PostMapping("/login")
public TokenVO login(@RequestBody LoginDTO dto) {
    return authService.login(dto);
}
```

### 5. 自动从 Swagger 获取

```java
// 如果 operateModule 和 operateName 为空，会自动从 Swagger 注解获取
@Tag(name = "用户管理")  // 对应 operateModule
@ApiAccessLog
public class UserController {
    
    @Operation(summary = "用户列表")  // 对应 operateName
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
}
```

## 操作类型

```java
public enum OperateTypeEnum implements ArrayValuable<Integer> {
    
    GET(1, "查询"),
    CREATE(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    EXPORT(5, "导出"),
    IMPORT(6, "导入"),
    ;
}
```

## 日志字段

| 字段 | 说明 |
|-----|------|
| traceId | 链路追踪 ID |
| userId | 用户 ID |
| userType | 用户类型 |
| operateModule | 操作模块 |
| operateName | 操作名称 |
| operateType | 操作类型 |
| requestMethod | 请求方法 |
| requestUrl | 请求地址 |
| requestParams | 请求参数 |
| responseData | 响应结果 |
| userIp | 用户 IP |
| userAgent | 用户代理 |
| startTime | 开始时间 |
| endTime | 结束时间 |
| duration | 执行时长 |
| resultCode | 结果状态码 |
| resultMsg | 结果消息 |
