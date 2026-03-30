# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-excel</artifactId>
</dependency>
```

## 2. 定义数据模型

```java
@Data
public class UserExcelVO {
    
    @ExcelProperty("用户编号")
    private Long id;
    
    @ExcelProperty("用户名称")
    private String username;
    
    @ExcelProperty("手机号")
    private String mobile;
    
    @ExcelProperty("邮箱")
    private String email;
    
    @DictFormat("sys_user_sex")  // 字典转换
    @ExcelProperty("性别")
    private Integer sex;
    
    @ColumnWidth(20)  // 列宽
    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

## 3. 导出 Excel

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 查询数据
        List<UserDO> users = userService.list();
        
        // 2. 转换为 VO
        List<UserExcelVO> vos = BeanUtils.toBean(users, UserExcelVO.class);
        
        // 3. 导出 Excel
        ExcelUtils.write(response, "用户列表.xlsx", "用户列表", 
                UserExcelVO.class, vos);
    }
}
```

## 4. 导入 Excel

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) 
            throws IOException {
        // 1. 读取 Excel
        List<UserExcelVO> vos = ExcelUtils.read(file, UserExcelVO.class);
        
        // 2. 转换为 DO
        List<UserDO> users = BeanUtils.toBean(vos, UserDO.class);
        
        // 3. 保存数据
        userService.saveBatch(users);
    }
}
```

## 5. 完整示例

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderExcelVO {
    
    @ExcelProperty("订单编号")
    private String orderNo;
    
    @ExcelProperty("商品名称")
    private String productName;
    
    @ExcelProperty("数量")
    private Integer quantity;
    
    @MoneyConvert  // 金额转换：分 -> 元
    @ExcelProperty("金额(元)")
    private Integer amount;
    
    @DictFormat("sys_order_status")  // 字典转换
    @ExcelProperty("状态")
    private Integer status;
    
    @AreaConvert  // 地区转换
    @ExcelProperty("地区")
    private Integer areaId;
}

// 导出
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    List<OrderExcelVO> vos = getOrderData();
    ExcelUtils.write(response, "订单列表.xlsx", "订单列表", 
            OrderExcelVO.class, vos);
}

// 导入
@PostMapping("/import")
public void importExcel(@RequestParam("file") MultipartFile file) throws IOException {
    List<OrderExcelVO> vos = ExcelUtils.read(file, OrderExcelVO.class);
    processOrderData(vos);
}
```

## 6. 验证

运行导出功能，下载的 Excel 文件应该包含：
- 所有定义的列
- 字典值显示为标签
- 金额显示为元
- 地区显示为名称
