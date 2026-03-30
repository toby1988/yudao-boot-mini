# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-biz-ip</artifactId>
</dependency>
```

## 2. 基本使用

### 2.1 IP 地址查询

```java
import cn.iocoder.yudao.framework.ip.core.utils.IPUtils;
import cn.iocoder.yudao.framework.ip.core.Area;

public class IPExample {
    
    public static void main(String[] args) {
        // 查询 IP 对应的地区编号
        Integer areaId = IPUtils.getAreaId("127.0.0.1");
        System.out.println("地区编号: " + areaId);
        
        // 查询 IP 对应的地区信息
        Area area = IPUtils.getArea("127.0.0.1");
        System.out.println("地区名称: " + area.getName());
    }
}
```

### 2.2 区域信息查询

```java
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import cn.iocoder.yudao.framework.ip.core.Area;
import cn.iocoder.yudao.framework.ip.core.enums.AreaTypeEnum;

public class AreaExample {
    
    public static void main(String[] args) {
        // 根据 ID 获取区域
        Area area = AreaUtils.getArea(110105);  // 朝阳区
        System.out.println("区域名称: " + area.getName());
        
        // 格式化区域（显示完整路径）
        String formatted = AreaUtils.format(110105);  // 北京 北京市 朝阳区
        System.out.println("区域路径: " + formatted);
        
        // 获取所有省份
        List<Area> provinces = AreaUtils.getByType(
            AreaTypeEnum.PROVINCE, 
            Function.identity()
        );
        System.out.println("省份数量: " + provinces.size());
    }
}
```

## 3. 在 Web 应用中使用

### 3.1 获取用户 IP 并查询地区

```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @PostMapping("/user/login-log")
    public void logLogin(HttpServletRequest request) {
        // 获取用户 IP
        String ip = getClientIP(request);
        
        // 查询 IP 对应的地区
        Area area = IPUtils.getArea(ip);
        
        // 记录登录日志
        LoginLog log = new LoginLog();
        log.setIp(ip);
        log.setAreaId(area.getId());
        log.setAreaName(AreaUtils.format(area.getId()));
        
        loginLogService.save(log);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}
```

### 3.2 省市区级联选择

```java
@RestController
@RequestMapping("/api/area")
public class AreaController {
    
    @GetMapping("/provinces")
    public List<AreaVO> getProvinces() {
        return AreaUtils.getByType(AreaTypeEnum.PROVINCE, 
            area -> new AreaVO(area.getId(), area.getName()));
    }
    
    @GetMapping("/cities/{provinceId}")
    public List<AreaVO> getCities(@PathVariable Integer provinceId) {
        Area province = AreaUtils.getArea(provinceId);
        if (province == null || province.getChildren() == null) {
            return Collections.emptyList();
        }
        return CollectionUtils.convertList(province.getChildren(), 
            area -> new AreaVO(area.getId(), area.getName()));
    }
    
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
```

## 4. 常见 ID 参考

| 区域 ID | 区域名称 | 说明 |
|--------|---------|------|
| 0 | 全球 | 根节点 |
| 1 | 中国 | 国家节点 |
| 110000 | 北京 | 省级 |
| 110100 | 北京市 | 市级 |
| 110105 | 朝阳区 | 区级 |

## 5. 验证安装

启动应用后，查看日志：

```
[INFO] 启动加载 IPUtils 成功，耗时 (XX) 毫秒
[INFO] 启动加载 AreaUtils 成功，耗时 (XX) 毫秒
```

出现以上日志表示模块加载成功。
