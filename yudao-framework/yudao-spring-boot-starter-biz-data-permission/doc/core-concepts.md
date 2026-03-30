# 核心概念

## 数据权限概述

数据权限是指在数据访问层进行的权限控制，确保用户只能访问其有权限的数据。

### 与功能权限的区别

| 类型 | 控制层面 | 示例 |
|-----|---------|------|
| 功能权限 | 能否访问某个功能 | 用户能否访问订单列表 |
| 数据权限 | 能看到哪些数据 | 用户能看到哪些订单 |

### 常见的数据权限模式

| 模式 | 说明 | 适用场景 |
|-----|------|---------|
| 全部数据 | 可以查看所有数据 | 超级管理员 |
| 本部门数据 | 只能查看本部门的数据 | 部门经理 |
| 本部门及以下 | 可以查看本部门及下级部门的数据 | 区域经理 |
| 仅本人数据 | 只能查看自己创建的数据 | 普通员工 |

---

## 核心组件

### 1. DataPermissionRule

数据权限规则接口，定义如何生成过滤条件。

```java
public interface DataPermissionRule {
    
    /**
     * 返回需要生效的表名
     */
    Set<String> getTableNames();
    
    /**
     * 根据表名和别名，生成过滤条件
     */
    Expression getExpression(String tableName, Alias tableAlias);
}
```

**工作流程：**
1. `getTableNames()` 返回需要应用权限的表名
2. `getExpression()` 为匹配的表生成 WHERE 条件

---

### 2. DataPermissionRuleFactory

规则工厂，管理所有数据权限规则。

```java
public interface DataPermissionRuleFactory {
    
    List<DataPermissionRule> getDataPermissionRules();
    
    List<DataPermissionRule> getDataPermissionRule(String mappedStatementId);
}
```

---

### 3. DataPermissionRuleHandler

MyBatis Plus 处理器，在 SQL 执行前拦截并重写 SQL。

```java
public class DataPermissionRuleHandler implements MultiDataPermissionHandler {
    
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 1. 获取 Mapper 对应的规则
        // 2. 检查表名是否匹配
        // 3. 生成过滤条件
        // 4. 返回条件表达式
    }
}
```

---

### 4. @DataPermission

数据权限注解，用于控制数据权限的启用和规则。

```java
@DataPermission(enable = true, excludeRules = {CustomRule.class})
public List<OrderDO> getOrderList() {
    return orderMapper.selectList(null);
}
```

---

## SQL 重写原理

### 原始 SQL

```sql
SELECT * FROM sys_user WHERE status = 1
```

### 重写后的 SQL

```sql
SELECT * FROM sys_user WHERE dept_id IN (1, 2, 3) AND status = 1
```

### 重写流程

```
1. MyBatis Plus DataPermissionInterceptor 拦截 SQL
2. 解析 SQL，获取表名
3. 调用 DataPermissionRuleHandler.getSqlSegment()
4. 遍历所有 DataPermissionRule
5. 如果表名匹配，调用 getExpression() 生成条件
6. 将条件添加到 SQL 的 WHERE 子句
```

---

## 权限上下文

### LoginUser 上下文

数据权限基于当前登录用户，通过 `SecurityFrameworkUtils.getLoginUser()` 获取：

```java
// 获取当前用户
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

// 获取用户的数据权限
DeptDataPermissionRespDTO permission = loginUser.getContext("DeptDataPermission", 
    DeptDataPermissionRespDTO.class);
```

### 权限类型

| 类型 | 说明 |
|-----|------|
| ALL | 查看全部数据 |
| DEPT | 查看指定部门数据 |
| DEPT_AND_CHILD | 查看本部门及下级部门数据 |
| SELF | 仅查看自己创建的数据 |

---

## 依赖关系

```
DataPermissionAnnotationAdvisor
        │
        ↓
DataPermissionContextHolder (ThreadLocal)
        │
        ↓
DataPermissionRuleHandler (MyBatis Plus 插件)
        │
        ↓
DataPermissionRuleFactory
        │
        ↓
DataPermissionRule[]
        │
        ├── DeptDataPermissionRule (内置)
        └── CustomRule (自定义)
```
