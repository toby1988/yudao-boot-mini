# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-biz-data-permission</artifactId>
</dependency>
```

## 2. 配置部门数据权限

### 2.1 创建配置类

```java
@Configuration
public class DataPermissionConfig {
    
    @Autowired
    private DeptDataPermissionRule deptDataPermissionRule;
    
    @PostConstruct
    public void init() {
        // 为需要数据权限的表添加配置
        deptDataPermissionRule.addDeptColumn(UserDO.class);      // 用户表
        deptDataPermissionRule.addDeptColumn(OrderDO.class);     // 订单表
        deptDataPermissionRule.addDeptColumn(SalaryDO.class);    // 工资表
    }
}
```

### 2.2 配置多个表

```java
@PostConstruct
public void init() {
    // 使用默认的 dept_id 字段
    deptDataPermissionRule.addDeptColumn(UserDO.class);
    deptDataPermissionRule.addDeptColumn(OrderDO.class);
    
    // 使用自定义字段名
    deptDataPermissionRule.addDeptColumn(ProductDO.class, "owning_dept_id");
    
    // 使用 user_id 字段（基于用户的权限）
    deptDataPermissionRule.addUserColumn(OrderDO.class, "creator_id");
}
```

## 3. 确保表有字段

确保需要数据权限的表有 `dept_id` 字段：

```sql
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```

## 4. 测试验证

### 4.1 创建测试用户

```sql
-- 创建部门
INSERT INTO sys_dept (id, name) VALUES (1, '研发部');
INSERT INTO sys_dept (id, name) VALUES (2, '销售部');

-- 创建用户
INSERT INTO sys_user (id, username, dept_id, user_id) 
VALUES (1, 'admin', 1, 1);
INSERT INTO sys_user (id, username, dept_id, user_id) 
VALUES (2, 'user2', 1, 2);
INSERT INTO sys_user (id, username, dept_id, user_id) 
VALUES (3, 'user3', 2, 3);
```

### 4.2 查询测试

```java
@Slf4j
@SpringBootTest
public class DataPermissionTest {
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    public void testQuery() {
        // 查询所有用户（会自动过滤）
        List<UserDO> users = userMapper.selectList(null);
        log.info("查询到用户数量: {}", users.size());
        // 只会返回当前用户有权限看到的数据
    }
}
```

## 5. 运行验证

启动应用后，查看 SQL 日志：

```
SELECT * FROM sys_user WHERE dept_id IN (1)
```

可以看到 SQL 自动添加了 `WHERE dept_id IN (1)` 条件。

## 6. 常见配置

### 6.1 禁用数据权限

```java
@DataPermission(enable = false)
public List<UserDO> selectAll() {
    return userMapper.selectList(null);
}
```

### 6.2 忽略数据权限执行

```java
// 使用工具类忽略权限
List<UserDO> users = DataPermissionUtils.executeIgnore(() -> {
    return userMapper.selectList(null);
});
```
