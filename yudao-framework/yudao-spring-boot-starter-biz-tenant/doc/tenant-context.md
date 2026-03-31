# 租户上下文指南

## 概述

`TenantContextHolder` 是租户上下文管理器，使用 ThreadLocal 存储当前租户 ID。

## API 方法

### TenantContextHolder

```java
// 获取租户 ID
Long tenantId = TenantContextHolder.getTenantId();

// 获取租户 ID（必填，不存在则抛异常）
Long tenantId = TenantContextHolder.getRequiredTenantId();

// 设置租户 ID
TenantContextHolder.setTenantId(1L);

// 是否忽略租户
boolean ignore = TenantContextHolder.isIgnore();

// 设置忽略租户
TenantContextHolder.setIgnore(true);

// 清除上下文
TenantContextHolder.clear();
```

### TenantUtils

```java
// 在指定租户下执行（Runnable）
TenantUtils.execute(1L, () -> {
    // 租户 1 的上下文
    userMapper.selectList(null);
});

// 在指定租户下执行（Callable）
UserVO user = TenantUtils.execute(1L, () -> {
    return userService.getUser(1L);
});

// 忽略租户执行（Runnable）
TenantUtils.executeIgnore(() -> {
    // 忽略租户过滤
    userMapper.selectList(null);
});

// 忽略租户执行（Callable）
List<UserDO> users = TenantUtils.executeIgnore(() -> {
    return userMapper.selectList(null);
});
```

---

## 使用方式

### 1. 获取当前租户

```java
@Service
public class UserService {
    
    public UserVO getCurrentUser() {
        Long tenantId = TenantContextHolder.getTenantId();
        return getUserByTenant(tenantId);
    }
    
    public UserVO getCurrentUserRequired() {
        // 获取租户 ID，不存在则抛异常
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        return getUserByTenant(tenantId);
    }
}
```

### 2. 切换租户

```java
@Service
public class DataService {
    
    public void syncDataToTenant(Long targetTenantId) {
        // 切换到目标租户执行
        TenantUtils.execute(targetTenantId, () -> {
            // 这里会使用 targetTenantId 的上下文
            dataService.processData();
        });
    }
}
```

### 3. 忽略租户

```java
@Service
public class AdminService {
    
    public List<TenantDO> getAllTenants() {
        // 忽略租户过滤，查询所有租户的数据
        return TenantUtils.executeIgnore(() -> {
            return tenantMapper.selectList(null);
        });
    }
}
```

### 4. 批量处理所有租户

```java
@Service
public class SyncService {
    
    @Autowired
    private TenantMapper tenantMapper;
    
    public void syncAllTenants() {
        // 获取所有租户
        List<TenantDO> tenants = TenantUtils.executeIgnore(() -> {
            return tenantMapper.selectList(null);
        });
        
        // 遍历每个租户执行
        for (TenantDO tenant : tenants) {
            TenantUtils.execute(tenant.getId(), () -> {
                // 在每个租户的上下文中执行
                doSync();
            });
        }
    }
}
```

---

## 注意事项

### 1. 线程安全

```java
// 使用 TransmittableThreadLocal，支持线程池传递
private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();
```

### 2. 清理上下文

```java
// 框架会在请求结束时自动清理
// 如果手动设置，需要确保清理
try {
    TenantContextHolder.setTenantId(1L);
    // 业务逻辑
} finally {
    TenantContextHolder.clear();  // 重要！
}
```

### 3. 异步任务

```java
// 异步任务需要手动传递租户 ID
@Async
public void asyncProcess(Long tenantId) {
    TenantContextHolder.setTenantId(tenantId);
    try {
        // 异步处理逻辑
    } finally {
        TenantContextHolder.clear();
    }
}
```
