# 转换器使用指南

## 概述

框架提供了多种内置转换器，用于在 Excel 导入导出时进行数据转换。

## DictConvert - 字典转换器

自动将字典值转换为标签。

### 工作原理

| 方向 | 转换 |
|-----|------|
| 导出 | 字典值 → 字典标签 |
| 导入 | 字典标签 → 字典值 |

### 使用方式

```java
@Data
public class UserExcelVO {
    
    @DictFormat("sys_user_sex")  // 指定字典类型
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

### 前提条件

需要引入数据字典模块：
```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-excel</artifactId>
</dependency>
```

---

## MoneyConvert - 金额转换器

自动将分转换为元。

### 工作原理

| 方向 | 转换 |
|-----|------|
| 导出 | 分 → 元（除以 100） |
| 导入 | 元 → 分（乘以 100） |

### 使用方式

```java
@Data
public class OrderExcelVO {
    
    @MoneyConvert
    @ExcelProperty("金额(元)")
    private Integer amount;  // 单位：分
}
```

### 效果

| 数据库值 | Excel 显示 |
|---------|----------|
| 10000 | 100.00 |
| 9999 | 99.99 |
| 100 | 1.00 |
| 1 | 0.01 |

### 注意事项

- 数据库存储的是**分**（Integer 类型）
- Excel 显示的是**元**（String 类型）
- 支持四舍五入（RoundingMode.HALF_UP）

---

## AreaConvert - 区域转换器

自动将地区 ID 转换为地区名称。

### 工作原理

| 方向 | 转换 |
|-----|------|
| 导出 | 地区 ID → 地区名称 |
| 导入 | 地区名称 → 地区 ID |

### 使用方式

```java
@Data
public class AddressExcelVO {
    
    @AreaConvert
    @ExcelProperty("所在地区")
    private Integer areaId;  // 地区 ID
}
```

### 效果

| 数据库值 | Excel 显示 |
|---------|----------|
| 110105 | 北京 北京市 朝阳区 |
| 440300 | 广东省 深圳市 |
| 410200 | 河南省 开封市 |

### 前提条件

需要引入 IP 工具模块：
```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-biz-ip</artifactId>
    <optional>true</optional>
</dependency>
```

---

## JsonConvert - JSON 转换器（预留）

将 JSON 字符串格式化显示。

```java
// 未来可能支持
@Data
public class DataExcelVO {
    
    @JsonConvert
    @ExcelProperty("扩展信息")
    private String extraInfo;  // JSON 字符串
}
```

---

## 组合使用

```java
@Data
public class OrderExcelVO {
    
    @ExcelProperty("订单编号")
    private String orderNo;
    
    @ExcelProperty("商品名称")
    private String productName;
    
    @MoneyConvert  // 金额转换
    @ExcelProperty("金额(元)")
    private Integer amount;
    
    @DictFormat("sys_order_status")  // 字典转换
    @ExcelProperty("状态")
    private Integer status;
    
    @AreaConvert  // 地区转换
    @ExcelProperty("收货地区")
    private Integer areaId;
}
```

---

## 自定义转换器

如果需要自定义转换器，参考以下步骤：

### 1. 实现 Converter 接口

```java
@Slf4j
public class CustomConvert implements Converter<String> {
    
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }
    
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }
    
    @Override
    public WriteCellData<String> convertToExcelData(
            String value, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 导出时转换
        return new WriteCellData<>(formatValue(value));
    }
    
    @Override
    public String convertToJavaData(
            ReadCellData readCellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 导入时转换
        return parseValue(readCellData.getStringValue());
    }
    
    private String formatValue(String value) {
        // 格式化逻辑
        return value;
    }
    
    private String parseValue(String label) {
        // 解析逻辑
        return label;
    }
}
```

### 2. 注册转换器

```java
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    FastExcelFactory.write(response.getOutputStream(), UserExcelVO.class)
            .registerConverter(new CustomConvert())
            .sheet("数据")
            .doWrite(getData());
    
    response.addHeader("Content-Disposition", "attachment;filename=data.xlsx");
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
}
```
