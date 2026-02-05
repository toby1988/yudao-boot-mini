# AkTools 生产级数据采集框架

## 架构概述

本框架采用大厂生产级代码标准，基于策略模式和六边形架构设计，提供了高可用、高性能的AkTools数据采集解决方案。

## 核心特性

### 1. 策略模式设计
- **统一接口**: `AkToolsDataStrategy<T>` 定义标准的数据采集策略
- **策略工厂**: `AkToolsStrategyFactory` 负责策略的注册和管理
- **可扩展性**: 易于添加新的数据接口和采集策略

### 2. 数据处理优化
- **批量处理**: `StockDataBatchProcessor` 支持并行数据转换
- **重试机制**: 内置指数退避重试策略
- **数据转换器**: `StockSpotDataConverter` 专门处理数据格式转换

### 3. 监控与运维
- **指标收集**: `AkToolsMetricsCollector` 提供详细的采集指标
- **日志规范**: 统一的日志格式和级别管理
- **健康检查**: 采集成功率、转换成功率等关键指标

### 4. 配置管理
- **灵活配置**: `AkToolsCollectConfig` 支持多种配置参数
- **环境适配**: 支持不同环境的参数调优
- **动态调整**: 运行时可调整重试次数、超时时间等

## 目录结构

```
service/aktools/
├── converter/                    # 数据转换器
│   ├── StockSpotDataConverter.java     # 股票行情数据转换器
│   └── StockDataBatchProcessor.java    # 批量数据处理器
├── factory/                      # 策略工厂
│   └── AkToolsStrategyFactory.java     # 策略管理工厂
├── impl/                         # 服务实现
│   └── AkToolsDataServiceImpl.java   # 生产级服务实现
├── metrics/                      # 监控指标
│   └── AkToolsMetricsCollector.java    # 指标收集器
├── strategy/                     # 策略模式
│   ├── AkToolsDataStrategy.java        # 策略接口
│   └── AbstractAkToolsDataStrategy.java # 抽象策略基类
└── AkToolsDataService.java      # 服务接口
```

## 使用示例

### 基础数据采集
```java
@Autowired
private AkToolsDataService akToolsDataService;

// 采集股票实时行情
int count = akToolsDataService.collectStockSpotData(LocalDate.now());
```

### 配置文件示例
```yaml
aktools:
  collect:
    default-retry-count: 3
    default-timeout-ms: 30000
    batch-size: 1000
    enable-parallel-processing: true
    parallel-thread-count: 8
    time:
      stock-spot-time: "15:15"
      stock-valuation-time: "18:00"
    retry:
      max-attempts: 3
      initial-delay-ms: 1000
      max-delay-ms: 10000
      multiplier: 2.0
```

## 监控指标

### 核心指标
- `aktools.collect.success`: 采集成功次数
- `aktools.collect.failure`: 采集失败次数
- `aktools.convert.success`: 转换成功次数
- `aktools.convert.failure`: 转换失败次数
- `aktools.collect.duration`: 采集耗时分布
- `aktools.convert.duration`: 转换耗时分布

### 指标查询
```java
@Autowired
private AkToolsMetricsCollector metricsCollector;

// 获取采集成功率
double successRate = metricsCollector.getCollectSuccessRate();

// 获取转换成功率
double convertRate = metricsCollector.getConvertSuccessRate();
```

## 扩展指南

### 添加新的数据接口

1. **创建策略类**
```java
@Component
public class NewDataStrategy extends AbstractAkToolsDataStrategy<NewDataDO> {
    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.NEW_DATA_API;
    }
    
    @Override
    public NewDataDO convertData(Object rawData, ConversionContext context) {
        // 实现数据转换逻辑
    }
}
```

2. **注册策略**
策略会自动通过Spring容器注册到工厂中

3. **实现服务方法**
在服务实现中调用对应的策略

## 最佳实践

### 1. 异常处理
- 使用统一的异常类型 `AkToolsException`
- 区分业务异常和系统异常
- 提供详细的错误码和错误信息

### 2. 性能优化
- 合理设置批量大小和并行线程数
- 使用连接池和缓存减少网络开销
- 监控内存使用，避免OOM

### 3. 运维监控
- 定期检查采集成功率
- 设置告警阈值
- 记录慢查询和异常情况

## 版本说明

- **V1**: 基础版本，简单的服务实现
- **V2-V3**: 生产级版本，包含策略模式、监控、配置管理等高级特性

推荐在生产环境中使用最新版本。