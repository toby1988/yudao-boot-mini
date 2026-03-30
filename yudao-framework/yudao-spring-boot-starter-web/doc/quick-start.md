# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-web</artifactId>
</dependency>
```

## 2. 基础配置

```yaml
# application.yml
yudao:
  xss:
    enable: true              # XSS 防护开关
    exclude-urls:             # 排除的 URL
      - /admin-api/**
  api:
    access-log:
      enable: true            # API 日志开关
  swagger:
    title: 项目 API 文档
    description: 项目接口文档
    version: 1.0.0
```

## 3. 全局异常处理

框架自动处理所有异常，返回统一格式：

```json
{
    "code": 500,
    "msg": "系统异常",
    "data": null
}
```

## 4. API 访问日志

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

## 5. 数据脱敏

```java
@Data
public class UserVO {
    
    @MobileDesensitize  // 手机号脱敏：132****5917
    private String mobile;
    
    @IdCardDesensitize  // 身份证脱敏：110***********1234
    private String idCard;
    
    @ChineseNameDesensitize  // 姓名脱敏：张**
    private String name;
}
```

## 6. XSS 防护

```java
@Data
public class ArticleDTO {
    
    @XssClean  // 自动清理 XSS 代码
    private String content;
}
```

## 7. 验证

启动应用后：
- 访问 `/doc.html` 查看 Swagger 文档
- 调用接口，查看异常处理和日志记录效果
