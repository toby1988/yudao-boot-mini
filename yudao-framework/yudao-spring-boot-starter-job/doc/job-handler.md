# JobHandler 使用指南

## 概述

`JobHandler` 是定时任务处理器接口，所有定时任务都需要实现此接口。

## 接口定义

```java
public interface JobHandler {
    
    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    String execute(String param) throws Exception;
}
```

## 使用方式

### 1. 实现 JobHandler

```java
@Component
public class OrderTimeoutJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        // 查询超时订单
        List<OrderDO> timeoutOrders = orderMapper.selectTimeoutOrders();
        
        // 取消订单
        for (OrderDO order : timeoutOrders) {
            orderService.cancel(order.getId());
        }
        
        return "取消了 " + timeoutOrders.size() + " 个超时订单";
    }
}
```

### 2. 带参数的 Job

```java
@Component
public class SyncDataJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        // 解析参数
        JSONObject params = JSON.parseObject(param);
        String dataType = params.getString("dataType");
        Integer pageSize = params.getIntValue("pageSize");
        
        // 根据参数执行不同逻辑
        switch (dataType) {
            case "user":
                syncUserData(pageSize);
                break;
            case "order":
                syncOrderData(pageSize);
                break;
        }
        
        return "同步完成";
    }
}
```

### 3. 异常处理

```java
@Component
public class RiskyJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        try {
            // 业务逻辑
            doSomething();
            return "执行成功";
        } catch (BusinessException e) {
            // 业务异常，返回错误信息
            return "业务异常：" + e.getMessage();
        } catch (Exception e) {
            // 系统异常，抛出让框架记录
            throw new RuntimeException("任务执行失败", e);
        }
    }
}
```

---

## 使用场景

### 场景1：定时清理

```java
@Component
@Slf4j
public class CleanupJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        // 清理 30 天前的日志
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        int count = logMapper.deleteByCreateTimeBefore(threshold);
        
        log.info("[CleanupJobHandler][清理了 {} 条日志]", count);
        return "清理了 " + count + " 条日志";
    }
}
```

### 场景2：定时统计

```java
@Component
@Slf4j
public class StatisticsJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        // 统计今日数据
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        Integer orderCount = orderMapper.countByCreateTimeAfter(today);
        Integer userCount = userMapper.countByCreateTimeAfter(today);
        BigDecimal orderAmount = orderMapper.sumAmountByCreateTimeAfter(today);
        
        // 保存统计结果
        StatisticsDO stats = new StatisticsDO();
        stats.setOrderCount(orderCount);
        stats.setUserCount(userCount);
        stats.setOrderAmount(orderAmount);
        statisticsMapper.insert(stats);
        
        return "统计完成";
    }
}
```

### 场景3：定时同步

```java
@Component
@Slf4j
public class SyncJobHandler implements JobHandler {
    
    @Autowired
    private ThirdPartyService thirdPartyService;
    
    @Override
    public String execute(String param) throws Exception {
        // 同步用户数据到第三方
        List<UserDO> users = userMapper.selectList(null);
        
        int successCount = 0;
        int failCount = 0;
        
        for (UserDO user : users) {
            try {
                thirdPartyService.syncUser(user);
                successCount++;
            } catch (Exception e) {
                log.error("[SyncJobHandler][同步用户失败，userId={}]", user.getId(), e);
                failCount++;
            }
        }
        
        return String.format("同步完成，成功：%d，失败：%d", successCount, failCount);
    }
}
```

---

## 注意事项

### 1. Bean 注册

```java
// 必须使用 @Component 注解注册为 Spring Bean
@Component
public class MyJobHandler implements JobHandler { }
```

### 2. 线程安全

```java
// JobHandler 是单例的，注意线程安全
@Component
public class MyJobHandler implements JobHandler {
    
    // ❌ 不推荐：共享变量
    private int count = 0;
    
    @Override
    public String execute(String param) throws Exception {
        // ❌ 不推荐
        count++;
        
        // ✅ 推荐：使用局部变量
        int localCount = 0;
        localCount++;
        
        return "执行完成";
    }
}
```

### 3. 执行时间

```java
// 避免任务执行时间过长
// 如果任务耗时较长，考虑分批处理
@Override
public String execute(String param) throws Exception {
    int pageNo = 1;
    int pageSize = 1000;
    
    while (true) {
        List<DataDO> list = dataMapper.selectPage(pageNo, pageSize);
        if (list.isEmpty()) {
            break;
        }
        
        // 分批处理
        processBatch(list);
        pageNo++;
    }
    
    return "处理完成";
}
```
