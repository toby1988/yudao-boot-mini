# 分布式锁使用指南

## 概述

分布式锁保证同一时间只有一个线程执行某个方法，基于 Lock4j + Redisson 实现。

## 使用方式

### 1. 基础使用

```java
@Service
public class StockService {
    
    /**
     * 根据方法参数加锁
     */
    @Lock4j(keys = {"#productId"})
    public void deductStock(Long productId, Integer quantity) {
        // 同一时间只有一个线程执行
        Stock stock = stockMapper.selectById(productId);
        stock.setQuantity(stock.getQuantity() - quantity);
        stockMapper.updateById(stock);
    }
}
```

### 2. 复合 Key

```java
// 根据多个参数组合加锁
@Lock4j(keys = {"#userId", "#orderId"})
public void processOrder(Long userId, Long orderId) {
    // lock: userId:orderId
}
```

### 3. 自定义 Key 前缀

```java
@Lock4j(keys = {"#productId"}, name = "stock:deduct")
public void deductStock(Long productId) {
    // lock: stock:deduct:productId
}
```

### 4. 设置超时时间

```java
// 锁定 30 秒
@Lock4j(keys = {"#orderId"}, expire = 30000)
public void processOrder(Long orderId) {
    // 处理逻辑
}
```

### 5. 等待时间

```java
// 等待 5 秒获取锁
@Lock4j(keys = {"#id"}, acquireTimeout = 5000)
public void process(Long id) {
    // 处理逻辑
}
```

## 注解属性

| 属性 | 类型 | 默认值 | 说明 |
|-----|------|-------|------|
| keys | String[] | {} | 锁的 Key（支持 SpEL） |
| name | String | "" | 锁名称前缀 |
| expire | long | 30000 | 锁超时时间（毫秒） |
| acquireTimeout | long | 60000 | 获取锁超时时间（毫秒） |
| customLockTimeoutHandler | Class | DefaultLockFailureStrategy | 获取锁失败处理器 |

## 使用场景

### 场景1：库存扣减

```java
@Service
public class StockService {
    
    @Lock4j(keys = {"#productId"}, expire = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long productId, Integer quantity) {
        // 1. 检查库存
        Stock stock = stockMapper.selectById(productId);
        if (stock.getQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        // 2. 扣减库存
        stock.setQuantity(stock.getQuantity() - quantity);
        stockMapper.updateById(stock);
        
        // 3. 记录流水
        stockLogService.saveLog(productId, quantity);
    }
}
```

### 场景2：订单处理

```java
@Service
public class OrderService {
    
    @Lock4j(keys = {"#orderId"}, expire = 30000)
    public void processOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        
        // 防止重复处理
        if (OrderStatusEnum.PROCESSED.getStatus().equals(order.getStatus())) {
            return;
        }
        
        // 处理订单
        doProcess(order);
        
        // 更新状态
        order.setStatus(OrderStatusEnum.PROCESSED.getStatus());
        orderMapper.updateById(order);
    }
}
```

### 场景3：定时任务

```java
@Service
public class TaskService {
    
    /**
     * 定时任务互斥执行
     */
    @Lock4j(keys = {"'daily-report'"}, expire = 600000)
    public void generateDailyReport() {
        // 同一时间只有一个节点执行
        // 防止多节点重复生成报表
    }
}
```

### 场景4：用户操作

```java
@Service
public class UserService {
    
    @Lock4j(keys = {"#userId"}, expire = 5000)
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        // 防止同一用户并发更新
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(dto, user);
        userMapper.updateById(user);
    }
}
```

## 锁失败处理

### 默认策略

默认策略是直接抛出异常：

```java
public class DefaultLockFailureStrategy implements LockFailureStrategy {
    
    @Override
    public void onLockFailure(String key, Method method, Object[] args) {
        throw new ServiceException(GlobalErrorCodeConstants.LOCKED.getCode(), 
                "请求频繁，请稍后再试");
    }
}
```

### 自定义策略

```java
public class CustomLockFailureStrategy implements LockFailureStrategy {
    
    @Override
    public void onLockFailure(String key, Method method, Object[] args) {
        // 自定义处理逻辑
        log.warn("获取锁失败: {}", key);
        throw new BusinessException("系统繁忙，请稍后再试");
    }
}

// 使用自定义策略
@Lock4j(keys = {"#id"}, customLockTimeoutHandler = CustomLockFailureStrategy.class)
public void process(Long id) { }
```

## 实现原理

```
请求到达
    │
    ↓
@Lock4j 注解被识别
    │
    ↓
Lock4j 拦截器
    │
    ├→ 解析 keys（支持 SpEL）
    ├→ 生成锁 Key
    │
    ↓
Redisson 尝试获取锁
    │
    ├── 成功
    │       │
    │       ↓
    │   执行业务逻辑
    │       │
    │       ↓
    │   释放锁
    │
    └── 失败（超时）
            │
            ↓
        调用 LockFailureStrategy
        抛出异常或自定义处理
```

## 注意事项

### 1. 锁粒度

```java
// ✅ 推荐：细粒度锁
@Lock4j(keys = {"#productId"})
public void deductStock(Long productId) { }

// ❌ 不推荐：粗粒度锁
@Lock4j(keys = {"'stock'"})
public void deductStock(Long productId) { }
```

### 2. 锁超时时间

```java
// 根据业务复杂度设置合理的超时时间
@Lock4j(keys = {"#id"}, expire = 30000)  // 30 秒
public void complexProcess(Long id) { }
```

### 3. 异常处理

```java
@Lock4j(keys = {"#id"})
public void process(Long id) {
    try {
        // 业务逻辑
    } catch (Exception e) {
        // 异常处理
        // 锁会自动释放
        throw e;
    }
}
```

## 常见问题

### Q: 分布式锁不生效？

A: 检查以下几点：
1. 确认 Redisson 依赖已引入
2. 确认 Redis 连接正常
3. 确认注解在 public 方法上

### Q: 如何避免死锁？

A: 框架会自动设置锁超时时间，默认 30 秒后自动释放。

### Q: 锁粒度如何选择？

A: 根据业务场景选择：
- 库存扣减：按商品 ID 加锁
- 订单处理：按订单 ID 加锁
- 定时任务：按任务名称加锁
