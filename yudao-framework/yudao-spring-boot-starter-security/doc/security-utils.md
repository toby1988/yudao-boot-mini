# SecurityFrameworkUtils 使用指南

## 概述

`SecurityFrameworkUtils` 是安全服务工具类，提供获取当前用户信息、权限校验等功能。

## API 方法

### 获取用户信息

```java
// 获取当前登录用户
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

// 获取用户 ID
Long userId = SecurityFrameworkUtils.getLoginUserId();

// 获取用户昵称
String nickname = SecurityFrameworkUtils.getLoginUserNickname();

// 获取部门 ID
Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();
```

### 获取认证信息

```java
// 获取 Authentication
Authentication authentication = SecurityFrameworkUtils.getAuthentication();

// 获取 Token
String token = SecurityFrameworkUtils.obtainAuthorization(
    request, 
    "Authorization",  // Header 名称
    "token"           // 参数名
);
```

### 设置用户信息

```java
// 设置登录用户到上下文
SecurityFrameworkUtils.setLoginUser(loginUser, request);
```

### 权限判断

```java
// 是否跳过权限校验（跨租户场景）
boolean skip = SecurityFrameworkUtils.skipPermissionCheck();
```

## 使用方式

### 获取当前用户

```java
@Service
public class UserService {
    
    public UserVO getCurrentUser() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return getUser(userId);
    }
    
    public String getCurrentUserNickname() {
        return SecurityFrameworkUtils.getLoginUserNickname();
    }
    
    public Long getCurrentUserDeptId() {
        return SecurityFrameworkUtils.getLoginUserDeptId();
    }
}
```

### 权限校验

```java
@Service
public class DataPermissionService {
    
    public boolean hasPermission(String permission) {
        // 是否跳过权限校验（跨租户访问）
        if (SecurityFrameworkUtils.skipPermissionCheck()) {
            return true;
        }
        
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        
        // 检查权限
        return loginUser.getScopes().contains(permission);
    }
}
```

### Token 解析

```java
@Component
public class TokenParser {
    
    public String parseToken(HttpServletRequest request) {
        // 从 Header 或 Parameter 获取 Token
        return SecurityFrameworkUtils.obtainAuthorization(
            request,
            "Authorization",
            "token"
        );
    }
}
```

## 常见问题

### Q: 获取用户 ID 为 null？

A: 检查是否已登录，或 Token 是否有效：

```java
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
if (loginUser == null) {
    // 未登录或 Token 无效
    throw new ServiceException(UNAUTHORIZED);
}
```

### Q: 跨租户访问如何处理？

A: 使用 `skipPermissionCheck()` 判断：

```java
if (SecurityFrameworkUtils.skipPermissionCheck()) {
    // 跨租户访问，跳过权限校验
    return true;
}
```
