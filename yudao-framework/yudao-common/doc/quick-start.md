# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-common</artifactId>
</dependency>
```

## 2. 统一返回结果

```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public CommonResult<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.get(id);
        return CommonResult.success(user);
    }
    
    @PostMapping("/user/create")
    public CommonResult<Long> createUser(@RequestBody UserCreateDTO dto) {
        Long id = userService.create(dto);
        return CommonResult.success(id);
    }
}
```

## 3. 分页查询

```java
@GetMapping("/user/list")
public CommonResult<PageResult<UserVO>> list(UserPageReqVO req) {
    PageResult<UserVO> result = userService.page(req);
    return CommonResult.success(result);
}
```

## 4. 业务异常

```java
public void updateUser(Long id, UserUpdateDTO dto) {
    User user = userMapper.selectById(id);
    if (user == null) {
        throw new ServiceException(USER_NOT_FOUND);
    }
    // 更新逻辑
}
```

## 5. 对象转换

```java
// DO -> VO
UserVO vo = BeanUtils.toBean(user, UserVO.class);

// List<DO> -> List<VO>
List<UserVO> vos = BeanUtils.toBean(users, UserVO.class);

// PageResult<DO> -> PageResult<VO>
PageResult<UserVO> pageVO = BeanUtils.toBean(page, UserVO.class);
```

## 6. JSON 操作

```java
// 对象转 JSON
String json = JsonUtils.toJsonString(user);

// JSON 转对象
UserVO user = JsonUtils.parseObject(json, UserVO.class);

// JSON 转数组
List<UserVO> users = JsonUtils.parseArray(json, UserVO.class);
```

## 7. 集合操作

```java
// List 转换
List<UserVO> vos = CollectionUtils.convertList(users, user -> {
    UserVO vo = new UserVO();
    vo.setId(user.getId());
    vo.setName(user.getName());
    return vo;
});

// List 转 Map
Map<Long, User> userMap = CollectionUtils.convertMap(users, User::getId);

// 过滤
List<User> activeUsers = CollectionUtils.filterList(users, 
    user -> user.getStatus() == 1);

// 去重
List<User> distinctUsers = CollectionUtils.distinct(users, User::getId);
```

## 8. 日期操作

```java
// Date <-> LocalDateTime 转换
LocalDateTime localDateTime = DateUtils.of(date);
Date date = DateUtils.of(localDateTime);

// 判断是否今天
boolean isToday = DateUtils.isToday(localDateTime);

// 创建指定时间
Date date = DateUtils.buildTime(2024, 1, 1);
```
