# XSS 防护指南

## 概述

XSS（Cross-Site Scripting）防护模块用于防止跨站脚本攻击。

## 工作原理

```
请求到达
    │
    ↓
XssFilter 拦截
    │
    ├── 请求参数清理
    │       └── XssRequestWrapper.getParameter()
    │
    └── 请求体清理
            └── @RequestBody 通过 XssStringJsonDeserializer
```

## 配置

```yaml
yudao:
  xss:
    enable: true                    # 是否启用
    exclude-urls:                   # 排除的 URL
      - /admin-api/system/notice/** # 公告内容允许 HTML
    exclude-parameters:             # 排除的参数名
      - content                     # 内容字段允许 HTML
```

## 使用方式

### 1. 自动清理（默认）

所有请求参数和 JSON 字段都会自动清理。

```java
@PostMapping("/user/create")
public void create(@RequestBody UserCreateDTO dto) {
    // dto 中的字符串字段会自动清理 XSS
    userService.create(dto);
}
```

### 2. 排除某些字段

```java
@Data
public class ArticleDTO {
    
    private String title;
    
    @JsonIgnore  // 不清理
    private String content;
}
```

### 3. 排除某些 URL

```yaml
yudao:
  xss:
    exclude-urls:
      - /admin-api/system/notice/**
```

## XssCleaner 接口

### 默认实现

```java
public class JsoupXssCleaner implements XssCleaner {
    
    @Override
    public String clean(String html) {
        // 使用 Jsoup 清理 HTML
        return Jsoup.clean(html, Whitelist.relaxed());
    }
}
```

### 自定义实现

```java
@Component
public class CustomXssCleaner implements XssCleaner {
    
    @Override
    public String clean(String html) {
        // 自定义清理逻辑
        return html.replaceAll("<script>.*?</script>", "");
    }
}
```

## 清理规则

### 默认白名单

```java
Whitelist.relaxed()
    .addTags("img")                           // 允许 img 标签
    .addAttributes("img", "src", "width", "height")
    .addProtocols("img", "src", "http", "https", "data")
```

### 自定义白名单

```java
@Configuration
public class XssConfig {
    
    @Bean
    public XssCleaner xssCleaner() {
        return html -> {
            // 自定义白名单
            Whitelist whitelist = Whitelist.relaxed()
                .addTags("video", "source")
                .addAttributes("video", "src", "controls");
            
            return Jsoup.clean(html, whitelist);
        };
    }
}
```

## 常见问题

### Q: 为什么某些 HTML 标签被清除了？

A: 默认只允许安全的 HTML 标签。如需允许更多标签，自定义 `XssCleaner`。

### Q: 如何完全关闭 XSS 防护？

```yaml
yudao:
  xss:
    enable: false
```

### Q: 排除特定字段不清理？

A: 在 `exclude-parameters` 中配置，或使用 `@JsonIgnore` 注解。
