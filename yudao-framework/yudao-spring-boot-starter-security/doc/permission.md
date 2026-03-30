# 权限校验指南

## 概述

框架支持基于 `@PreAuthorize` 注解的功能权限校验。

## 使用方式

### 1. 基础权限校验

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    // 需要 user:list 权限
    @PreAuthorize("@ss.hasPermission('user:list')")
    @GetMapping("/list")
    public List<UserVO> list() {
        return userService.list();
    }
}
```

### 2. 多权限校验

```java
// 需要 user:create 或 user:update 权限
@PreAuthorize("@ss.hasAnyPermissions('user:create', 'user:update')")
@PostMapping("/save")
public void save(@RequestBody UserSaveDTO dto) {
    userService.save(dto);
}
```

### 3. 角色校验

```java
// 需要 ADMIN 角色
@PreAuthorize("@ss.hasRole('ADMIN')")
@GetMapping("/admin/data")
public DataVO getAdminData() {
    return dataService.getAdminData();
}

// 需要 ADMIN 或 MANAGER 角色
@PreAuthorize("@ss.hasAnyRoles('ADMIN', 'MANAGER')")
@GetMapping("/manager/data")
public DataVO getManagerData() {
    return dataService.getManagerData();
}
```

### 4. OAuth2 授权范围

```java
// 需要 user:read 授权范围
@PreAuthorize("@ss.hasScope('user:read')")
@GetMapping("/api/user/{id}")
public UserVO getUser(@PathVariable Long id) {
    return userService.get(id);
}
```

---

## SecurityFrameworkService

框架提供的权限服务接口：

```java
public interface SecurityFrameworkService {
    
    // 是否有权限
    boolean hasPermission(String permission);
    
    // 是否有任一权限
    boolean hasAnyPermissions(String... permissions);
    
    // 是否有角色
    boolean hasRole(String role);
    
    // 是否有任一角色
    boolean hasAnyRoles(String... roles);
    
    // 是否有授权范围
    boolean hasScope(String scope);
    
    // 是否有任一授权范围
    boolean hasAnyScopes(String... scopes);
}
```

---

## 完整示例

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @PreAuthorize("@ss.hasPermission('user:list')")
    @GetMapping("/list")
    public PageResult<UserVO> list(UserPageReqVO req) {
        return userService.page(req);
    }
    
    @PreAuthorize("@ss.hasPermission('user:create')")
    @PostMapping("/create")
    public Long create(@RequestBody @Valid UserCreateDTO dto) {
        return userService.create(dto);
    }
    
    @PreAuthorize("@ss.hasPermission('user:update')")
    @PutMapping("/update")
    public void update(@RequestBody @Valid UserUpdateDTO dto) {
        userService.update(dto);
    }
    
    @PreAuthorize("@ss.hasPermission('user:delete')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
    
    @PreAuthorize("@ss.hasPermission('user:export')")
    @GetMapping("/export")
    public void export(UserExportReqVO req, HttpServletResponse response) {
        userService.export(req, response);
    }
}
```

---

## 注意事项

### 1. Bean 名称

```java
// 使用 @ss 作为 Bean 名号
@PreAuthorize("@ss.hasPermission('user:list')")

// 对应的 Bean 定义
@Service("ss")
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {
    // ...
}
```

### 2. 跨租户场景

```java
// 跨租户访问时，权限校验会自动跳过
if (SecurityFrameworkUtils.skipPermissionCheck()) {
    return true;  // 跳过权限校验
}
```

### 3. 未登录处理

```java
// 未登录时，权限校验返回 false
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
if (loginUser == null) {
    return false;
}
```
