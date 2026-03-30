# 数据字典集成指南

## 概述

Excel 模块与数据字典模块深度集成，支持在 Excel 导入导出时自动进行字典值的转换。

## 工作原理

```
┌─────────────────────────────────────────────────────────────┐
│                    字典转换流程                              │
└─────────────────────────────────────────────────────────────┘

导出流程：
    数据库值 (1) 
        ↓
    @DictFormat("sys_user_sex")
        ↓
    DictConvert.convertToExcelData()
        ↓
    DictFrameworkUtils.parseDictDataLabel("sys_user_sex", "1")
        ↓
    Excel 显示 (男)

导入流程：
    Excel 输入 (男)
        ↓
    @DictFormat("sys_user_sex")
        ↓
    DictConvert.convertToJavaData()
        ↓
    DictFrameworkUtils.parseDictDataValue("sys_user_sex", "男")
        ↓
    数据库值 (1)
```

## 使用方式

### 1. 添加注解

```java
@Data
public class UserExcelVO {
    
    @DictFormat("sys_user_sex")  // 字典类型
    @ExcelProperty("性别")
    private Integer sex;
    
    @DictFormat("sys_common_status")
    @ExcelProperty("状态")
    private Integer status;
}
```

### 2. 导出效果

| 数据库值 | Excel 显示 |
|---------|----------|
| 1 | 男 |
| 2 | 女 |
| 0 | 正常 |
| 1 | 停用 |

### 3. 导入效果

| Excel 输入 | 数据库值 |
|-----------|---------|
| 男 | 1 |
| 女 | 2 |
| 正常 | 0 |
| 停用 | 1 |

---

## 字典配置

### 1. 创建字典类型

```sql
-- 字典类型表
INSERT INTO sys_dict_type (id, name, type, status) 
VALUES (1, '用户性别', 'sys_user_sex', 0);

-- 字典数据表
INSERT INTO sys_dict_data (id, dict_type, label, value, sort, status) 
VALUES (1, 'sys_user_sex', '男', '1', 1, 0);
INSERT INTO sys_dict_data (id, dict_type, label, value, sort, status) 
VALUES (2, 'sys_user_sex', '女', '2', 2, 0);
```

### 2. 常用字典类型

| 字典类型 | 说明 | 可选值 |
|---------|------|-------|
| sys_user_sex | 用户性别 | 1:男, 2:女 |
| sys_common_status | 通用状态 | 0:正常, 1:停用 |
| sys_notice_type | 通知类型 | 1:通知, 2:公告 |
| sys_operate_type | 操作类型 | 1:查询, 2:新增, ... |

---

## 完整示例

### 用户导出

```java
@Data
public class UserExcelVO {
    
    @ExcelProperty("用户编号")
    private Long id;
    
    @ExcelProperty("用户名称")
    private String username;
    
    @DictFormat("sys_user_sex")
    @ExcelProperty("性别")
    private Integer sex;
    
    @DictFormat("sys_common_status")
    @ExcelProperty("状态")
    private Integer status;
}

@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    List<UserDO> users = userService.list();
    List<UserExcelVO> vos = BeanUtils.toBean(users, UserExcelVO.class);
    ExcelUtils.write(response, "用户列表.xlsx", "用户列表", 
            UserExcelVO.class, vos);
}
```

### 用户导入

```java
@PostMapping("/import")
public void importExcel(@RequestParam("file") MultipartFile file) throws IOException {
    List<UserExcelVO> vos = ExcelUtils.read(file, UserExcelVO.class);
    
    // 字典标签会自动转换为值
    // Excel: "男" → 数据库: 1
    List<UserDO> users = BeanUtils.toBean(vos, UserDO.class);
    
    userService.saveBatch(users);
}
```

---

## 注意事项

### 1. 字典类型必须存在

```java
// ❌ 错误：字典类型不存在
@DictFormat("not_exists_type")  // 会报错
private Integer sex;

// ✅ 正确：使用存在的字典类型
@DictFormat("sys_user_sex")
private Integer sex;
```

### 2. 字典值必须匹配

```java
// 如果字典值不匹配，转换会失败
// Excel: "其他" 
// 字典值: 男、女
// 结果: 返回 null，日志打印错误
```

### 3. 空值处理

```java
// DictConvert 会自动处理空值
// 空值 → 返回空字符串
```

---

## 常见问题

### Q: 导入时字典标签不存在？

A: 检查以下几点：
1. 字典类型是否正确
2. 字典标签是否完全匹配
3. 字典数据是否存在

### Q: 导出时字典值显示为空？

A: 可能原因：
1. 字典类型不存在
2. 字典值在字典中找不到
3. 字典数据状态为停用

### Q: 如何扩展字典转换？

A: 可以自定义转换器：

```java
public class CustomDictConvert implements Converter<String> {
    
    @Override
    public WriteCellData<String> convertToExcelData(
            String value, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 自定义转换逻辑
        String label = customDictService.getLabel(value);
        return new WriteCellData<>(label);
    }
}
```
