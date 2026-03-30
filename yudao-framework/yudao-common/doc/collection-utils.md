# Collection 工具类使用指南

## 概述

`CollectionUtils` 提供集合的常用操作，包括转换、过滤、去重等。

## API 方法

### 集合转换

#### convertList - List 转换

```java
// 基础转换
List<UserVO> vos = CollectionUtils.convertList(users, user -> {
    UserVO vo = new UserVO();
    vo.setId(user.getId());
    return vo;
});

// 使用方法引用
List<Long> ids = CollectionUtils.convertList(users, User::getId);

// 带过滤条件
List<UserVO> vos = CollectionUtils.convertList(users, 
    user -> BeanUtils.toBean(user, UserVO.class),
    user -> user.getStatus() == 1  // 只转换状态为1的
);
```

#### convertSet - Set 转换

```java
// 转换为 Set
Set<Long> ids = CollectionUtils.convertSet(users, User::getId);

// 带过滤条件
Set<String> names = CollectionUtils.convertSet(users, 
    User::getName,
    user -> user.getStatus() == 1
);
```

#### convertMap - Map 转换

```java
// 基础转换 Map<id, User>
Map<Long, User> userMap = CollectionUtils.convertMap(users, User::getId);

// 转换 Map<id, name>
Map<Long, String> nameMap = CollectionUtils.convertMap(users, 
    User::getId, 
    User::getName
);

// 处理重复 key
Map<Long, User> userMap = CollectionUtils.convertMap(users, 
    User::getId,
    Function.identity(),
    (v1, v2) -> v2  // 重复时取后者
);
```

#### convertMultiMap - 一对多 Map

```java
// 按部门分组 Map<deptId, List<User>>
Map<Long, List<User>> deptUserMap = CollectionUtils.convertMultiMap(users, 
    User::getDeptId
);

// 按部门分组，只取姓名 Map<deptId, List<String>>
Map<Long, List<String>> deptNameMap = CollectionUtils.convertMultiMap(users,
    User::getDeptId,
    User::getName
);
```

#### convertPage - 分页转换

```java
// PageResult<DO> -> PageResult<VO>
PageResult<UserVO> voPage = CollectionUtils.convertPage(page, 
    user -> BeanUtils.toBean(user, UserVO.class)
);
```

### 集合过滤

#### filterList - 过滤

```java
// 过滤状态为1的用户
List<User> activeUsers = CollectionUtils.filterList(users, 
    user -> user.getStatus() == 1
);
```

### 集合去重

#### distinct - 去重

```java
// 按 ID 去重（保留第一个）
List<User> distinctUsers = CollectionUtils.distinct(users, User::getId);

// 按 ID 去重（保留最后一个）
List<User> distinctUsers = CollectionUtils.distinct(users, 
    User::getId,
    (v1, v2) -> v2
);
```

### 集合查找

#### findFirst - 查找第一个

```java
// 查找第一个匹配的元素
User user = CollectionUtils.findFirst(users, u -> u.getId() == 1);

// 查找并转换
String name = CollectionUtils.findFirst(users, 
    u -> u.getId() == 1,
    User::getName
);
```

#### getFirst - 获取第一个

```java
// 获取第一个元素
User firstUser = CollectionUtils.getFirst(users);
```

#### anyMatch - 任意匹配

```java
// 判断是否有任意元素匹配
boolean hasAdmin = CollectionUtils.anyMatch(users, 
    user -> "admin".equals(user.getUsername())
);
```

### 集合统计

#### getSumValue - 求和

```java
// 计算金额总和
Integer totalAmount = CollectionUtils.getSumValue(orders, 
    Order::getAmount,
    Integer::sum
);

// 带默认值
Integer totalAmount = CollectionUtils.getSumValue(orders,
    Order::getAmount,
    Integer::sum,
    0
);
```

#### getMaxValue - 获取最大值

```java
// 获取最大金额
Integer maxAmount = CollectionUtils.getMaxValue(orders, Order::getAmount);
```

#### getMinValue - 获取最小值

```java
// 获取最小金额
Integer minAmount = CollectionUtils.getMinValue(orders, Order::getAmount);
```

### 集合对比

#### diffList - 对比差异

```java
// 对比新旧列表，找出新增、修改、删除的数据
List<List<User>> diff = CollectionUtils.diffList(oldUsers, newUsers, 
    (old, newObj) -> old.getId().equals(newObj.getId())
);

List<User> createList = diff.get(0);  // 新增
List<User> updateList = diff.get(1);  // 修改（新列表中存在的）
List<User> deleteList = diff.get(2);  // 删除
```

### 其他方法

```java
// 判断是否包含任意一个
boolean containsAny = CollectionUtils.containsAny(source, "a", "b", "c");

// 集合是否任意为空
boolean isAnyEmpty = CollectionUtils.isAnyEmpty(list1, list2, list3);

// 添加非空元素
CollectionUtils.addIfNotNull(list, item);

// 单例集合
Collection<User> singleton = CollectionUtils.singleton(user);
```

## 使用场景

### 场景1：DO/VO 转换

```java
public List<UserVO> listUsers() {
    List<User> users = userMapper.selectList(null);
    return CollectionUtils.convertList(users, user -> {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        return vo;
    });
}
```

### 场景2：批量查询后按 ID 分组

```java
public Map<Long, User> getUserMap(Set<Long> ids) {
    List<User> users = userMapper.selectBatchIds(ids);
    return CollectionUtils.convertMap(users, User::getId);
}
```

### 场景3：数据对比同步

```java
public void syncUsers(List<UserDTO> newUsers) {
    List<User> oldUsers = userMapper.selectList(null);
    
    List<List<User>> diff = CollectionUtils.diffList(oldUsers, newUsers, 
        (old, newObj) -> old.getId().equals(newObj.getId())
    );
    
    userMapper.insertBatch(diff.get(0));  // 新增
    userMapper.updateBatch(diff.get(1));  // 修改
    userMapper.deleteBatch(diff.get(2));  // 删除
}
```
