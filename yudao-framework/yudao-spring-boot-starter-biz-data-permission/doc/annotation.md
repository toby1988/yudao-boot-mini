# @DataPermission 注解使用指南

## 概述

`@DataPermission` 注解用于控制数据权限的启用和规则配置。

## 注解定义

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    
    /**
     * 是否开启数据权限（默认 true）
     */
    boolean enable() default true;
    
    /**
     * 生效的数据权限规则数组（优先级高于 excludeRules）
     */
    Class<? extends DataPermissionRule>[] includeRules() default {};
    
    /**
     * 排除的数据权限规则数组
     */
    Class<? extends DataPermissionRule>[] excludeRules() default {};
}
```

## 使用方式

### 1. 禁用数据权限

#### 方法级别

```java
@DataPermission(enable = false)
public List<UserDO> selectAllUsers() {
    return userMapper.selectList(null);
}
```

#### 类级别

```java
@DataPermission(enable = false)
public interface SystemConfigMapper extends BaseMapperX<SystemConfigDO> {
    // 整个 Mapper 禁用数据权限
}
```

---

### 2. 排除特定规则

```java
// 排除部门数据权限规则
@DataPermission(excludeRules = {DeptDataPermissionRule.class})
public List<OrderDO> getOrderList() {
    return orderMapper.selectList(null);
}

// 排除多个规则
@DataPermission(excludeRules = {
    DeptDataPermissionRule.class,
    CustomerDataPermissionRule.class
})
public List<DataDO> getDataList() {
    return dataMapper.selectList(null);
}
```

---

### 3. 指定生效规则

```java
// 只应用指定的规则
@DataPermission(includeRules = {OwnerDataPermissionRule.class})
public List<OrderDO> getMyOrders() {
    return orderMapper.selectList(null);
}
```

---

## 使用场景

### 场景1：系统管理页面

```java
@Service
public class SystemUserService {
    
    @DataPermission(enable = false)
    public List<UserDO> getAllUsers() {
        // 系统管理页面需要查看所有用户
        return userMapper.selectList(null);
    }
}
```

### 场景2：数据导出

```java
@Service
public class DataExportService {
    
    @DataPermission(enable = false)
    public void exportAllOrders(OutputStream os) {
        // 导出所有数据（由操作员自行判断权限）
        List<OrderDO> orders = orderMapper.selectList(null);
        ExcelUtils.export(os, orders, OrderDO.class);
    }
    
    // 或者只导出有权限的数据
    public void exportAllowedOrders(OutputStream os) {
        // 自动应用数据权限
        List<OrderDO> orders = orderMapper.selectList(null);
        ExcelUtils.export(os, orders, OrderDO.class);
    }
}
```

### 场景3：统计报表

```java
@Service
public class ReportService {
    
    // 统计报表：只看自己部门的数据
    public ReportVO getDeptReport() {
        List<OrderDO> orders = orderMapper.selectList(null);
        return calculateReport(orders);
    }
    
    // 管理报表：查看所有数据
    @DataPermission(enable = false)
    public ReportVO getManagerReport() {
        List<OrderDO> orders = orderMapper.selectList(null);
        return calculateReport(orders);
    }
}
```

---

## 注解优先级

### 优先级顺序

1. **方法级别注解** > 类级别注解
2. **includeRules** > excludeRules

### 示例

```java
// 类级别：禁用数据权限
@DataPermission(enable = false)
@Service
public class OrderService {
    
    // 方法级别优先：启用数据权限
    @DataPermission(enable = true)
    public List<OrderDO> getOrdersWithPermission() {
        return orderMapper.selectList(null);  // 应用权限
    }
    
    // 继承类级别：禁用数据权限
    public List<OrderDO> getAllOrders() {
        return orderMapper.selectList(null);  // 不应用权限
    }
}
```

---

## 工具类使用

### DataPermissionUtils

用于在代码中动态忽略数据权限：

```java
// 忽略数据权限执行（Runnable）
DataPermissionUtils.executeIgnore(() -> {
    List<UserDO> users = userMapper.selectList(null);
    // 处理逻辑
});

// 忽略数据权限执行（Callable）
List<UserDO> users = DataPermissionUtils.executeIgnore(() -> {
    return userMapper.selectList(null);
});
```

### 使用场景

```java
@Service
public class DataService {
    
    public void syncData() {
        // 同步数据时需要查看所有数据
        List<OrderDO> allOrders = DataPermissionUtils.executeIgnore(() -> {
            return orderMapper.selectList(null);
        });
        
        // 处理数据...
    }
    
    public void generateReport() {
        // 生成报表时忽略权限
        DataPermissionUtils.executeIgnore(() -> {
            // 报表逻辑...
        });
    }
}
```

---

## 常见问题

### Q: 注解不生效？

A: 检查以下几点：
1. 确认 `DataPermissionAnnotationAdvisor` 已创建
2. 方法必须是 public
3. 不能是内部调用（同一类内调用）

### Q: 什么时候使用 enable=false？

A: 适用场景：
1. 系统管理功能
2. 数据导出
3. 定时任务
4. 数据同步

### Q: includeRules 和 excludeRules 冲突？

A: 优先级：
1. includeRules 生效时，只应用指定规则
2. includeRules 为空时，应用 excludeRules
