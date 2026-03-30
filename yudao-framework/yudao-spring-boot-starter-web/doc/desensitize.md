# 数据脱敏指南

## 概述

数据脱敏模块提供敏感数据的自动脱敏功能，支持多种脱敏方式。

## 内置脱敏注解

### @MobileDesensitize - 手机号脱敏

```java
@Data
public class UserVO {
    
    @MobileDesensitize
    private String mobile;  // 13248765917 → 132****5917
}
```

**属性：**
- `prefixKeep`: 前缀保留长度（默认 3）
- `suffixKeep`: 后缀保留长度（默认 4）
- `replacer`: 替换字符（默认 *）

### @IdCardDesensitize - 身份证脱敏

```java
@Data
public class UserVO {
    
    @IdCardDesensitize
    private String idCard;  // 110101199001011234 → 110***********1234
}
```

### @ChineseNameDesensitize - 姓名脱敏

```java
@Data
public class UserVO {
    
    @ChineseNameDesensitize
    private String name;  // 张三 → 张*
}
```

### @BankCardDesensitize - 银行卡脱敏

```java
@Data
public class UserVO {
    
    @BankCardDesensitize
    private String bankCard;  // 6222021234567890123 → 6222**********0123
}
```

### @EmailDesensitize - 邮箱脱敏

```java
@Data
public class UserVO {
    
    @EmailDesensitize
    private String email;  // zhangsan@example.com → z***@example.com
}
```

### @PasswordDesensitize - 密码脱敏

```java
@Data
public class UserVO {
    
    @PasswordDesensitize
    private String password;  // 123456 → ******
}
```

### @FixedPhoneDesensitize - 固定电话脱敏

```java
@Data
public class UserVO {
    
    @FixedPhoneDesensitize
    private String phone;  // 010-12345678 → 010-****5678
}
```

### @CarLicenseDesensitize - 车牌号脱敏

```java
@Data
public class UserVO {
    
    @CarLicenseDesensitize
    private String carLicense;  // 京A12345 → 京A***45
}
```

## 自定义脱敏

### 1. 创建脱敏处理器

```java
public class CustomDesensitizationHandler implements DesensitizationHandler {
    
    @Override
    public String desensitize(String origin) {
        if (origin == null || origin.length() < 4) {
            return origin;
        }
        // 自定义脱敏逻辑
        return origin.substring(0, 2) + "***" + origin.substring(origin.length() - 2);
    }
}
```

### 2. 创建脱敏注解

```java
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = CustomDesensitizationHandler.class)
public @interface CustomDesensitize {
}
```

### 3. 使用自定义注解

```java
@Data
public class UserVO {
    
    @CustomDesensitize
    private String customField;
}
```

## 条件脱敏

### 使用 disable 属性

```java
@Data
public class UserVO {
    
    @MobileDesensitize(disable = "@securityFrameworkService.hasPermission('user:mobile:view')")
    private String mobile;
}
```

## 完整示例

```java
@Data
public class UserVO {
    
    private Long id;
    
    @ChineseNameDesensitize
    private String name;
    
    @MobileDesensitize
    private String mobile;
    
    @IdCardDesensitize
    private String idCard;
    
    @EmailDesensitize
    private String email;
    
    @BankCardDesensitize
    private String bankCard;
}
```

**输出示例：**
```json
{
    "id": 1,
    "name": "张*",
    "mobile": "132****5917",
    "idCard": "110***********1234",
    "email": "z***@example.com",
    "bankCard": "6222**********0123"
}
```
