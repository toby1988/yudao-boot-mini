# ExcelUtils 使用指南

## 概述

`ExcelUtils` 是 Excel 工具类，提供 Excel 的导入导出功能。

## API 方法

### write() - 导出 Excel

将数据列表写入 Excel 并通过 HTTP 响应输出。

```java
public static <T> void write(
    HttpServletResponse response,
    String filename,      // 文件名，如 "用户列表.xlsx"
    String sheetName,     // Sheet 名称，如 "用户列表"
    Class<T> head,        // 数据模型类
    List<T> data          // 数据列表
) throws IOException
```

**使用示例：**

```java
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    List<UserExcelVO> vos = getUserList();
    ExcelUtils.write(response, "用户列表.xlsx", "用户列表", 
            UserExcelVO.class, vos);
}
```

**效果：**
- 自动设置响应头：`Content-Disposition: attachment;filename=用户列表.xlsx`
- 自动设置响应类型：`application/vnd.ms-excel`
- 列宽自适应（最大 255）
- Long 类型不会丢失精度

---

### read() - 导入 Excel

从 Excel 文件读取数据列表。

```java
public static <T> List<T> read(
    MultipartFile file,   // 上传的文件
    Class<T> head         // 数据模型类
) throws IOException
```

**使用示例：**

```java
@PostMapping("/import")
public void importExcel(@RequestParam("file") MultipartFile file) 
        throws IOException {
    List<UserExcelVO> vos = ExcelUtils.read(file, UserExcelVO.class);
    
    // 处理数据
    for (UserExcelVO vo : vos) {
        // ...
    }
}
```

---

## 数据模型定义

### 基础定义

```java
@Data
public class UserExcelVO {
    
    @ExcelProperty("用户编号")
    private Long id;
    
    @ExcelProperty("用户名称")
    private String username;
    
    @ExcelProperty("手机号")
    private String mobile;
}
```

### 常用注解

| 注解 | 说明 | 示例 |
|-----|------|------|
| `@ExcelProperty` | 定义列名 | `@ExcelProperty("用户名称")` |
| `@ColumnWidth` | 设置列宽 | `@ColumnWidth(20)` |
| `@DateTimeFormat` | 日期格式 | `@DateTimeFormat("yyyy-MM-dd")` |
| `@DictFormat` | 字典格式 | `@DictFormat("sys_user_sex")` |
| `@ExcelIgnore` | 忽略字段 | `@ExcelIgnore` |

### 完整示例

```java
@Data
public class UserExcelVO {
    
    @ExcelProperty("用户编号")
    private Long id;
    
    @ColumnWidth(20)
    @ExcelProperty("用户名称")
    private String username;
    
    @ColumnWidth(15)
    @ExcelProperty("手机号")
    private String mobile;
    
    @DictFormat("sys_user_sex")
    @ExcelProperty("性别")
    private Integer sex;
    
    @DictFormat("sys_common_status")
    @ExcelProperty("状态")
    private Integer status;
    
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
    @MoneyConvert
    @ExcelProperty("余额(元)")
    private Integer balance;
    
    @ExcelIgnore  // 不导出该字段
    private String password;
}
```

---

## 高级用法

### 1. 自定义样式

```java
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    List<UserExcelVO> vos = getUserList();
    
    FastExcelFactory.write(response.getOutputStream(), UserExcelVO.class)
            .registerWriteHandler(new ColumnWidthStyleStrategy())  // 列宽
            .registerWriteHandler(new CustomCellWriteHandler())    // 自定义样式
            .sheet("用户列表")
            .doWrite(vos);
    
    response.addHeader("Content-Disposition", "attachment;filename=users.xlsx");
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
}
```

### 2. 多 Sheet 导出

```java
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    ExcelWriter excelWriter = FastExcelFactory.write(response.getOutputStream()).build();
    
    // 写入第一个 Sheet
    WriteSheet sheet1 = FastExcelFactory.writerSheet(0, "用户列表")
            .head(UserExcelVO.class).build();
    excelWriter.write(getUserList(), sheet1);
    
    // 写入第二个 Sheet
    WriteSheet sheet2 = FastExcelFactory.writerSheet(1, "订单列表")
            .head(OrderExcelVO.class).build();
    excelWriter.write(getOrderList(), sheet2);
    
    excelWriter.finish();
    
    response.addHeader("Content-Disposition", "attachment;filename=report.xlsx");
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
}
```

### 3. 模板导出

```java
@GetMapping("/export/template")
public void exportTemplate(HttpServletResponse response) throws IOException {
    // 导出空模板，只包含表头
    ExcelUtils.write(response, "用户导入模板.xlsx", "用户导入", 
            UserExcelVO.class, Collections.emptyList());
}
```

### 4. 导入时数据校验

```java
@PostMapping("/import")
public void importExcel(@RequestParam("file") MultipartFile file) 
        throws IOException {
    List<UserExcelVO> vos = ExcelUtils.read(file, UserExcelVO.class);
    
    // 数据校验
    for (int i = 0; i < vos.size(); i++) {
        UserExcelVO vo = vos.get(i);
        if (vo.getUsername() == null) {
            throw new RuntimeException("第" + (i + 2) + "行：用户名不能为空");
        }
        if (vo.getMobile() == null) {
            throw new RuntimeException("第" + (i + 2) + "行：手机号不能为空");
        }
    }
    
    // 保存数据
    saveUsers(vos);
}
```

---

## 常见问题

### Q: Long 类型精度丢失？

A: ExcelUtils 已自动处理，使用 `LongStringConverter` 确保 Long 类型不会丢失精度。

### Q: 日期格式不正确？

A: 使用 `@DateTimeFormat` 注解指定格式：
```java
@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
@ExcelProperty("创建时间")
private LocalDateTime createTime;
```

### Q: 如何设置列宽？

A: 使用 `@ColumnWidth` 注解：
```java
@ColumnWidth(20)
@ExcelProperty("用户名称")
private String username;
```

### Q: 导出中文文件名乱码？

A: ExcelUtils 已自动使用 `HttpUtils.encodeUtf8()` 编码文件名。

### Q: 如何导出图片？

A: 需要自定义 `AbstractCellWriteHandler` 处理器，详见 FastExcel 官方文档。
