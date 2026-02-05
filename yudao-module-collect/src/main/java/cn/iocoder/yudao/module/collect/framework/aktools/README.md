# AkTools (AkShare HTTP 接口) 调用框架

## 框架概述

这是一个基于大厂生产级标准封装的 AkTools (AkShare HTTP 接口) 调用框架，提供了完整的配置管理、连接池、异常处理和序列化优化功能。

## 核心特性

### 🏗️ 配置管理层
- **AkToolsProperties**: 支持在 `application.yaml` 中配置 baseUrl、apiPrefix、timeout 和 apiKey
- **连接池配置**: MaxTotal=200, DefaultMaxPerRoute=50 的高性能连接池
- **参数验证**: 使用 JSR-303 注解进行配置校验

### 🔌 连接池层
- **基于 HttpClient 的 RestTemplate**: 高性能 HTTP 客户端
- **SSL 支持**: 内置 SSL 配置，支持 HTTPS
- **超时控制**: 连接超时、读取超时、请求超时精细化控制
- **连接回收**: 自动回收过期和空闲连接

### ⚡ 异常处理层
- **AkToolsException**: 继承自 yudao 的 ServiceException
- **统一错误码**: 完整的错误码体系 (2000100001-2000100010)
- **详细异常信息**: 包含 AkTools 错误码、HTTP 状态码等上下文信息

### 🔄 序列化优化层
- **NaN/Infinity 处理**: 金融数据特殊值处理
- **BigDecimal 映射**: NaN 映射为 BigDecimal.ZERO
- **类型安全**: 支持 Double、Float、BigDecimal 等数值类型

## 目录结构

```
framework/aktools/
├── config/                    # 配置类
│   ├── AkToolsProperties.java # 核心配置属性
│   └── AkToolsJacksonConfig.java # Jackson 序列化配置
├── exception/                 # 异常处理
│   ├── AkToolsException.java # 自定义异常类
│   └── AkToolsErrorCodeConstants.java # 错误码常量
├── http/                     # HTTP 客户端
│   ├── AkToolsHttpClient.java # 核心 HTTP 客户端
│   └── AkToolsRestTemplateConfig.java # RestTemplate 配置
├── model/                    # 数据模型
│   └── AkToolsResponse.java  # 通用响应包装类
└── demo/                     # 使用示例
    ├── dto/                  # 数据传输对象
    │   └── StockQuoteDTO.java # 股票行情 DTO
    └── service/              # 服务示例
        └── AkToolsStockService.java # 股票服务示例
```

## 配置说明

### application.yaml 配置示例

```yaml
aktools:
  # AkTools 服务基础 URL
  base-url: http://127.0.0.1:8080
  # API 接口前缀
  api-prefix: /api/public
  # HTTP 请求超时时间（毫秒）
  timeout: 30000
  # API 访问密钥
  api-key: your-api-key-here
  
  # 连接池配置
  pool:
    # 连接池最大连接数
    max-total: 200
    # 每个路由的默认最大连接数
    default-max-per-route: 50
    # 连接超时时间（毫秒）
    connection-timeout: 5000
    # 从连接池获取连接的超时时间（毫秒）
    connection-request-timeout: 2000
```

## 使用示例

### 1. 基础使用

```java
@Service
@RequiredArgsConstructor
public class YourService {
    
    private final AkToolsHttpClient akToolsHttpClient;
    
    public void callAkToolsApi() {
        // GET 请求示例
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", "000001.SZ");
        
        List<StockQuoteDTO> result = akToolsHttpClient.getForObject(
            "/stock/quote", params, List.class);
    }
}
```

### 2. 股票服务完整示例

参考 `AkToolsStockService` 类，提供了：
- 单只股票行情获取
- 多只股票行情批量获取
- 股票历史数据获取

### 3. 异常处理

```java
try {
    StockQuoteDTO quote = stockService.getStockQuote("000001.SZ");
} catch (AkToolsException e) {
    // 处理 AkTools 业务异常
    log.error("AkTools 调用失败: {}", e.getAktoolsMessage());
} catch (Exception e) {
    // 处理其他异常
    log.error("系统异常: {}", e.getMessage());
}
```

## 错误码说明

| 错误码 | 描述 | HTTP状态码 |
|--------|------|------------|
| 2000100001 | AkTools 接口调用失败 | 500 |
| 2000100002 | AkTools 接口业务错误 | 业务相关 |
| 2000100003 | AkTools 接口认证失败 | 401 |
| 2000100004 | AkTools 接口请求参数错误 | 400 |
| 2000100005 | AkTools 接口调用超时 | 408 |
| 2000100006 | AkTools 网络连接异常 | 503 |
| 2000100007 | AkTools 响应数据解析失败 | 500 |
| 2000100008 | AkTools 接口返回空数据 | 204 |
| 2000100009 | AkTools 接口调用频率超限 | 429 |
| 2000100010 | AkTools 服务暂时不可用 | 503 |

## 性能优化

### 连接池优势
- **高并发支持**: MaxTotal=200 支持高并发请求
- **资源复用**: 连接复用减少 TCP 连接开销
- **智能回收**: 自动回收过期连接，防止连接泄漏

### 序列化优化
- **特殊值处理**: 优雅处理 NaN、Infinity 等金融数据特殊情况
- **类型安全**: 避免数值类型转换异常
- **性能友好**: 减少反序列化失败导致的重试

## 生产环境建议

1. **安全配置**:
   - 生产环境应配置有效的 SSL 证书
   - apiKey 应通过配置中心或环境变量注入
   - 启用请求签名验证

2. **监控告警**:
   - 监控连接池使用情况
   - 统计接口调用成功率和响应时间
   - 设置超时和失败率告警

3. **容错机制**:
   - 实现熔断降级策略
   - 配置合理的重试机制
   - 准备本地缓存作为兜底方案

## 扩展开发

### 添加新的接口调用
1. 在 `AkToolsHttpClient` 中添加对应的方法
2. 创建相应的 DTO 类
3. 在服务类中封装业务逻辑

### 自定义序列化规则
1. 继承 `JsonDeserializer` 实现自定义反序列化器
2. 在 `AkToolsJacksonConfig` 中注册新的序列化器
3. 测试特殊场景下的数据处理

这个框架遵循了代码整洁之道，使用 Lombok 简化代码，包含完整的注释和指标说明，是一个生产级的高质量框架实现。