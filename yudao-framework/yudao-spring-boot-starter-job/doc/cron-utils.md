# CronUtils 使用指南

## 概述

`CronUtils` 是 Quartz Cron 表达式工具类，提供 Cron 表达式验证和时间计算功能。

## API 方法

### isValid

验证 Cron 表达式是否有效。

```java
boolean valid = CronUtils.isValid("0 0 12 * * ?");
// true

boolean invalid = CronUtils.isValid("invalid cron");
// false
```

### getNextTimes

获取下 N 个满足执行的时间。

```java
List<LocalDateTime> nextTimes = CronUtils.getNextTimes("0 0 12 * * ?", 5);
// 返回未来 5 次中午 12 点的时间
```

---

## 使用方式

### 1. 验证 Cron 表达式

```java
@RestController
@RequestMapping("/api/job")
public class JobController {
    
    @PostMapping("/validate-cron")
    public CommonResult<Boolean> validateCron(@RequestBody Map<String, String> params) {
        String cronExpression = params.get("cronExpression");
        boolean valid = CronUtils.isValid(cronExpression);
        return CommonResult.success(valid);
    }
}
```

### 2. 获取下次执行时间

```java
@RestController
@RequestMapping("/api/job")
public class JobController {
    
    @GetMapping("/next-times")
    public CommonResult<List<LocalDateTime>> getNextTimes(
            @RequestParam String cronExpression,
            @RequestParam(defaultValue = "5") int count) {
        
        List<LocalDateTime> nextTimes = CronUtils.getNextTimes(cronExpression, count);
        return CommonResult.success(nextTimes);
    }
}
```

### 3. 显示预执行时间

```java
@Service
public class JobService {
    
    public JobVO getJobWithNextTimes(Long jobId) {
        JobDO job = jobMapper.selectById(jobId);
        
        JobVO vo = BeanUtils.toBean(job, JobVO.class);
        vo.setNextTimes(CronUtils.getNextTimes(job.getCronExpression(), 5));
        
        return vo;
    }
}
```

---

## Cron 表达式格式

### 格式说明

```
秒 分 时 日 月 周 年（可选）
│  │  │  │  │  │  │
│  │  │  │  │  │  └──── 年（可选）
│  │  │  │  │  └─────── 周（0-7，0和7都表示周日）
│  │  │  │  └────────── 月（1-12）
│  │  │  └───────────── 日（1-31）
│  │  └──────────────── 时（0-23）
│  └─────────────────── 分（0-59）
└────────────────────── 秒（0-59）
```

### 常用表达式

| 表达式 | 说明 |
|-------|------|
| `0 0 12 * * ?` | 每天中午 12 点 |
| `0 0/5 * * * ?` | 每 5 分钟 |
| `0 0 0 * * ?` | 每天凌晨 0 点 |
| `0 0 0 1 * ?` | 每月 1 号凌晨 |
| `0 0 0 ? * 1` | 每周日凌晨 |
| `0 0/30 9-17 * * ?` | 工作日 9-17 点每 30 分钟 |
| `0 0 0 1 1 ?` | 每年 1 月 1 日 |

### 特殊字符

| 字符 | 说明 |
|-----|------|
| `*` | 所有值 |
| `?` | 不指定（用于日和周） |
| `-` | 范围 |
| `,` | 多个值 |
| `/` | 步长 |

---

## 使用场景

### 场景1：任务配置验证

```java
@Service
public class JobService {
    
    public void createJob(JobCreateDTO dto) {
        // 验证 Cron 表达式
        if (!CronUtils.isValid(dto.getCronExpression())) {
            throw new BusinessException("Cron 表达式不正确");
        }
        
        // 创建任务
        JobDO job = BeanUtils.toBean(dto, JobDO.class);
        jobMapper.insert(job);
    }
}
```

### 场景2：显示下次执行时间

```java
@RestController
@RequestMapping("/api/job")
public class JobController {
    
    @GetMapping("/{id}")
    public JobVO getJob(@PathVariable Long id) {
        JobDO job = jobMapper.selectById(id);
        
        JobVO vo = BeanUtils.toBean(job, JobVO.class);
        
        // 获取下次执行时间
        if (CronUtils.isValid(job.getCronExpression())) {
            vo.setNextTimes(CronUtils.getNextTimes(job.getCronExpression(), 5));
        }
        
        return vo;
    }
}
```

### 场景3：Cron 表达式生成器

```java
// 前端可以实现 Cron 表达式生成器
// 后端使用 CronUtils 验证和预览
```

---

## 注意事项

### 1. 表达式验证

```java
// 使用前先验证
if (!CronUtils.isValid(cronExpression)) {
    throw new IllegalArgumentException("Cron 表达式不正确");
}
```

### 2. 时区问题

```java
// CronUtils 使用系统默认时区
// 如果需要其他时区，需要自行转换
```

### 3. 性能考虑

```java
// getNextTimes 会计算多个时间点
// 不要在高频调用的场景使用
```
