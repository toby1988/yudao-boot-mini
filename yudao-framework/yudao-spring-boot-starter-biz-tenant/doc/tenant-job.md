# 多租户定时任务指南

## 概述

`@TenantJob` 注解用于标记定时任务，自动遍历所有租户执行。

## 工作原理

```
定时任务触发
    │
    ↓
@TenantJob 被识别
    │
    ↓
TenantJobAspect 拦截
    │
    ├→ 获取所有租户（忽略租户查询）
    │
    ├→ 遍历每个租户
    │       │
    │       ├→ 设置租户上下文
    │       │
    │       └→ 执行任务逻辑
    │
    └→ 清理上下文
```

## 使用方式

### 1. 基础使用

```java
@Component
public class UserJob {
    
    @TenantJob
    @XxlJob("syncUserJob")
    public void syncUser() {
        // 自动遍历所有租户执行
        // 每个租户都会执行一次这个方法
        List<UserDO> users = userMapper.selectList(null);
        // 处理逻辑
    }
}
```

### 2. 手动遍历租户

```java
@Component
public class OrderJob {
    
    @XxlJob("syncOrderJob")
    public void syncOrder() {
        // 获取所有租户
        List<TenantDO> tenants = TenantUtils.executeIgnore(() -> {
            return tenantMapper.selectList(null);
        });
        
        // 遍历执行
        for (TenantDO tenant : tenants) {
            TenantUtils.execute(tenant.getId(), () -> {
                // 在当前租户上下文中执行
                List<OrderDO> orders = orderMapper.selectList(null);
                // 处理逻辑
            });
        }
    }
}
```

---

## 使用场景

### 场景1：订单超时取消

```java
@Component
public class OrderJob {
    
    @TenantJob
    @XxlJob("cancelTimeoutOrder")
    public void cancelTimeoutOrder() {
        // 查询超时未支付的订单
        List<OrderDO> orders = orderMapper.selectTimeoutOrders();
        
        // 取消订单
        for (OrderDO order : orders) {
            orderService.cancel(order.getId());
        }
    }
}
```

### 场景2：数据统计

```java
@Component
public class StatisticsJob {
    
    @TenantJob
    @XxlJob("dailyStatistics")
    public void dailyStatistics() {
        // 统计今日数据
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        Integer todayOrderCount = orderMapper.countByCreateTimeAfter(today);
        Integer todayUserCount = userMapper.countByCreateTimeAfter(today);
        
        // 保存统计结果
        StatisticsDO stats = new StatisticsDO();
        stats.setOrderCount(todayOrderCount);
        stats.setUserCount(todayUserCount);
        statisticsMapper.insert(stats);
    }
}
```

### 场景3：缓存刷新

```java
@Component
public class CacheJob {
    
    @TenantJob
    @XxlJob("refreshCache")
    public void refreshCache() {
        // 刷新字典缓存
        dictService.refreshCache();
        
        // 刷新配置缓存
        configService.refreshCache();
        
        // 刷新菜单缓存
        menuService.refreshCache();
    }
}
```

### 场景4：数据同步

```java
@Component
public class SyncJob {
    
    @TenantJob
    @XxlJob("syncUserData")
    public void syncUserData() {
        // 同步用户数据到第三方系统
        List<UserDO> users = userMapper.selectList(null);
        
        for (UserDO user : users) {
            thirdPartyService.syncUser(user);
        }
    }
}
```

---

## 完整示例

```java
@Slf4j
@Component
public class OrderTimeoutJob {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 每分钟执行一次，取消超时未支付的订单
     */
    @TenantJob
    @XxlJob("cancelTimeoutOrder")
    public void cancelTimeoutOrder() {
        log.info("[cancelTimeoutOrder][开始执行]");
        
        // 查询超时 30 分钟的订单
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);
        List<OrderDO> timeoutOrders = orderMapper.selectByStatusAndCreateTimeBefore(
            OrderStatusEnum.UNPAID.getStatus(), timeout
        );
        
        log.info("[cancelTimeoutOrder][查询到 {} 条超时订单]", timeoutOrders.size());
        
        // 取消订单
        for (OrderDO order : timeoutOrders) {
            try {
                orderService.cancel(order.getId());
                log.info("[cancelTimeoutOrder][取消订单成功，orderId={}]", order.getId());
            } catch (Exception e) {
                log.error("[cancelTimeoutOrder][取消订单失败，orderId={}]", order.getId(), e);
            }
        }
        
        log.info("[cancelTimeoutOrder][执行完成]");
    }
}
```

---

## 注意事项

### 1. 租户数量

```java
// 如果租户数量较多，任务执行时间会较长
// 建议：
// 1. 并行处理（使用线程池）
// 2. 分批处理
// 3. 优化查询条件
```

### 2. 异常处理

```java
@TenantJob
@XxlJob("syncData")
public void syncData() {
    try {
        // 业务逻辑
    } catch (Exception e) {
        log.error("[syncData][执行异常]", e);
        // 记录异常，不影响其他租户执行
    }
}
```

### 3. 性能优化

```java
@TenantJob
@XxlJob("processData")
public void processData() {
    // 使用分页查询，避免内存溢出
    int pageNo = 1;
    int pageSize = 1000;
    
    while (true) {
        List<DataDO> list = dataMapper.selectPage(pageNo, pageSize);
        if (list.isEmpty()) {
            break;
        }
        
        // 处理数据
        processBatch(list);
        
        pageNo++;
    }
}
```
