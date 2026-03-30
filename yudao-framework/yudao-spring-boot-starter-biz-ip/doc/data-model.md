# 数据模型说明

## Area 区域实体

### 类定义

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Area {
    
    /**
     * 编号 - 全球，即根目录
     */
    public static final Integer ID_GLOBAL = 0;
    
    /**
     * 编号 - 中国
     */
    public static final Integer ID_CHINA = 1;
    
    /**
     * 编号
     */
    private Integer id;
    
    /**
     * 名字
     */
    private String name;
    
    /**
     * 类型
     * @see AreaTypeEnum
     */
    private Integer type;
    
    /**
     * 父节点
     */
    private Area parent;
    
    /**
     * 子节点
     */
    private List<Area> children;
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | Integer | 区域编号 |
| name | String | 区域名称 |
| type | Integer | 区域类型（1-国家 2-省份 3-城市 4-区县） |
| parent | Area | 父节点（JSON 序列化时使用 @JsonManagedReference） |
| children | Area[] | 子节点列表（JSON 序列化时使用 @JsonBackReference） |

### 常量

| 常量 | 值 | 说明 |
|-----|---|------|
| ID_GLOBAL | 0 | 全球（根节点） |
| ID_CHINA | 1 | 中国 |

### JSON 序列化

```json
{
  "id": 110105,
  "name": "朝阳区",
  "type": 4,
  "parent": null
}
```

> 注意：children 字段使用 @JsonBackReference 注解，序列化时会被忽略，防止循环引用。

---

## AreaTypeEnum 区域类型枚举

### 枚举定义

```java
@AllArgsConstructor
@Getter
public enum AreaTypeEnum implements ArrayValuable<Integer> {
    
    COUNTRY(1, "国家"),
    PROVINCE(2, "省份"),
    CITY(3, "城市"),
    DISTRICT(4, "地区"),
    ;
    
    private final Integer type;
    private final String name;
}
```

### 枚举值

| 枚举值 | type | name | 说明 |
|-------|------|------|------|
| COUNTRY | 1 | 国家 | 国家级别 |
| PROVINCE | 2 | 省份 | 省级行政区 |
| CITY | 3 | 城市 | 地级市 |
| DISTRICT | 4 | 地区 | 区、县、县级市 |

---

## 数据结构示例

### 树形结构

```
全球 (0)
└── 中国 (1)
    ├── 北京 (110000) [PROVINCE]
    │   └── 北京市 (110100) [CITY]
    │       ├── 东城区 (110101) [DISTRICT]
    │       ├── 西城区 (110102) [DISTRICT]
    │       ├── 朝阳区 (110105) [DISTRICT]
    │       └── ...
    ├── 上海 (310000) [PROVINCE]
    │   └── 上海市 (310100) [CITY]
    │       ├── 黄浦区 (310101) [DISTRICT]
    │       ├── 徐汇区 (310104) [DISTRICT]
    │       └── ...
    └── ...
```

### ID 规则

| 位数 | 示例 | 说明 |
|-----|------|------|
| 6 位 | 110000 | 省级（后4位为0） |
| 6 位 | 110100 | 市级（后2位为0） |
| 6 位 | 110105 | 区级 |

### 常用区域 ID

| ID | 名称 | 类型 |
|-----|------|------|
| 0 | 全球 | 根节点 |
| 1 | 中国 | 国家 |
| 110000 | 北京 | 省份 |
| 310000 | 上海 | 省份 |
| 440000 | 广东 | 省份 |
| 440100 | 广州 | 城市 |
| 440300 | 深圳 | 城市 |
| 110105 | 朝阳区 | 区县 |

---

## 数据来源

### ip2region.xdb

- **用途**：IP 地址查询
- **格式**：xdb 二进制格式
- **大小**：约 7MB
- **位置**：`src/main/resources/ip2region.xdb`

### area.csv

- **用途**：区域数据
- **格式**：CSV 文件
- **大小**：约 100KB
- **位置**：`src/main/resources/area.csv`
- **格式**：`id,name,type,parentId`

```
id,name,type,parentId
110000,北京,2,1
110100,北京市,3,110000
110101,东城区,4,110100
```

---

## 数据加载

### 加载时机

- 应用启动时自动加载
- 加载到内存，启动后不再读取文件

### 加载顺序

1. 加载 area.csv
2. 解析 CSV 行数据
3. 构建 Area 对象
4. 构建父子关系

### 内存占用

| 数据 | 占用空间 |
|-----|---------|
| Area 数据 | ~2MB |
| ip2region 数据 | ~5MB |
| **总计** | ~7MB |
