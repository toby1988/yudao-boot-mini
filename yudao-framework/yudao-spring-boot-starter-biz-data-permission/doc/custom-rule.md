# 自定义数据权限规则

## 概述

除了内置的 `DeptDataPermissionRule`，框架支持自定义数据权限规则。

## 实现步骤

### 1. 创建规则类

实现 `DataPermissionRule` 接口：

```java
@Component
public class CustomerDataPermissionRule implements DataPermissionRule {
    
    private static final String TABLE_NAME = "biz_customer";
    private static final String COLUMN_NAME = "owner_id";
    
    @Override
    public Set<String> getTableNames() {
        return Collections.singleton(TABLE_NAME);
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        
        // 生成过滤条件：WHERE owner_id = 当前用户ID
        return new EqualsTo(
            MyBatisUtils.buildColumn(tableName, tableAlias, COLUMN_NAME),
            new LongValue(loginUser.getId())
        );
    }
}
```

### 2. 自动注册

规则会自动注册到 Spring 容器，无需额外配置。

---

## 常见场景

### 场景1：基于用户的所有权

```java
@Component
public class OwnerDataPermissionRule implements DataPermissionRule {
    
    private final Set<String> TABLE_NAMES = Set.of("biz_order", "biz_contract");
    
    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        
        // WHERE creator_id = 当前用户ID
        return new EqualsTo(
            MyBatisUtils.buildColumn(tableName, tableAlias, "creator_id"),
            new LongValue(loginUser.getId())
        );
    }
}
```

### 场景2：基于业务属性的权限

```java
@Component
public class ProductCategoryDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Set<String> getTableNames() {
        return Collections.singleton("biz_product");
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 获取用户可访问的商品分类
        Set<Long> allowedCategoryIds = getAllowCategoryIds();
        if (allowedCategoryIds.isEmpty()) {
            return null;
        }
        
        // WHERE category_id IN (1, 2, 3)
        return new InExpression(
            MyBatisUtils.buildColumn(tableName, tableAlias, "category_id"),
            new ParenthesedExpressionList(
                new ExpressionList<>(
                    allowedCategoryIds.stream()
                        .map(LongValue::new)
                        .collect(Collectors.toList())
                )
            )
        );
    }
    
    private Set<Long> getAllowCategoryIds() {
        // 从缓存或数据库获取用户可访问的分类
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        return categoryService.getAllowCategoryIds(loginUser.getId());
    }
}
```

### 场景3：多条件组合

```java
@Component
public class ComplexDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Set<String> getTableNames() {
        return Collections.singleton("biz_sensitive_data");
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        
        // 条件1：dept_id IN (1, 2, 3)
        Expression deptExpression = new InExpression(
            MyBatisUtils.buildColumn(tableName, tableAlias, "dept_id"),
            new ParenthesedExpressionList(
                new ExpressionList<>(
                    getDeptIds().stream()
                        .map(LongValue::new)
                        .collect(Collectors.toList())
                )
            )
        );
        
        // 条件2：security_level <= 用户安全等级
        Expression levelExpression = new LessThanEquals(
            MyBatisUtils.buildColumn(tableName, tableAlias, "security_level"),
            new LongValue(loginUser.getSecurityLevel())
        );
        
        // 组合：WHERE (dept_id IN (1,2,3) AND security_level <= 3)
        return new ParenthesedExpressionList(
            new AndExpression(deptExpression, levelExpression)
        );
    }
}
```

---

## 高级用法

### 1. 动态表名

```java
@Component
public class DynamicTableDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Set<String> getTableNames() {
        // 动态返回需要权限控制的表
        return Set.of(
            "biz_order_2024",
            "biz_order_2025",
            "biz_order_2026"
        );
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 为每个表生成相同的权限条件
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        
        return new EqualsTo(
            MyBatisUtils.buildColumn(tableName, tableAlias, "user_id"),
            new LongValue(loginUser.getId())
        );
    }
}
```

### 2. 条件性禁用

```java
@Component
public class ConditionalDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Set<String> getTableNames() {
        return Collections.singleton("biz_report");
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        
        // 检查是否需要应用权限
        if (!needApplyPermission(loginUser)) {
            return null;  // 不添加条件
        }
        
        // 添加权限条件
        return buildPermissionExpression(tableName, tableAlias, loginUser);
    }
    
    private boolean needApplyPermission(LoginUser loginUser) {
        // 超级管理员不需要权限
        if (loginUser.getRoles().contains("SUPER_ADMIN")) {
            return false;
        }
        return true;
    }
}
```

### 3. 自定义表达式

```java
@Component
public class CustomExpressionDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Set<String> getTableNames() {
        return Collections.singleton("biz_data");
    }
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 使用 JSqlParser 构建复杂表达式
        // WHERE (create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY))
        
        String column = MyBatisUtils.buildColumn(tableName, tableAlias, "create_time").toString();
        String sql = column + " >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
        
        // 或者直接构建 Expression 对象
        return new GreaterThanEquals(
            MyBatisUtils.buildColumn(tableName, tableAlias, "create_time"),
            new Function("DATE_SUB", new ExpressionList<>(
                new Function("NOW"),
                new IntervalExpression(30, "DAY")
            ))
        );
    }
}
```

---

## 最佳实践

### 1. 规则命名

```java
// ✅ 推荐：清晰的命名
public class OrderDeptDataPermissionRule implements DataPermissionRule { }
public class ProductOwnerDataPermissionRule implements DataPermissionRule { }

// ❌ 不推荐：模糊的命名
public class MyRule implements DataPermissionRule { }
public class Rule1 implements DataPermissionRule { }
```

### 2. 表名配置

```java
// ✅ 推荐：使用常量
private static final Set<String> TABLE_NAMES = Set.of("biz_order", "biz_contract");

@Override
public Set<String> getTableNames() {
    return TABLE_NAMES;
}

// ❌ 不推荐：每次创建新集合
@Override
public Set<String> getTableNames() {
    return Set.of("biz_order", "biz_contract");
}
```

### 3. 空值处理

```java
@Override
public Expression getExpression(String tableName, Alias tableAlias) {
    LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
    if (loginUser == null) {
        return null;  // 未登录，不添加条件
    }
    
    // 检查是否有权限数据
    DeptDataPermissionRespDTO permission = getPermission(loginUser);
    if (permission == null) {
        return null;
    }
    
    // 检查是否查看全部
    if (Boolean.TRUE.equals(permission.getAll())) {
        return null;  // 查看全部，不添加条件
    }
    
    // 构建权限条件
    return buildExpression(permission);
}
```

---

## 调试技巧

### 1. 打印生成的 SQL

```yaml
# application.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2. 日志输出

```java
@Slf4j
public class MyDataPermissionRule implements DataPermissionRule {
    
    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        log.debug("[getExpression][表名: {}, 用户: {}]", tableName, loginUser);
        
        Expression expression = buildExpression(loginUser);
        log.debug("[getExpression][生成条件: {}]", expression);
        
        return expression;
    }
}
```
