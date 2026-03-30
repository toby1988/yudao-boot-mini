# IPUtils 使用指南

## 概述

`IPUtils` 是 IP 地址工具类，提供 IP 地址到地理位置的查询功能。

## 技术实现

- 基于 [ip2region](https://gitee.com/zhijiantianya/ip2region) 项目
- 使用 xdb 格式的精简版数据文件
- 数据文件加载到内存，提供毫秒级查询速度

## API 方法

### getAreaId(String ip)

查询 IP 对应的地区编号。

```java
Integer areaId = IPUtils.getAreaId("192.168.1.1");
// 返回: 地区编号，如 2163（对应某个区域）
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| ip | String | IP 地址，格式为 "192.168.1.1" |

**返回值：**

| 类型 | 说明 |
|-----|------|
| Integer | 地区编号 |

**异常：**

- 当 IP 格式错误时，可能抛出异常

---

### getAreaId(long ip)

查询 IP 对应的地区编号（使用 IP 的整数形式）。

```java
long ipLong = ipToLong("192.168.1.1");
Integer areaId = IPUtils.getAreaId(ipLong);
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| ip | long | IP 地址的整数形式 |

---

### getArea(String ip)

查询 IP 对应的地区信息。

```java
Area area = IPUtils.getArea("192.168.1.1");
System.out.println(area.getName());  // 如: 上海
```

**参数说明：**

| 参数 | 类型 | 说明 |
|-----|------|------|
| ip | String | IP 地址，格式为 "192.168.1.1" |

**返回值：**

| 类型 | 说明 |
|-----|------|
| Area | 区域信息对象，包含 id、name、type、parent、children |

---

### getArea(long ip)

查询 IP 对应的地区信息（使用 IP 的整数形式）。

```java
long ipLong = ipToLong("192.168.1.1");
Area area = IPUtils.getArea(ipLong);
```

## 使用示例

### 示例1：获取客户端 IP 并查询地区

```java
@Slf4j
@Service
public class LoginService {
    
    public void recordLoginLog(HttpServletRequest request, User user) {
        // 1. 获取客户端 IP
        String ip = getClientIP(request);
        
        // 2. 查询 IP 对应的地区
        Integer areaId = IPUtils.getAreaId(ip);
        Area area = IPUtils.getArea(ip);
        
        // 3. 记录登录日志
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setIp(ip);
        loginLog.setAreaId(areaId);
        loginLog.setAreaName(area != null ? area.getName() : "未知");
        
        loginLogMapper.insert(loginLog);
        
        log.info("用户 {} 从 {} 登录，地区：{}", 
                user.getUsername(), ip, area != null ? area.getName() : "未知");
    }
    
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能有多个 IP，取第一个
        return ip.split(",")[0].trim();
    }
}
```

### 示例2：批量查询 IP 对应地区

```java
@Service
public class IPBatchService {
    
    public Map<String, Area> batchGetArea(List<String> ips) {
        Map<String, Area> result = new HashMap<>();
        for (String ip : ips) {
            try {
                Area area = IPUtils.getArea(ip);
                result.put(ip, area);
            } catch (Exception e) {
                log.warn("查询 IP {} 的地区信息失败", ip, e);
                result.put(ip, null);
            }
        }
        return result;
    }
}
```

### 示例3：IP 统计分析

```java
@Service
public class IPStatService {
    
    public Map<String, Integer> statByProvince(List<LoginLog> logs) {
        Map<String, Integer> stat = new HashMap<>();
        
        for (LoginLog log : logs) {
            String ip = log.getIp();
            Integer areaId = IPUtils.getAreaId(ip);
            
            // 获取省份
            Integer provinceId = AreaUtils.getParentIdByType(areaId, AreaTypeEnum.PROVINCE);
            if (provinceId != null) {
                String provinceName = AreaUtils.getArea(provinceId).getName();
                stat.merge(provinceName, 1, Integer::sum);
            }
        }
        
        return stat;
    }
}
```

## 注意事项

### 1. 内存占用

IP 数据库加载到内存，大约占用 **5-10MB** 内存空间。

### 2. 启动耗时

首次加载数据文件需要一定时间，通常在 **100-500ms** 内完成。

### 3. IP 格式要求

```java
// ✅ 正确的 IP 格式
IPUtils.getAreaId("192.168.1.1");
IPUtils.getAreaId("10.0.0.1");

// ❌ 错误的 IP 格式（会抛出异常）
IPUtils.getAreaId("192.168.1");  // 不完整
IPUtils.getAreaId("localhost");  // 不是 IP 格式
```

### 4. 本地 IP 处理

```java
// 本地 IP（127.0.0.1）可能返回 null 或默认值
Area area = IPUtils.getArea("127.0.0.1");
if (area == null) {
    log.debug("本地 IP，跳过地区查询");
}
```

### 5. 异常处理

```java
public Area safeGetArea(String ip) {
    try {
        return IPUtils.getArea(ip);
    } catch (Exception e) {
        log.warn("查询 IP {} 地区信息失败", ip, e);
        return null;
    }
}
```

## 常见问题

### Q: 为什么返回的 Area 对象为 null？

A: 可能的原因：
1. IP 格式错误
2. IP 数据库文件缺失
3. 本地/内网 IP 没有映射到地区

### Q: 如何处理内网 IP？

A: 内网 IP 通常没有对应的城市信息，建议：
```java
public String getAreaName(String ip) {
    if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
        return "内网";
    }
    Area area = IPUtils.getArea(ip);
    return area != null ? area.getName() : "未知";
}
```

### Q: 数据文件在哪里？

A: 数据文件位于 `src/main/resources/ip2region.xdb`，启动时自动加载。
