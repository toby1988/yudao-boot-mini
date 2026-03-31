# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-biz-tenant</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
yudao:
  tenant:
    enable: true                          # 是否开启多租户
    ignore-urls:                          # 忽略租户的 URL
      - /admin-api/system/auth/login
      - /admin-api/infra/file/get/**
    ignore-tables:                        # 忽略租户的表
      - system_tenant
      - system_tenant_package
    ignore-caches:                        # 忽略租户的缓存
      - system_dict
```

## 3. 实体类继承 TenantBaseDO

```java
@Data
@TableName("system_user")
public class UserDO extends TenantBaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String username;
    
    private String mobile;
}
```

## 4. 获取租户上下文

```java
@Service
public class UserService {
    
    public void someMethod() {
        // 获取当前租户 ID
        Long tenantId = TenantContextHolder.getTenantId();
        
        // 获取租户 ID（必填）
        Long tenantId = TenantContextHolder.getRequiredTenantId();
    }
}
```

## 5. 切换租户执行

```java
@Service
public class DataService {
    
    public void syncData() {
        // 在指定租户下执行
        TenantUtils.execute(1L, () -> {
            // 这里的代码会在租户 1 的上下文中执行
            userMapper.selectList(null);
        });
        
        // 忽略租户执行
        TenantUtils.executeIgnore(() -> {
            // 这里的代码会忽略租户过滤
            userMapper.selectList(null);
        });
    }
}
```

## 6. 忽略租户

```java
@Service
public class SystemService {
    
    // 忽略租户过滤
    @TenantIgnore
    public List<TenantDO> getAllTenants() {
        return tenantMapper.selectList(null);
    }
}
```
