# 查询构造器指南

## 概述

`LambdaQueryWrapperX` 扩展了 MyBatis Plus 的 `LambdaQueryWrapper`，增加了 `xxxIfPresent` 方法，用于在条件值存在时才拼接条件。

## 核心特性

| 方法 | 说明 | 原始方法 |
|-----|------|---------|
| `eqIfPresent` | 值存在时拼接 eq | `eq` |
| `neIfPresent` | 值存在时拼接 ne | `ne` |
| `likeIfPresent` | 值存在时拼接 like | `like` |
| `inIfPresent` | 值存在时拼接 in | `in` |
| `gtIfPresent` | 值存在时拼接 gt | `gt` |
| `geIfPresent` | 值存在时拼接 ge | `ge` |
| `ltIfPresent` | 值存在时拼接 lt | `lt` |
| `leIfPresent` | 值存在时拼接 le | `le` |
| `betweenIfPresent` | 值存在时拼接 between | `between` |

## 使用方式

### eqIfPresent

```java
// 值不为 null 时拼接 eq 条件
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .eqIfPresent(UserDO::getStatus, req.getStatus())
        .eqIfPresent(UserDO::getDeptId, req.getDeptId());

// 等价于
LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
if (req.getStatus() != null) {
    wrapper.eq(UserDO::getStatus, req.getStatus());
}
if (req.getDeptId() != null) {
    wrapper.eq(UserDO::getDeptId, req.getDeptId());
}
```

### likeIfPresent

```java
// 值不为空时拼接 like 条件
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .likeIfPresent(UserDO::getUsername, req.getUsername());

// 当 username 为空字符串或 null 时，不会拼接条件
```

### inIfPresent

```java
// 集合不为空时拼接 in 条件
List<Integer> statusList = Arrays.asList(1, 2, 3);
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .inIfPresent(UserDO::getStatus, statusList);

// 使用数组
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .inIfPresent(UserDO::getStatus, 1, 2, 3);
```

### betweenIfPresent

```java
// 值存在时拼接 between 条件
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .betweenIfPresent(UserDO::getCreateTime, req.getBeginTime(), req.getEndTime());

// 只有一个值时，自动转为 ge 或 le
// beginTime 有值 → ge
// endTime 有值 → le
```

### gtIfPresent / geIfPresent / ltIfPresent / leIfPresent

```java
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .geIfPresent(UserDO::getCreateTime, req.getBeginTime())
        .leIfPresent(UserDO::getCreateTime, req.getEndTime());
```

---

## 完整示例

### 用户查询

```java
public PageResult<UserVO> page(UserPageReqVO req) {
    LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
            .likeIfPresent(UserDO::getUsername, req.getUsername())
            .eqIfPresent(UserDO::getStatus, req.getStatus())
            .eqIfPresent(UserDO::getDeptId, req.getDeptId())
            .betweenIfPresent(UserDO::getCreateTime, req.getCreateTime())
            .orderByDesc(UserDO::getId);
    
    PageResult<UserDO> page = userMapper.selectPage(req, wrapper);
    return CollectionUtils.convertPage(page, user -> BeanUtils.toBean(user, UserVO.class));
}
```

### 订单查询

```java
public PageResult<OrderVO> page(OrderPageReqVO req) {
    LambdaQueryWrapperX<OrderDO> wrapper = new LambdaQueryWrapperX<OrderDO>()
            .eqIfPresent(OrderDO::getStatus, req.getStatus())
            .eqIfPresent(OrderDO::getUserId, req.getUserId())
            .inIfPresent(OrderDO::getOrderType, req.getOrderTypes())
            .betweenIfPresent(OrderDO::getCreateTime, req.getBeginTime(), req.getEndTime())
            .orderByDesc(OrderDO::getId);
    
    return orderMapper.selectPage(req, wrapper);
}
```

---

## 注意事项

### 1. 空字符串处理

```java
// likeIfPresent 会检查空字符串
.likeIfPresent(UserDO::getUsername, "")  // 不会拼接条件

// eqIfPresent 只检查 null
.eqIfPresent(UserDO::getStatus, null)  // 不会拼接条件
.eqIfPresent(UserDO::getStatus, 0)     // 会拼接条件（0 不是 null）
```

### 2. 空集合处理

```java
// inIfPresent 会检查空集合
.inIfPresent(UserDO::getStatus, Collections.emptyList())  // 不会拼接条件
```

### 3. 链式调用

```java
// LambdaQueryWrapperX 重写了所有方法，支持链式调用
LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
        .eq(UserDO::getStatus, 1)
        .like(UserDO::getUsername, "admin")
        .orderByDesc(UserDO::getCreateTime)
        .last("LIMIT 10");
```
