# API 参考

## IPUtils

IP 地址工具类，提供 IP 到地理位置的查询。

| 方法 | 返回类型 | 说明 |
|-----|---------|------|
| `getAreaId(String ip)` | Integer | 查询 IP 对应的地区编号 |
| `getAreaId(long ip)` | Integer | 查询 IP 对应的地区编号（整数形式） |
| `getArea(String ip)` | Area | 查询 IP 对应的地区信息 |
| `getArea(long ip)` | Area | 查询 IP 对应的地区信息（整数形式） |

---

## AreaUtils

区域工具类，提供区域数据的查询和操作。

| 方法 | 返回类型 | 说明 |
|-----|---------|------|
| `getArea(Integer id)` | Area | 根据 ID 获取区域 |
| `parseArea(String pathStr)` | Area | 根据路径解析区域 |
| `format(Integer id)` | String | 格式化区域（默认空格分隔） |
| `format(Integer id, String separator)` | String | 格式化区域（指定分隔符） |
| `getByType(AreaTypeEnum type, Function<Area, T> func)` | List\<T\> | 获取指定类型的区域列表 |
| `getParentIdByType(Integer id, AreaTypeEnum type)` | Integer | 获取指定类型的上级区域 ID |
| `getAreaNodePathList(List<Area> areas)` | List\<String\> | 获取所有节点的全路径 |

---

## Area

区域实体类。

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | Integer | 区域编号 |
| name | String | 区域名称 |
| type | Integer | 区域类型 |
| parent | Area | 父节点 |
| children | List\<Area\> | 子节点列表 |

| 常量 | 值 | 说明 |
|-----|---|------|
| ID_GLOBAL | 0 | 全球（根节点） |
| ID_CHINA | 1 | 中国 |

---

## AreaTypeEnum

区域类型枚举。

| 枚举值 | type | name | 说明 |
|-------|------|------|------|
| COUNTRY | 1 | 国家 | 国家级别 |
| PROVINCE | 2 | 省份 | 省级行政区 |
| CITY | 3 | 城市 | 地级市 |
| DISTRICT | 4 | 地区 | 区、县、县级市 |
