# 忽略租户指南

## 概述

`@TenantIgnore` 注解用于标记指定方法或类不进行租户过滤。

## @TenantIgnore 注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TenantIgnore {
    
    // 是否开启忽略租户（支持 SpEL）
    String enable() default "true";
}
```

## 使用方式

### 1. 方法级别

```java
@Service
public class TenantService {
    
    // 忽略租户过滤
    @TenantIgnore
    public List<TenantDO> getAllTenants() {
        return tenantMapper.selectList(null);
    }
    
    // 忽略租户过滤
    @TenantIgnore
    public TenantDO getTenantById(Long id) {
        return tenantMapper.selectById(id);
    }
}
```

### 2. 类级别

```java
// 整个类的所有方法都忽略租户
@TenantIgnore
@Service
public class SystemConfigService {
    
    public List<SystemConfigDO> listAll() {
        return configMapper.selectList(null);
    }
    
    public SystemConfigDO getByKey(String key) {
        return configMapper.selectByKey(key);
    }
}
```

### 3. 实体类级别

```java
// 实体类标记 @TenantIgnore，对应的表自动忽略租户
@TenantIgnore
@Data
@TableName("system_tenant")
public class TenantDO extends BaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
}
```

### 4. Controller 级别

```java
// Controller 标记 @TenantIgnore，对应的 URL 自动添加到 ignoreUrls
@TenantIgnore
@RestController
@RequestMapping("/admin-api/tenant")
public class TenantController {
    
    @GetMapping("/list")
    public List<TenantVO> list() {
        return tenantService.list();
    }
}
```

### 5. 条件忽略

```java
@Service
public class UserService {
    
    // 条件忽略：只在特定条件下忽略租户
    @TenantIgnore(enable = "#forceAll == true")
    public List<UserDO> listUsers(boolean forceAll) {
        return userMapper.selectList(null);
    }
}
```

---

## 使用场景

### 场景1：租户管理

```java
@Service
public class TenantService {
    
    @TenantIgnore
    public List<TenantDO> listAll() {
        // 查询所有租户，需要忽略租户过滤
        return tenantMapper.selectList(null);
    }
    
    @TenantIgnore
    public TenantDO getById(Long id) {
        return tenantMapper.selectById(id);
    }
    
    public void create(TenantCreateDTO dto) {
        TenantDO tenant = BeanUtils.toBean(dto, TenantDO.class);
        tenantMapper.insert(tenant);
    }
}
```

### 场景2：系统配置

```java
@TenantIgnore
@Service
public class SystemConfigService {
    
    public SystemConfigDO getByKey(String key) {
        return configMapper.selectByKey(key);
    }
    
    public void updateByKey(String key, String value) {
        configMapper.updateByKey(key, value);
    }
}
```

### 场景3：字典数据

```java
@TenantIgnore
@Service
public class DictDataService {
    
    public List<DictDataDO> listByType(String dictType) {
        return dictDataMapper.selectByType(dictType);
    }
}
```

---

## 配置文件方式

除了使用 `@TenantIgnore` 注解，还可以通过配置文件忽略：

```yaml
yudao:
  tenant:
    ignore-tables:                          # 忽略租户的表
      - system_tenant
      - system_tenant_package
      - system_dict_type
      - system_dict_data
      - system_config
      - system_menu
    ignore-urls:                            # 忽略租户的 URL
      - /admin-api/system/tenant/**
      - /admin-api/system/dict/**
    ignore-caches:                          # 忽略租户的缓存
      - system_dict
      - system_config
```

---

## 注意事项

### 1. 优先级

```java
// 配置文件 > 注解
// 如果表在 ignore-tables 中配置了，即使没有 @TenantIgnore 也会忽略
```

### 2. 继承性

```java
// @TenantIgnore 支持继承
@TenantIgnore
public abstract class BaseService {
    // 子类也会继承 @TenantIgnore
}
```

### 3. SpEL 表达式

```java
// 支持 SpEL 条件判断
@TenantIgnore(enable = "#id == 1L")
public UserDO getUser(Long id) {
    return userMapper.selectById(id);
}
```
