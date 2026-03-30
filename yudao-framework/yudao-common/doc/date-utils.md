# 日期工具类使用指南

## 概述

`DateUtils` 提供日期时间的常用操作。

## API 方法

### 类型转换

```java
// LocalDateTime -> Date
Date date = DateUtils.of(localDateTime);

// Date -> LocalDateTime
LocalDateTime localDateTime = DateUtils.of(date);
```

### 日期判断

```java
// 判断是否过期
boolean expired = DateUtils.isExpired(endTime);

// 判断是否今天
boolean isToday = DateUtils.isToday(localDateTime);

// 判断是否昨天
boolean isYesterday = DateUtils.isYesterday(localDateTime);
```

### 日期创建

```java
// 创建指定时间
Date date = DateUtils.buildTime(2024, 1, 1);  // 2024-01-01 00:00:00

// 创建指定时间（精确到秒）
Date date = DateUtils.buildTime(2024, 1, 1, 12, 30, 0);
```

### 日期比较

```java
// 获取最大值
Date max = DateUtils.max(date1, date2);
LocalDateTime max = DateUtils.max(time1, time2);
```

### 时间计算

```java
// 当前时间加上指定时长
Date future = DateUtils.addTime(Duration.ofHours(1));
```

## 常量

```java
DateUtils.TIME_ZONE_DEFAULT           // "GMT+8"
DateUtils.SECOND_MILLIS               // 1000
DateUtils.FORMAT_YEAR_MONTH_DAY       // "yyyy-MM-dd"
DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND  // "yyyy-MM-dd HH:mm:ss"
```

## 使用示例

### 场景1：查询今天的数据

```java
public List<OrderVO> getTodayOrders() {
    LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    
    QueryWrapper<Order> wrapper = new QueryWrapper<>();
    wrapper.ge("create_time", todayStart);
    
    return orderMapper.selectList(wrapper);
}
```

### 场景2：判断订单是否过期

```java
public boolean isOrderExpired(Order order) {
    return DateUtils.isExpired(order.getExpireTime());
}
```

### 场景3：格式化日期

```java
// 使用 Hutool
String dateStr = LocalDateTimeUtil.format(LocalDateTime.now(), 
    DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
```

### 场景4：时间范围查询

```java
public List<LogVO> getLogs(Date start, Date end) {
    QueryWrapper<Log> wrapper = new QueryWrapper<>();
    wrapper.between("create_time", 
        DateUtils.of(start), 
        DateUtils.of(end));
    return logMapper.selectList(wrapper);
}
```

## LocalDateTimeUtils

提供更多 LocalDateTime 操作：

```java
// 使用 Hutool 的 LocalDateTimeUtil
LocalDateTime now = LocalDateTime.now();

// 解析
LocalDateTime time = LocalDateTimeUtil.parse("2024-01-01 12:00:00");

// 格式化
String str = LocalDateTimeUtil.format(time, "yyyy-MM-dd HH:mm:ss");

// 计算
LocalDateTime tomorrow = now.plusDays(1);
LocalDateTime lastMonth = now.minusMonths(1);

// 计算差值
long days = LocalDateTimeUtil.between(start, end, ChronoUnit.DAYS);
```
