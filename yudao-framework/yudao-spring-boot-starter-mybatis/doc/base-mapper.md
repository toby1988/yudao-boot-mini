# BaseMapperX 使用指南

## 概述

`BaseMapperX` 继承 MyBatis Plus 的 `BaseMapper` 和 `MPJBaseMapper`，提供更多便捷方法。

## 分页查询

### selectPage

```java
// 基础分页
PageResult<UserDO> page = userMapper.selectPage(req, wrapper);

// 带排序
PageResult<UserDO> page = userMapper.selectPage(req, sortingFields, wrapper);

// 联表分页
PageResult<UserVO> page = userMapper.selectJoinPage(req, UserVO.class, lambdaWrapper);
```

### 使用示例

```java
public PageResult<UserVO> page(UserPageReqVO req) {
    LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
            .likeIfPresent(UserDO::getUsername, req.getUsername())
            .eqIfPresent(UserDO::getStatus, req.getStatus())
            .orderByDesc(UserDO::getId);
    
    PageResult<UserDO> page = userMapper.selectPage(req, wrapper);
    return CollectionUtils.convertPage(page, user -> BeanUtils.toBean(user, UserVO.class));
}
```

---

## 查询单条

### selectOne

```java
// 根据字段查询
UserDO user = userMapper.selectOne(UserDO::getUsername, "admin");

// 根据两个字段查询
UserDO user = userMapper.selectOne(UserDO::getUsername, "admin", UserDO::getStatus, 1);

// 根据三个字段查询
UserDO user = userMapper.selectOne(
    UserDO::getUsername, "admin", 
    UserDO::getStatus, 1,
    UserDO::getDeptId, 100
);
```

### selectFirstOne

```java
// 获取第一个匹配的记录（避免并发场景 selectOne 报错）
UserDO user = userMapper.selectFirstOne(UserDO::getUsername, "admin");
```

---

## 查询列表

### selectList

```java
// 查询所有
List<UserDO> list = userMapper.selectList();

// 根据字段查询
List<UserDO> list = userMapper.selectList(UserDO::getStatus, 1);

// 根据 IN 查询
List<UserDO> list = userMapper.selectList(UserDO::getStatus, Arrays.asList(1, 2));

// 根据两个字段查询
List<UserDO> list = userMapper.selectList(UserDO::getStatus, 1, UserDO::getDeptId, 100);
```

---

## 查询数量

### selectCount

```java
// 查询总数
Long count = userMapper.selectCount();

// 根据字段查询数量
Long count = userMapper.selectCount(UserDO::getStatus, 1);
```

---

## 批量操作

### insertBatch

```java
// 批量插入
List<UserDO> users = Arrays.asList(user1, user2, user3);
userMapper.insertBatch(users);

// 指定批次大小
userMapper.insertBatch(users, 500);
```

### updateBatch

```java
// 批量更新（根据 ID）
List<UserDO> users = Arrays.asList(user1, user2, user3);
userMapper.updateBatch(users);

// 指定批次大小
userMapper.updateBatch(users, 500);

// 批量更新（根据条件）
UserDO update = new UserDO();
update.setStatus(0);
int count = userMapper.updateBatch(update);  // 更新所有记录
```

### deleteBatch

```java
// 批量删除
userMapper.deleteBatch(UserDO::getId, Arrays.asList(1L, 2L, 3L));
```

---

## 删除操作

### delete

```java
// 根据字段删除
userMapper.delete(UserDO::getUsername, "admin");

// 根据 ID 删除
userMapper.deleteById(1L);
```

---

## 完整示例

```java
@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {
    
    // 大部分场景使用内置方法即可，无需自定义 SQL
}

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    // 查询单个
    public UserDO getByUsername(String username) {
        return userMapper.selectOne(UserDO::getUsername, username);
    }
    
    // 查询列表
    public List<UserDO> listByDeptId(Long deptId) {
        return userMapper.selectList(UserDO::getDeptId, deptId);
    }
    
    // 分页查询
    public PageResult<UserDO> page(UserPageReqVO req) {
        LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, req.getUsername())
                .eqIfPresent(UserDO::getStatus, req.getStatus());
        return userMapper.selectPage(req, wrapper);
    }
    
    // 批量插入
    public void batchCreate(List<UserDO> users) {
        userMapper.insertBatch(users);
    }
    
    // 批量删除
    public void batchDelete(List<Long> ids) {
        userMapper.deleteBatch(UserDO::getId, ids);
    }
}
```
