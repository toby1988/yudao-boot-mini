# 数据翻译指南

## 概述

数据翻译基于 Easy-Trans 实现，可以将 ID 翻译为对应的名称。

## 使用方式

### 1. 简单翻译

```java
@Data
public class UserVO implements VO {
    
    private Long id;
    
    private String username;
    
    @TransType(type = TransType.SIMPLE, targetClassName = "cn.iocoder.yudao.system.dal.dataobject.dept.DeptDO", fields = "name")
    private Long deptId;  // 翻译为部门名称
    
    private String deptName;  // 部门名称（翻译结果）
}
```

### 2. 字典翻译

```java
@Data
public class UserVO implements VO {
    
    @TransType(type = TransType.DICTIONARY, targetClassName = "sys_user_sex")
    private Integer sex;  // 翻译为字典标签
    
    private String sexName;  // 性别名称（翻译结果）
}
```

### 3. 远程翻译

```java
@Data
public class OrderVO implements VO {
    
    @TransType(type = TransType.REMOTE, targetClassName = "user", fields = "name")
    private Long userId;  // 远程调用获取用户名
    
    private String userName;  // 用户名（翻译结果）
}
```

---

## TranslateUtils

当 `@TransType` 注解无法满足需求时，可以使用 `TranslateUtils` 手动触发翻译。

```java
public List<UserVO> listUsers() {
    List<UserVO> users = userMapper.selectList();
    
    // 手动触发翻译
    TranslateUtils.translate(users);
    
    return users;
}
```

---

## 使用场景

### 场景1：部门名称翻译

```java
@Data
public class UserVO implements VO {
    
    private Long id;
    private String username;
    
    @TransType(type = TransType.SIMPLE, 
               targetClassName = "cn.iocoder.yudao.system.dal.dataobject.dept.DeptDO", 
               fields = "name")
    private Long deptId;
    
    private String deptName;  // 自动填充
}
```

**结果：**
```json
{
    "id": 1,
    "username": "admin",
    "deptId": 100,
    "deptName": "研发部"
}
```

### 场景2：字典值翻译

```java
@Data
public class UserVO implements VO {
    
    @TransType(type = TransType.DICTIONARY, targetClassName = "sys_user_sex")
    private Integer sex;
    
    private String sexName;  // 自动填充
    
    @TransType(type = TransType.DICTIONARY, targetClassName = "sys_common_status")
    private Integer status;
    
    private String statusName;  // 自动填充
}
```

**结果：**
```json
{
    "sex": 1,
    "sexName": "男",
    "status": 0,
    "statusName": "正常"
}
```

---

## 注意事项

### 1. 性能考虑

```java
// Easy-Trans 会批量查询需要翻译的数据
// 对于大量数据的翻译，注意性能影响
```

### 2. 字段命名

```java
// 翻译结果字段命名规则：
// deptId → deptIdName 或 deptName
// 确保字段名正确
```

### 3. 空值处理

```java
// 当源字段为空时，翻译结果也为空
```

---

## 配置

```yaml
# application.yml
easy-trans:
  enabled: true
  # ... 其他配置
```
