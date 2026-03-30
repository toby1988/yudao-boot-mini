# 字段处理器指南

## 概述

MyBatis 模块提供了多种字段处理器，用于自动处理字段值。

## DefaultDBFieldHandler - 自动填充

### 功能

自动填充 BaseDO 的通用字段：
- 插入时：createTime、updateTime、creator、updater
- 更新时：updateTime、updater

### 工作原理

```java
public class DefaultDBFieldHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充 createTime、updateTime
        // 填充 creator、updater（从登录用户获取）
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充 updateTime
        // 填充 updater（从登录用户获取）
    }
}
```

### 使用方式

无需额外配置，框架自动注册。

---

## TypeHandler - 类型处理器

### IntegerListTypeHandler

将 `List<Integer>` 转换为逗号分隔的字符串存储。

```java
@Data
@TableName("sys_config")
public class ConfigDO extends BaseDO {
    
    @TableField(typeHandler = IntegerListTypeHandler.class)
    private List<Integer> values;  // 数据库存储: "1,2,3"
}
```

### LongListTypeHandler

将 `List<Long>` 转换为逗号分隔的字符串存储。

```java
@Data
@TableName("sys_role_menu")
public class RoleMenuDO extends BaseDO {
    
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> menuIds;  // 数据库存储: "1,2,3"
}
```

### LongSetTypeHandler

将 `Set<Long>` 转换为逗号分隔的字符串存储。

```java
@Data
@TableName("sys_user_role")
public class UserRoleDO extends BaseDO {
    
    @TableField(typeHandler = LongSetTypeHandler.class)
    private Set<Long> roleIds;  // 数据库存储: "1,2,3"
}
```

### StringListTypeHandler

将 `List<String>` 转换为逗号分隔的字符串存储。

```java
@Data
@TableName("sys_dict_data")
public class DictDataDO extends BaseDO {
    
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tags;  // 数据库存储: "tag1,tag2,tag3"
}
```

---

## 完整示例

### 角色菜单关联

```java
@Data
@TableName("sys_role")
public class RoleDO extends BaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> menuIds;  // 菜单 ID 列表
    
    @TableField(typeHandler = IntegerListTypeHandler.class)
    private List<Integer> dataScopes;  // 数据权限范围
}
```

### 用户配置

```java
@Data
@TableName("sys_user_config")
public class UserConfigDO extends BaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> favoriteMenus;  // 收藏的菜单
    
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> recentVisitIds;  // 最近访问的 ID
}
```

---

## 注意事项

### 1. 查询包含 TypeHandler 的字段

```java
// 查询时会自动转换类型
RoleDO role = roleMapper.selectById(1L);
List<Long> menuIds = role.getMenuIds();  // 自动转换为 List<Long>
```

### 2. 空值处理

```java
// 空列表会存储为空字符串
role.setMenuIds(Collections.emptyList());
// 数据库存储: ""
```

### 3. 性能考虑

```java
// TypeHandler 会增加一定的性能开销
// 建议只在需要时使用，不要滥用
```
