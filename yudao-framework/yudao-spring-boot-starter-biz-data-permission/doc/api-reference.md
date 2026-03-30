# API 参考

## 注解

### @DataPermission

数据权限注解，用于控制数据权限的启用和规则。

| 属性 | 类型 | 默认值 | 说明 |
|-----|------|-------|------|
| enable | boolean | true | 是否开启数据权限 |
| includeRules | Class[] | {} | 生效的规则数组（优先级高） |
| excludeRules | Class[] | {} | 排除的规则数组 |

---

## 接口

### DataPermissionRule

数据权限规则接口。

| 方法 | 返回类型 | 说明 |
|-----|---------|------|
| getTableNames() | Set\<String\> | 返回需要生效的表名 |
| getExpression(String, Alias) | Expression | 生成过滤条件表达式 |

### DataPermissionRuleFactory

数据权限规则工厂接口。

| 方法 | 返回类型 | 说明 |
|-----|---------|------|
| getDataPermissionRules() | List\<DataPermissionRule\> | 获取所有规则 |
| getDataPermissionRule(String) | List\<DataPermissionRule\> | 获取指定 Mapper 的规则 |

---

## 实现类

### DeptDataPermissionRule

部门数据权限规则实现。

| 方法 | 说明 |
|-----|------|
| addDeptColumn(Class) | 为实体类添加部门字段（默认 dept_id） |
| addDeptColumn(Class, String) | 为实体类添加部门字段（指定字段名） |
| addDeptColumn(String, String) | 为表名添加部门字段 |
| addUserColumn(Class) | 为实体类添加用户字段（默认 user_id） |
| addUserColumn(Class, String) | 为实体类添加用户字段（指定字段名） |
| addUserColumn(String, String) | 为表名添加用户字段 |

---

## 工具类

### DataPermissionUtils

数据权限工具类。

| 方法 | 返回类型 | 说明 |
|-----|---------|------|
| executeIgnore(Runnable) | void | 忽略权限执行逻辑 |
| executeIgnore(Callable\<T\>) | T | 忽略权限执行并返回结果 |
| addDisableDataPermission() | void | 添加禁用权限到上下文 |
| removeDataPermission() | void | 移除权限上下文 |

---

## 配置类

### YudaoDataPermissionAutoConfiguration

自动配置类，创建以下 Bean：

| Bean | 类型 | 说明 |
|-----|------|------|
| dataPermissionRuleFactory | DataPermissionRuleFactory | 规则工厂 |
| dataPermissionRuleHandler | DataPermissionRuleHandler | MyBatis Plus 处理器 |
| dataPermissionAnnotationAdvisor | DataPermissionAnnotationAdvisor | AOP 切面 |

---

## 常量

### DeptDataPermissionRule

```java
// 上下文 Key
protected static final String CONTEXT_KEY = "DeptDataPermissionRule";

// 默认字段名
private static final String DEPT_COLUMN_NAME = "dept_id";
private static final String USER_COLUMN_NAME = "user_id";
```

---

## DTO 类

### DeptDataPermissionRespDTO

部门数据权限响应 DTO。

| 字段 | 类型 | 说明 |
|-----|------|------|
| all | Boolean | 是否查看全部数据 |
| deptIds | Set\<Long\> | 可访问的部门 ID 列表 |
| self | Boolean | 是否只查看自己的数据 |
