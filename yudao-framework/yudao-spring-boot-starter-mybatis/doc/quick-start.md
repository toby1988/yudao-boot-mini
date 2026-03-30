# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-mybatis</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/yudao?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted    # 逻辑删除字段
      logic-delete-value: true       # 逻辑删除值
      logic-not-delete-value: false  # 逻辑未删除值
```

## 3. 定义实体类

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

## 4. 定义 Mapper

```java
@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {
    
    // 基础查询方法已内置，无需定义
}
```

## 5. 使用示例

### 查询

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    // 根据 ID 查询
    public UserDO getById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 根据字段查询
    public UserDO getByUsername(String username) {
        return userMapper.selectOne(UserDO::getUsername, username);
    }
    
    // 查询列表
    public List<UserDO> listByStatus(Integer status) {
        return userMapper.selectList(UserDO::getStatus, status);
    }
    
    // 分页查询
    public PageResult<UserDO> page(UserPageReqVO req) {
        LambdaQueryWrapperX<UserDO> wrapper = new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, req.getUsername())
                .eqIfPresent(UserDO::getStatus, req.getStatus())
                .orderByDesc(UserDO::getId);
        return userMapper.selectPage(req, wrapper);
    }
}
```

### 新增

```java
public void create(UserCreateDTO dto) {
    UserDO user = BeanUtils.toBean(dto, UserDO.class);
    userMapper.insert(user);  // 自动填充 createTime、updateTime、creator、updater
}
```

### 修改

```java
public void update(UserUpdateDTO dto) {
    UserDO user = BeanUtils.toBean(dto, UserDO.class);
    userMapper.updateById(user);  // 自动填充 updateTime、updater
}
```

### 删除

```java
public void delete(Long id) {
    userMapper.deleteById(id);  // 逻辑删除
}
```

## 6. 验证

启动应用后，测试 CRUD 操作是否正常工作。
