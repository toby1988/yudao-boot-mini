# AreaUtils 使用指南

## 概述

`AreaUtils` 是区域工具类，提供中国行政区划数据的查询和操作功能。

## 技术实现

- 数据来源：[Administrative-divisions-of-China](https://github.com/modood/Administrative-divisions-of-China)
- 数据格式：CSV 文件（`area.csv`）
- 数据结构：树形结构，包含 省 -> 市 -> 区 三级数据

## API 方法

### getArea(Integer id)

根据区域编号获取区域信息。

```java
Area area = AreaUtils.getArea(110105);
System.out.println(area.getName());  // 朝阳区
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | Integer | 区域编号 |

**返回值：**

| 类型 | 说明 |
|-----|------|
| Area | 区域信息对象，不存在时返回 null |

---

### parseArea(String pathStr)

根据路径字符串解析区域。

```java
Area area = AreaUtils.parseArea("河南省/开封市/祥符区");
System.out.println(area.getId());  // 祥符区的 ID
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| pathStr | String | 区域路径，格式：`省份/城市/区县` |

---

### format(Integer id)

格式化区域信息（显示完整路径）。

```java
String result = AreaUtils.format(110105);  // 北京 北京市 朝阳区
```

---

### format(Integer id, String separator)

使用指定分隔符格式化区域信息。

```java
String result = AreaUtils.format(110105, "/");  // 北京/北京市/朝阳区
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | Integer | 区域编号 |
| separator | String | 分隔符，默认为 " " |

---

### getByType(AreaTypeEnum type, Function<Area, T> func)

获取指定类型的区域列表。

```java
// 获取所有省份
List<Area> provinces = AreaUtils.getByType(AreaTypeEnum.PROVINCE, Function.identity());

// 获取所有省份名称
List<String> provinceNames = AreaUtils.getByType(AreaTypeEnum.PROVINCE, Area::getName);

// 获取所有城市
List<Area> cities = AreaUtils.getByType(AreaTypeEnum.CITY, Function.identity());
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| type | AreaTypeEnum | 区域类型（COUNTRY/PROVINCE/CITY/DISTRICT） |
| func | Function<Area, T> | 转换函数 |

---

### getParentIdByType(Integer id, AreaTypeEnum type)

根据区域编号和上级区域类型，获取上级区域编号。

```java
// 获取朝阳区所在省份的 ID
Integer provinceId = AreaUtils.getParentIdByType(110105, AreaTypeEnum.PROVINCE);
System.out.println(provinceId);  // 110000 (北京)

// 获取朝阳区所在城市的 ID
Integer cityId = AreaUtils.getParentIdByType(110105, AreaTypeEnum.CITY);
System.out.println(cityId);  // 110100 (北京市)
```

---

### getAreaNodePathList(List<Area> areas)

获取所有节点的全路径名称。

```java
List<Area> provinces = AreaUtils.getByType(AreaTypeEnum.PROVINCE, Function.identity());
List<String> paths = AreaUtils.getAreaNodePathList(provinces);
// 返回: ["北京", "北京/北京市", "北京/北京市/东城区", ...]
```

## 使用示例

### 示例1：省市区级联选择器

```java
@RestController
@RequestMapping("/api/area")
public class AreaController {
    
    /**
     * 获取所有省份
     */
    @GetMapping("/provinces")
    public List<AreaVO> getProvinces() {
        return AreaUtils.getByType(AreaTypeEnum.PROVINCE, 
            area -> new AreaVO(area.getId(), area.getName()));
    }
    
    /**
     * 根据省份获取城市列表
     */
    @GetMapping("/cities/{provinceId}")
    public List<AreaVO> getCities(@PathVariable Integer provinceId) {
        Area province = AreaUtils.getArea(provinceId);
        if (province == null || province.getChildren() == null) {
            return Collections.emptyList();
        }
        return CollectionUtils.convertList(province.getChildren(), 
            area -> new AreaVO(area.getId(), area.getName()));
    }
    
    /**
     * 根据城市获取区县列表
     */
    @GetMapping("/districts/{cityId}")
    public List<AreaVO> getDistricts(@PathVariable Integer cityId) {
        Area city = AreaUtils.getArea(cityId);
        if (city == null || city.getChildren() == null) {
            return Collections.emptyList();
        }
        return CollectionUtils.convertList(city.getChildren(), 
            area -> new AreaVO(area.getId(), area.getName()));
    }
}

@Data
@AllArgsConstructor
public class AreaVO {
    private Integer id;
    private String name;
}
```

### 示例2：格式化用户地址

```java
@Service
public class AddressService {
    
    public String formatUserAddress(UserAddress address) {
        StringBuilder sb = new StringBuilder();
        
        // 格式化省市区
        String areaStr = AreaUtils.format(address.getAreaId(), " ");
        sb.append(areaStr);
        
        // 添加详细地址
        sb.append(" ").append(address.getAddress());
        
        return sb.toString();
    }
}
```

### 示例3：获取用户所在省份

```java
@Service
public class UserService {
    
    public String getUserProvince(Integer areaId) {
        Integer provinceId = AreaUtils.getParentIdByType(areaId, AreaTypeEnum.PROVINCE);
        if (provinceId == null) {
            return "未知";
        }
        Area province = AreaUtils.getArea(provinceId);
        return province != null ? province.getName() : "未知";
    }
    
    /**
     * 统计用户地域分布
     */
    public Map<String, Long> statUserByProvince(List<User> users) {
        Map<String, Long> stat = new HashMap<>();
        for (User user : users) {
            String province = getUserProvince(user.getAreaId());
            stat.merge(province, 1L, Long::sum);
        }
        return stat;
    }
}
```

### 示例4：导出区域数据

```java
@Service
public class AreaExportService {
    
    public void exportAreaTree(HttpServletResponse response) {
        // 获取所有省份
        List<Area> provinces = AreaUtils.getByType(AreaTypeEnum.PROVINCE, Function.identity());
        
        List<AreaExportVO> list = new ArrayList<>();
        for (Area province : provinces) {
            for (Area city : province.getChildren()) {
                for (Area district : city.getChildren()) {
                    list.add(new AreaExportVO(
                        district.getId(),
                        province.getName(),
                        city.getName(),
                        district.getName(),
                        AreaUtils.format(district.getId(), "/")
                    ));
                }
            }
        }
        
        // 导出 Excel
        ExcelUtils.export(response, "区域数据", list, AreaExportVO.class);
    }
}

@Data
public class AreaExportVO {
    @ExcelProperty("区域编号")
    private Integer id;
    
    @ExcelProperty("省份")
    private String province;
    
    @ExcelProperty("城市")
    private String city;
    
    @ExcelProperty("区县")
    private String district;
    
    @ExcelProperty("完整路径")
    private String fullPath;
}
```

## 区域类型枚举

| 类型 | 说明 | 示例 |
|-----|------|------|
| COUNTRY | 国家 | 中国 |
| PROVINCE | 省份 | 北京、上海、河南 |
| CITY | 城市 | 北京市、开封市 |
| DISTRICT | 区县 | 朝阳区、祥符区 |

## 常见区域 ID 参考

### 直辖市

| ID | 名称 | 说明 |
|-----|------|------|
| 110000 | 北京 | 省级 |
| 120000 | 天津 | 省级 |
| 310000 | 上海 | 省级 |
| 500000 | 重庆 | 省级 |

### 热门城市

| ID | 名称 | 上级 |
|-----|------|------|
| 110100 | 北京市 | 北京 |
| 310100 | 上海市 | 上海 |
| 440100 | 广州市 | 广东省 |
| 440300 | 深圳市 | 广东省 |

## 注意事项

### 1. 内存占用

区域数据加载到内存，大约占用 **2-3MB** 内存空间。

### 2. ID 查找

```java
// 查找区域
Area area = AreaUtils.getArea(110105);
if (area == null) {
    log.warn("区域 ID 不存在: {}", areaId);
}
```

### 3. 路径解析

```java
// 路径必须从最顶层开始
Area area = AreaUtils.parseArea("河南省/开封市/祥符区");  // ✅ 正确
Area area = AreaUtils.parseArea("开封市/祥符区");        // ❌ 可能找不到
```

## 常见问题

### Q: 如何获取所有省份？

```java
List<Area> provinces = AreaUtils.getByType(AreaTypeEnum.PROVINCE, Function.identity());
```

### Q: 如何判断一个区域是否在中国？

```java
public boolean isInChina(Integer areaId) {
    Integer countryId = AreaUtils.getParentIdByType(areaId, AreaTypeEnum.COUNTRY);
    return Area.ID_CHINA.equals(countryId);
}
```

### Q: 如何获取区域的完整路径？

```java
String path = AreaUtils.format(areaId, "/");  // 北京/北京市/朝阳区
```
