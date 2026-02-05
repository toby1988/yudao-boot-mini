# 数据采集模块 (yudao-module-collect)

## 模块介绍

数据采集模块是基于芋道快速开发平台构建的专门用于各种数据采集功能的业务模块。

### 主要功能特性

1. **多类型数据采集支持**
   - HTTP接口采集
   - 数据库查询采集
   - 文件读取采集
   - 系统指标采集

2. **灵活的任务管理**
   - 支持Cron表达式定时执行
   - 任务状态管理（启用/停用）
   - 执行统计和监控

3. **完整的数据处理流程**
   - 数据去重机制
   - 采集数据存储
   - 处理状态跟踪
   - 详细日志记录

4. **企业级特性**
   - 租户隔离支持
   - 权限控制
   - 操作日志记录
   - 数据导出功能

## 目录结构

```
yudao-module-collect/
├── src/main/java/cn/iocoder/yudao/module/collect/
│   ├── api/                    # 对外API接口
│   ├── controller/             # 控制器层
│   │   └── admin/collect/      # 管理后台接口
│   │       └── vo/             # 请求响应VO
│   │           ├── task/       # 任务相关VO
│   │           ├── data/       # 数据相关VO
│   │           ├── config/     # 配置相关VO
│   │           └── log/        # 日志相关VO
│   ├── convert/                # 对象转换层
│   │   └── collect/            # 数据采集转换器
│   ├── dal/                    # 数据访问层
│   │   ├── dataobject/         # 数据实体类
│   │   ├── mysql/collect/      # MySQL Mapper接口
│   │   └── redis/              # Redis访问类
│   ├── enums/                  # 枚举类
│   ├── job/                    # 定时任务
│   ├── mq/                     # 消息队列
│   ├── service/                # 业务服务层
│   │   └── collect/            # 数据采集服务
│   │       └── impl/           # 服务实现类
│   └── util/                   # 工具类
└── src/main/resources/
    └── mapper/                 # MyBatis XML映射文件
```

## 核心数据库表

### 1. collect_task (数据采集任务表)
存储数据采集任务的基本信息和配置

### 2. collect_data (数据采集记录表)
存储实际采集到的数据内容

### 3. collect_config (数据采集配置表)
存储系统级别的采集配置参数

### 4. collect_log (数据采集日志表)
记录采集任务的执行日志和状态

## 快速开始

### 1. 数据库初始化
执行SQL脚本创建相关表：
```bash
# 执行 sql/mysql/collect.sql
```

### 2. 模块集成
在主项目 `yudao-server/pom.xml` 中添加依赖：
```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-module-collect</artifactId>
    <version>${revision}</version>
</dependency>
```

### 3. 功能使用

#### 创建采集任务
通过管理后台或API创建新的采集任务：
- 设置任务名称和唯一编码
- 选择采集类型（HTTP、数据库、文件、系统指标）
- 配置数据源参数（JSON格式）
- 设置执行频率（Cron表达式）

#### 执行采集任务
- 自动执行：通过Quartz定时任务按Cron表达式执行
- 手动执行：通过管理后台立即执行指定任务

#### 查看采集结果
- 管理后台查看任务执行状态和统计数据
- 查询采集到的具体数据内容
- 查看详细的执行日志

## API接口说明

### 任务管理接口
- `POST /collect/task/create` - 创建采集任务
- `PUT /collect/task/update` - 更新采集任务
- `DELETE /collect/task/delete` - 删除采集任务
- `GET /collect/task/get` - 获取任务详情
- `GET /collect/task/page` - 分页查询任务列表
- `PUT /collect/task/execute` - 立即执行任务

### 数据查询接口
- `GET /collect/data/page` - 分页查询采集数据
- `GET /collect/data/get` - 获取数据详情

### 配置管理接口
- `GET /collect/config/get-value` - 获取配置值
- `POST /collect/config/update` - 更新配置

## 扩展开发

### 添加新的采集类型
1. 在 `CollectTypeEnum` 中添加新的枚举值
2. 在 `CollectTaskServiceImpl.executeCollectTask()` 方法中添加对应的处理逻辑
3. 实现具体的采集逻辑方法

### 自定义数据处理
1. 修改 `CollectDataDO` 实体类添加需要的字段
2. 在采集完成后进行自定义的数据处理
3. 更新数据处理状态和结果

## 注意事项

1. **安全性**：确保数据源配置的安全性，避免敏感信息泄露
2. **性能**：合理设置采集频率，避免对源系统造成过大压力
3. **异常处理**：完善的异常处理机制，确保任务失败时能够正确记录和恢复
4. **数据量控制**：对于大量数据采集，建议采用分批处理的方式
5. **租户隔离**：利用系统的多租户特性，确保不同租户数据隔离

## 后续优化方向

1. **采集性能优化**：支持并发采集、批量处理
2. **数据质量监控**：增加数据质量检查和告警机制
3. **可视化展示**：提供采集数据的图表化展示
4. **智能调度**：基于历史执行情况优化任务调度策略
5. **插件化架构**：支持通过插件方式扩展采集类型