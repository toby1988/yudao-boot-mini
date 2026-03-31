# 异步任务指南

## 概述

框架基于 Spring Async 提供异步任务支持。

## 使用方式

### 1. 启用异步

框架已自动启用 `@EnableAsync`，无需手动配置。

### 2. 使用 @Async

```java
@Service
public class AsyncService {
    
    @Async
    public void asyncTask() {
        // 异步执行
        System.out.println("异步任务执行，线程：" + Thread.currentThread().getName());
    }
}
```

### 3. 带返回值的异步

```java
@Service
public class AsyncService {
    
    @Async
    public CompletableFuture<String> asyncTaskWithResult() {
        // 异步执行
        String result = doSomething();
        return CompletableFuture.completedFuture(result);
    }
}

// 调用
CompletableFuture<String> future = asyncService.asyncTaskWithResult();
String result = future.get();  // 阻塞等待结果
```

### 4. 配置线程池

```yaml
spring:
  task:
    execution:
      pool:
        core-size: 8                 # 核心线程数
        max-size: 16                 # 最大线程数
        queue-capacity: 100          # 队列容量
        keep-alive: 60s              # 线程空闲时间
      thread-name-prefix: async-     # 线程名前缀
```

---

## 使用场景

### 场景1：发送通知

```java
@Service
@Slf4j
public class NotificationService {
    
    @Async
    public void sendNotification(Long userId, String message) {
        try {
            // 发送通知（可能耗时）
            notificationClient.send(userId, message);
            log.info("[sendNotification][发送成功，userId={}]", userId);
        } catch (Exception e) {
            log.error("[sendNotification][发送失败，userId={}]", userId, e);
        }
    }
}
```

### 场景2：记录日志

```java
@Service
@Slf4j
public class LogService {
    
    @Async
    public void saveLog(String content) {
        LogDO log = new LogDO();
        log.setContent(content);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }
}
```

### 场景3：批量处理

```java
@Service
@Slf4j
public class BatchService {
    
    @Async
    public CompletableFuture<BatchResult> processBatch(List<DataDTO> dataList) {
        int successCount = 0;
        int failCount = 0;
        
        for (DataDTO data : dataList) {
            try {
                processOne(data);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("[processBatch][处理失败，data={}]", data, e);
            }
        }
        
        BatchResult result = new BatchResult(successCount, failCount);
        return CompletableFuture.completedFuture(result);
    }
}
```

---

## 注意事项

### 1. 自调用问题

```java
@Service
public class MyService {
    
    public void methodA() {
        this.methodB();  // ❌ 自调用，@Async 不生效
    }
    
    @Async
    public void methodB() {
        // 异步逻辑
    }
}

// 解决方案：注入自己
@Service
public class MyService {
    
    @Autowired
    private MyService self;
    
    public void methodA() {
        self.methodB();  // ✅ 通过代理调用
    }
    
    @Async
    public void methodB() {
        // 异步逻辑
    }
}
```

### 2. 异常处理

```java
@Service
public class AsyncService {
    
    @Async
    public void asyncTask() {
        try {
            // 业务逻辑
        } catch (Exception e) {
            // 异步任务的异常不会被全局异常处理器捕获
            // 需要手动处理
            log.error("[asyncTask][执行异常]", e);
        }
    }
}
```

### 3. 返回值

```java
// 无返回值
@Async
public void asyncTask() { }

// 有返回值
@Async
public CompletableFuture<String> asyncTaskWithResult() {
    return CompletableFuture.completedFuture("result");
}
```

---

## 配置参考

```yaml
spring:
  task:
    execution:
      pool:
        core-size: 8
        max-size: 16
        queue-capacity: 100
        keep-alive: 60s
        allow-core-thread-timeout: true
      thread-name-prefix: async-
      shutdown:
        await-termination: true
        await-termination-period: 30s
```
