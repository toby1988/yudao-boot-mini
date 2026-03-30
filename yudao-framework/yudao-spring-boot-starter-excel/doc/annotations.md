# 注解使用指南

## 概述

框架提供了多种注解，用于控制 Excel 的导入导出行为。

## @DictFormat - 字典格式化

将字典值转换为字典标签。

### 定义

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DictFormat {
    
    /**
     * 字典类型，如 "sys_user_sex"
     */
    String value();
}
```

### 使用

```java
@Data
public class UserExcelVO {
    
    @DictFormat("sys_user_sex")
    @ExcelProperty("性别")
    private Integer sex;  // 1:男 2:女
    
    @DictFormat("sys_common_status")
    @ExcelProperty("状态")
    private Integer status;  // 0:正常 1:停用
}
```

### 效果

| 数据库值 | Excel 显示 |
|---------|----------|
| 1 | 男 |
| 2 | 女 |
| 0 | 正常 |
| 1 | 停用 |

---

## @ExcelColumnSelect - 下拉选择

为 Excel 列添加下拉选择功能。

### 定义

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelColumnSelect {
    
    /**
     * 字典类型（与 functionName 二选一）
     */
    String dictType() default "";
    
    /**
     * 获取下拉数据源的方法名称（与 dictType 二选一）
     */
    String functionName() default "";
}
```

### 使用方式1：基于字典

```java
@Data
public class UserExcelVO {
    
    @ExcelColumnSelect(dictType = "sys_user_sex")
    @ExcelProperty("性别")
    private Integer sex;
}
```

### 使用方式2：基于方法

```java
@Data
public class OrderExcelVO {
    
    @ExcelColumnSelect(functionName = "getProductList")
    @ExcelProperty("商品")
    private Long productId;
}
```

### 下拉数据源方法

```java
public class OrderExcelVO {
    
    // 返回下拉数据源的方法
    public static List<String> getProductList() {
        return Arrays.asList("商品A", "商品B", "商品C");
    }
}
```

---

## 常用 FastExcel 注解

### @ExcelProperty

定义 Excel 列名。

```java
@ExcelProperty("用户名称")
private String username;
```

### @ColumnWidth

设置列宽。

```java
@ColumnWidth(20)
@ExcelProperty("用户名称")
private String username;
```

### @DateTimeFormat

日期格式化。

```java
@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
@ExcelProperty("创建时间")
private LocalDateTime createTime;
```

### @NumberFormat

数字格式化。

```java
@NumberFormat("#.##")
@ExcelProperty("价格")
private Double price;
```

### @ExcelIgnore

忽略字段（不导出）。

```java
@ExcelIgnore
private String password;
```

### @ExcelIgnoreUnannotated

忽略未加注解的字段。

```java
@Data
@ExcelIgnoreUnannotated  // 只导出加了 @ExcelProperty 的字段
public class UserExcelVO {
    
    @ExcelProperty("用户名称")
    private String username;
    
    private String password;  // 会被忽略
}
```

---

## 组合使用示例

```java
@Data
@ExcelIgnoreUnannotated
public class UserExcelVO {
    
    @ExcelProperty("用户编号")
    @ColumnWidth(15)
    private Long id;
    
    @ExcelProperty("用户名称")
    @ColumnWidth(20)
    private String username;
    
    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String mobile;
    
    @DictFormat("sys_user_sex")
    @ExcelColumnSelect(dictType = "sys_user_sex")
    @ExcelProperty("性别")
    @ColumnWidth(10)
    private Integer sex;
    
    @DictFormat("sys_common_status")
    @ExcelProperty("状态")
    @ColumnWidth(10)
    private Integer status;
    
    @MoneyConvert
    @ExcelProperty("余额(元)")
    @ColumnWidth(15)
    private Integer balance;
    
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;
    
    @AreaConvert
    @ExcelProperty("所在地区")
    @ColumnWidth(25)
    private Integer areaId;
    
    @ExcelIgnore
    private String password;
}
```
