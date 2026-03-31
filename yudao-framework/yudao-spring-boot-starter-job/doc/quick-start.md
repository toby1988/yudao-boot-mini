# 快速开始

## 1. 引入依赖

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-spring-boot-starter-job</artifactId>
</dependency>
```

## 2. 配置

```yaml
# application.yml
spring:
  quartz:
    job-store-type: jdbc                # 使用 JDBC 存储
    wait-for-jobs-to-complete-on-shutdown: true  # 关闭时等待任务完成
    overwrite-existing-jobs: true       # 覆盖已存在的任务
    properties:
      org.quartz:
        threadPool:
          threadCount: 10               # 线程池大小
```

## 3. 实现 JobHandler

```java
@Component
public class DemoJobHandler implements JobHandler {
    
    @Override
    public String execute(String param) throws Exception {
        System.out.println("执行任务，参数：" + param);
        return "执行成功";
    }
}
```

## 4. 使用 CronUtils

```java
// 验证 Cron 表达式
boolean valid = CronUtils.isValid("0 0 12 * * ?");

// 获取下次执行时间
List<LocalDateTime> nextTimes = CronUtils.getNextTimes("0 0 12 * * ?", 5);
```

## 5. 异步任务

```java
@Service
public class AsyncService {
    
    @Async
    public void asyncTask() {
        // 异步执行
        System.out.println("异步任务执行");
    }
}
```
