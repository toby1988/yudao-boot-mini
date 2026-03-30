# Swagger 文档指南

## 概述

Swagger 模块基于 Knife4j + SpringDoc 实现 API 文档自动生成。

## 配置

```yaml
springdoc:
  api-docs:
    enabled: true                  # 是否启用
  swagger-ui:
    enabled: true                  # Swagger UI 开启

yudao:
  swagger:
    title: 项目 API 文档
    description: 项目接口文档
    version: 1.0.0
    contact-name: 芋道源码
    contact-url: https://www.iocoder.cn
    contact-email: 7685413@qq.com
    license-name: MIT License
    license-url: https://www.iocoder.cn
    base-packages:                 # 扫描的包路径
      - cn.iocoder.yudao
    path-patterns:                 # 匹配的路径
      - /admin-api/**
      - /app-api/**
```

## 访问地址

| 地址 | 说明 |
|-----|------|
| `/doc.html` | Knife4j UI |
| `/swagger-ui.html` | Swagger UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/v3/api-docs.yaml` | OpenAPI YAML |

## 使用方式

### 1. 控制器注解

```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Operation(summary = "用户列表", description = "获取用户列表")
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
    
    @Operation(summary = "用户详情", description = "根据ID获取用户详情")
    @GetMapping("/{id}")
    public UserVO detail(@Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.get(id);
    }
}
```

### 2. DTO 注解

```java
@Data
@Schema(description = "用户创建 DTO")
public class UserCreateDTO {
    
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

### 3. VO 注解

```java
@Data
@Schema(description = "用户 VO")
public class UserVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "手机号")
    private String mobile;
}
```

### 4. 枚举注解

```java
@Getter
@AllArgsConstructor
@Schema(description = "用户状态")
public enum UserStatusEnum {
    
    @Schema(description = "正常")
    NORMAL(0),
    
    @Schema(description = "停用")
    DISABLE(1);
    
    private final Integer status;
}
```

### 5. 忽略接口

```java
@Hidden  // 忽略整个控制器
@RestController
@RequestMapping("/internal")
public class InternalController {
    
    @Hidden  // 忽略单个接口
    @GetMapping("/debug")
    public void debug() {
        // 内部接口，不暴露给前端
    }
}
```

## 常用注解

| 注解 | 说明 |
|-----|------|
| `@Tag` | 控制器分组 |
| `@Operation` | 接口描述 |
| `@Parameter` | 参数描述 |
| `@Schema` | 模型/字段描述 |
| `@Hidden` | 隐藏接口 |
| `@ApiResponse` | 响应描述 |

## 完整示例

```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Operation(summary = "创建用户", description = "创建新用户")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @PostMapping("/create")
    public void create(@RequestBody @Valid UserCreateDTO dto) {
        userService.create(dto);
    }
    
    @Operation(summary = "用户列表", description = "分页获取用户列表")
    @GetMapping("/list")
    public PageResult<UserVO> list(@Valid UserPageReqVO req) {
        return userService.page(req);
    }
}
```

## 常见问题

### Q: 文档地址 404？

A: 检查以下配置：
```yaml
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
```

### Q: 接口没有显示？

A: 检查包路径配置：
```yaml
yudao:
  swagger:
    base-packages:
      - cn.iocoder.yudao
```

### Q: 生产环境关闭文档？

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```
