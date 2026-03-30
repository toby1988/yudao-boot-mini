# 字段加密指南

## 概述

`EncryptTypeHandler` 基于 AES 对称加密，对数据库字段进行加解密。

## 配置

```yaml
# application.yml
mybatis-plus:
  encryptor:
    password: your-secret-key-here  # AES 密钥，16/24/32 字符
```

## 使用方式

### 1. 实体类中使用

```java
@Data
@TableName("sys_user")
public class UserDO extends BaseDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String username;
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String idCard;  // 身份证号，加密存储
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String bankCard;  // 银行卡号，加密存储
}
```

### 2. 自动加解密

```java
// 保存时自动加密
UserDO user = new UserDO();
user.setIdCard("110101199001011234");  // 明文
user.setBankCard("6222021234567890123");
userMapper.insert(user);
// 数据库存储的是加密后的密文

// 查询时自动解密
UserDO user = userMapper.selectById(1L);
System.out.println(user.getIdCard());  // 110101199001011234 (明文)
System.out.println(user.getBankCard());  // 6222021234567890123 (明文)
```

---

## 加密原理

```
保存流程：
    明文 "110101199001011234"
        ↓
    AES 加密
        ↓
    Base64 编码
        ↓
    存储到数据库 "xxxxxxxxxxxx"

查询流程：
    从数据库读取 "xxxxxxxxxxxx"
        ↓
    Base64 解码
        ↓
    AES 解密
        ↓
    明文 "110101199001011234"
```

---

## 使用场景

### 场景1：用户敏感信息

```java
@Data
@TableName("sys_user")
public class UserDO extends BaseDO {
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String idCard;       // 身份证号
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String bankCard;     // 银行卡号
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String realName;     // 真实姓名
}
```

### 场景2：支付信息

```java
@Data
@TableName("pay_channel")
public class PayChannelDO extends BaseDO {
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String appId;        // 应用 ID
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String appSecret;    // 应用密钥
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String privateKey;   // 私钥
}
```

### 场景3：联系方式

```java
@Data
@TableName("crm_customer")
public class CrmCustomerDO extends BaseDO {
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String mobile;       // 手机号
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String email;        // 邮箱
    
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String address;      // 详细地址
}
```

---

## 注意事项

### 1. 密钥安全

```yaml
# 不要将密钥提交到代码仓库
# 使用环境变量或配置中心
mybatis-plus:
  encryptor:
    password: ${ENCRYPT_PASSWORD}
```

### 2. 已有数据处理

```java
// 如果表中已有数据，需要先加密再使用
// 可以编写迁移脚本批量加密
```

### 3. 性能影响

```java
// 加解密会增加一定的性能开销
// 建议只对真正敏感的字段使用加密
// 普通字段不要使用加密
```

### 4. 查询限制

```java
// 加密字段无法直接查询
// 不支持：WHERE id_card = 'xxx'
// 需要先加密再查询，或者使用模糊查询等其他方式
```

### 5. 索引失效

```java
// 加密字段的索引会失效
// 不建议对加密字段创建索引
```
