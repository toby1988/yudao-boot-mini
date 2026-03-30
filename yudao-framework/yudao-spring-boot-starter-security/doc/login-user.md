# LoginUser 使用指南

## 概述

`LoginUser` 是登录用户信息类，存储当前用户的上下文信息。

## 字段定义

```java
@Data
public class LoginUser {
    
    // ========== 基础信息 ==========
    private Long id;                    // 用户编号
    private Integer userType;           // 用户类型（管理员/会员）
    private Map<String, String> info;   // 额外信息（昵称、部门ID等）
    private Long tenantId;              // 租户编号
    private List<String> scopes;        // OAuth2 授权范围
    private LocalDateTime expiresTime;  // 过期时间
    
    // ========== 上下文 ==========
    private Map<String, Object> context;  // 临时缓存
    private Long visitTenantId;           // 访问的租户编号
}
```

## 使用方式

### 获取 LoginUser

```java
// 获取当前登录用户
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

// 获取用户 ID
Long userId = loginUser.getId();

// 获取用户类型
Integer userType = loginUser.getUserType();

// 获取租户 ID
Long tenantId = loginUser.getTenantId();

// 获取昵称
String nickname = loginUser.getInfo().get("nickname");

// 获取部门 ID
String deptId = loginUser.getInfo().get("deptId");
```

### 使用 Context 缓存

```java
// 设置上下文
loginUser.setContext("key", value);

// 获取上下文
Object value = loginUser.getContext("key", Object.class);

// 带类型获取
DeptDataPermissionRespDTO permission = loginUser.getContext(
    "DeptDataPermission", 
    DeptDataPermissionRespDTO.class
);
```

## 使用场景

### 场景1：获取用户部门

```java
public Long getUserDeptId() {
    LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
    if (loginUser == null) {
        return null;
    }
    return MapUtil.getLong(loginUser.getInfo(), LoginUser.INFO_KEY_DEPT_ID);
}
```

### 场景2：跨租户访问判断

```java
public boolean canCrossTenantAccess() {
    LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
    if (loginUser == null) {
        return false;
    }
    // 跨租户访问时，visitTenantId 不等于 tenantId
    return ObjUtil.notEqual(loginUser.getVisitTenantId(), loginUser.getTenantId());
}
```

### 场景3：数据权限缓存

```java
// 在数据权限规则中，缓存权限数据到 LoginUser
public DeptDataPermissionRespDTO getDeptDataPermission() {
    LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
    
    // 先从缓存获取
    DeptDataPermissionRespDTO permission = loginUser.getContext(
        "DeptDataPermission", 
        DeptDataPermissionRespDTO.class
    );
    
    if (permission == null) {
        // 缓存没有，查询并设置
        permission = permissionApi.getDeptDataPermission(loginUser.getId());
        loginUser.setContext("DeptDataPermission", permission);
    }
    
    return permission;
}
```

## 注意事项

### 1. 线程安全

```java
// LoginUser 使用 ThreadLocal 存储，线程安全
// 每个请求独立的 LoginUser 实例
```

### 2. 序列化

```java
// context 字段使用 @JsonIgnore，不会序列化
// 避免在 JSON 传输时包含临时缓存数据
```

### 3. 空值处理

```java
// 获取 LoginUser 可能为 null（未登录）
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
if (loginUser == null) {
    throw new ServiceException(UNAUTHORIZED);
}
```
