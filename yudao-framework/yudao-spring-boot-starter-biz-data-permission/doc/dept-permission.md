# 部门数据权限

## 概述

`DeptDataPermissionRule` 是框架内置的数据权限规则，基于部门进行数据过滤。

## 工作原理

### 过滤条件生成

根据用户的部门权限，自动生成 SQL 条件：

| 用户权限 | 生成的 SQL 条件 |
|---------|---------------|
| 查看全部 | 无条件 |
| 查看指定部门 | `WHERE dept_id IN (1, 2, 3)` |
| 查看本部门及下级 | `WHERE dept_id IN (1, 2, 3, 4, 5)` |
| 仅查看自己 | `WHERE user_id = 1001` |
| 部门 + 自己 | `WHERE (dept_id IN (1, 2) OR user_id = 1001)` |
| 无权限 | `WHERE null = null`（返回空） |

---

## 配置方法

### 1. 基础配置

```java
@Configuration
public class DataPermissionConfig {
    
    @Autowired
    private DeptDataPermissionRule deptDataPermissionRule;
    
    @PostConstruct
    public void init() {
        // 为表添加部门权限（使用默认 dept_id 字段）
        deptDataPermissionRule.addDeptColumn(UserDO.class);
        deptDataPermissionRule.addDeptColumn(OrderDO.class);
    }
}
```

### 2. 自定义字段名

```java
@PostConstruct
public void init() {
    // 使用自定义的部门字段名
    deptDataPermissionRule.addDeptColumn(ProductDO.class, "owning_dept_id");
    deptDataPermissionRule.addDeptColumn(ContractDO.class, "sign_dept_id");
}
```

### 3. 基于用户的权限

```java
@PostConstruct
public void init() {
    // 基于 user_id 字段（查看自己创建的数据）
    deptDataPermissionRule.addUserColumn(OrderDO.class, "creator_id");
    deptDataPermissionRule.addUserColumn(ApplyDO.class, "applicant_id");
}
```

### 4. 组合配置

```java
@PostConstruct
public void init() {
    // 订单表同时支持部门和用户权限
    deptDataPermissionRule.addDeptColumn(OrderDO.class, "dept_id");
    deptDataPermissionRule.addUserColumn(OrderDO.class, "creator_id");
    // 最终条件：WHERE (dept_id IN (1,2,3) OR creator_id = 1001)
}
```

---

## 数据结构

### 表结构要求

```sql
-- 用户表
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `dept_id` bigint NOT NULL COMMENT '部门ID',   -- 部门字段
  `user_id` bigint NOT NULL COMMENT '创建人ID', -- 用户字段
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

-- 订单表
CREATE TABLE `biz_order` (
  `id` bigint NOT NULL,
  `order_no` varchar(64) NOT NULL,
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```

### 权限数据结构

```java
@Data
public class DeptDataPermissionRespDTO {
    
    /**
     * 是否拥有所有数据权限
     */
    private Boolean all;
    
    /**
     * 可访问的部门ID列表
     */
    private Set<Long> deptIds;
    
    /**
     * 是否只能查看自己的数据
     */
    private Boolean self;
}
```

---

## 使用示例

### 示例1：用户列表查询

```java
@Service
public class UserService {
    
    public List<UserVO> getUserList() {
        // 自动应用数据权限
        // 生成 SQL: SELECT * FROM sys_user WHERE dept_id IN (1, 2, 3)
        List<UserDO> users = userMapper.selectList(null);
        return convertList(users, UserVO.class);
    }
}
```

### 示例2：订单统计

```java
@Service
public class OrderService {
    
    public OrderStatisticsVO getStatistics() {
        // 查询条件自动应用权限
        QueryWrapper<OrderDO> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(*) as total, SUM(amount) as totalAmount");
        
        // 自动添加: WHERE dept_id IN (1, 2, 3)
        return orderMapper.selectOne(wrapper);
    }
}
```

### 示例3：联合查询

```java
@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {
    
    @Select("SELECT o.*, u.username FROM biz_order o " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "WHERE o.status = #{status}")
    List<OrderVO> getOrderList(Integer status);
    // 会自动为 biz_order 表添加权限条件
}
```

---

## 权限配置示例

### 管理员权限配置

```java
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Override
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        DeptDataPermissionRespDTO dto = new DeptDataPermissionRespDTO();
        
        // 超级管理员：查看全部
        if (isAdmin(userId)) {
            dto.setAll(true);
            return dto;
        }
        
        // 部门经理：查看本部门及下级部门
        if (isManager(userId)) {
            Set<Long> deptIds = getDeptAndChildren(userId);
            dto.setDeptIds(deptIds);
            dto.setSelf(false);
            return dto;
        }
        
        // 普通员工：仅查看自己的
        dto.setDeptIds(Collections.emptySet());
        dto.setSelf(true);
        return dto;
    }
}
```

---

## 注意事项

### 1. 字段必须存在

```java
// ❌ 错误：表没有 dept_id 字段
deptDataPermissionRule.addDeptColumn(ProductDO.class);  // 会报错

// ✅ 正确：确认表有字段或指定自定义字段
deptDataPermissionRule.addDeptColumn(ProductDO.class, "owning_dept_id");
```

### 2. 用户类型检查

```java
// 只有管理员用户才会应用数据权限
// 普通用户（如 B 端用户）不受影响
if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
    return null;  // 不添加权限条件
}
```

### 3. 性能考虑

```java
// 权限数据会缓存到 LoginUser 上下文
LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
DeptDataPermissionRespDTO permission = loginUser.getContext(CONTEXT_KEY, 
    DeptDataPermissionRespDTO.class);
if (permission == null) {
    // 首次查询，调用 API 获取
    permission = permissionApi.getDeptDataPermission(loginUser.getId());
    loginUser.setContext(CONTEXT_KEY, permission);  // 缓存
}
```
