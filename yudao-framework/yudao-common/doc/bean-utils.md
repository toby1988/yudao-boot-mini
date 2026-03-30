# Bean 工具类使用指南

## 概述

`BeanUtils` 基于 Hutool BeanUtil 实现对象转换。

## API 方法

### 对象转换

```java
// 对象转对象
UserVO vo = BeanUtils.toBean(user, UserVO.class);

// 带回调处理
UserVO vo = BeanUtils.toBean(user, UserVO.class, v -> {
    v.setAge(18);  // 额外处理
});
```

### 集合转换

```java
// List 转 List
List<UserVO> vos = BeanUtils.toBean(users, UserVO.class);

// List 转 List（带回调）
List<UserVO> vos = BeanUtils.toBean(users, UserVO.class, vo -> {
    vo.setAge(18);
});
```

### 分页转换

```java
// PageResult<DO> -> PageResult<VO>
PageResult<UserVO> voPage = BeanUtils.toBean(page, UserVO.class);

// 带回调
PageResult<UserVO> voPage = BeanUtils.toBean(page, UserVO.class, vo -> {
    vo.setAge(18);
});
```

### 属性拷贝

```java
// 属性拷贝（非空）
BeanUtils.copyProperties(source, target);
```

## 使用示例

### 场景1：DO 转 VO

```java
// 单个对象
User user = userMapper.selectById(id);
UserVO vo = BeanUtils.toBean(user, UserVO.class);

// 列表
List<User> users = userMapper.selectList(null);
List<UserVO> vos = BeanUtils.toBean(users, UserVO.class);

// 分页
PageResult<User> page = userMapper.selectPage(req);
PageResult<UserVO> voPage = BeanUtils.toBean(page, UserVO.class);
```

### 场景2：DTO 转 DO

```java
UserCreateDTO dto = new UserCreateDTO();
User user = BeanUtils.toBean(dto, User.class);
userMapper.insert(user);
```

### 场景3：带默认值转换

```java
UserVO vo = BeanUtils.toBean(user, UserVO.class, v -> {
    // 设置默认值
    if (v.getStatus() == null) {
        v.setStatus(0);
    }
});
```

### 场景4：属性拷贝更新

```java
User user = userMapper.selectById(id);
UserUpdateDTO dto = new UserUpdateDTO();
// 只拷贝非空属性
BeanUtils.copyProperties(dto, user);
userMapper.updateById(user);
```

## 高级用法

### 复杂对象转换

对于复杂对象转换，推荐使用 MapStruct：

```java
@Mapper(componentModel = "spring")
public interface UserConvert {
    
    UserVO convert(User user);
    
    List<UserVO> convertList(List<User> users);
}
```

### 嵌套对象处理

```java
UserVO vo = BeanUtils.toBean(user, UserVO.class, v -> {
    // 处理嵌套对象
    if (user.getDeptId() != null) {
        Dept dept = deptMapper.selectById(user.getDeptId());
        v.setDeptName(dept.getName());
    }
});
```

## 注意事项

1. **性能**：BeanUtils 基于反射，性能较低，对性能要求高的场景建议使用 MapStruct
2. **空值处理**：转换时会忽略 null 值
3. **类型匹配**：只拷贝类型匹配的属性
