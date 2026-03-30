# BaseDO 使用指南

## 概述

`BaseDO` 是所有实体类的基类，定义了通用字段。

## 字段定义

```java
@Data
public abstract class BaseDO implements Serializable, TransPojo {
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;    // 创建时间
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;    // 更新时间
    
    @TableField(fill = FieldFill.INSERT)
    private String creator;              // 创建人 ID
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;              // 更新人 ID
    
    @TableLogic
    private Boolean deleted;             // 逻辑删除标记
}
```

## 使用方式

### 1. 实体类继承 BaseDO

```java
@Data
@TableName("sys_user")
public class UserDO extends BaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String username;
    
    private String mobile;
    
    private Integer status;
}
```

### 2. 字段自动填充

```java
// 插入时自动填充
userMapper.insert(user);
// 自动填充：createTime、updateTime、creator、updater

// 更新时自动填充
userMapper.updateById(user);
// 自动填充：updateTime、updater
```

### 3. 逻辑删除

```java
// 逻辑删除
userMapper.deleteById(1L);
// 执行 SQL: UPDATE sys_user SET deleted = true WHERE id = 1

// 查询时自动过滤已删除的数据
List<UserDO> users = userMapper.selectList(null);
// 执行 SQL: SELECT * FROM sys_user WHERE deleted = false
```

### 4. 清除自动填充字段

```java
// 避免前端直接传递自动填充字段被更新
public void update(UserUpdateDTO dto) {
    UserDO user = BeanUtils.toBean(dto, UserDO.class);
    user.clean();  // 清除 creator、createTime、updater、updateTime
    userMapper.updateById(user);
}
```

---

## 字段说明

### createTime - 创建时间

- **类型**：`LocalDateTime`
- **填充时机**：INSERT
- **说明**：记录创建时间，自动填充

### updateTime - 更新时间

- **类型**：`LocalDateTime`
- **填充时机**：INSERT、UPDATE
- **说明**：记录最后更新时间，自动填充

### creator - 创建人

- **类型**：`String`
- **填充时机**：INSERT
- **说明**：记录创建人 ID，自动从登录用户获取

### updater - 更新人

- **类型**：`String`
- **填充时机**：INSERT、UPDATE
- **说明**：记录最后更新人 ID，自动从登录用户获取

### deleted - 逻辑删除

- **类型**：`Boolean`
- **默认值**：`false`
- **说明**：逻辑删除标记，删除时自动设置为 `true`

---

## 注意事项

### 1. 手动设置值

```java
// 如果手动设置了值，自动填充不会覆盖
UserDO user = new UserDO();
user.setCreateTime(LocalDateTime.now());  // 手动设置
userMapper.insert(user);
// 不会被自动填充覆盖
```

### 2. 无登录用户场景

```java
// 定时任务等场景，没有登录用户，creator 和 updater 为空
// 需要手动设置
UserDO user = new UserDO();
user.setCreator("system");
user.setUpdater("system");
userMapper.insert(user);
```

### 3. 继承 BaseDO 的注意事项

```java
@Data
@TableName("sys_user")
public class UserDO extends BaseDO {
    // 确保继承了 BaseDO 的所有字段
    // 数据库表需要对应的字段
}
```

---

## 数据库表结构

```sql
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态',
  -- BaseDO 字段
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```
