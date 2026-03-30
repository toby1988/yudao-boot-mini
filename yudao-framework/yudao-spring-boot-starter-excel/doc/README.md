# Excel 工具模块文档

## 目录结构

```
doc/
├── README.md                  # 本文档
├── quick-start.md             # 快速开始
├── excel-utils.md             # ExcelUtils 使用指南
├── converters.md              # 转换器使用指南
├── annotations.md             # 注解使用指南
└── dict-integration.md        # 数据字典集成
```

## 模块概述

**yudao-spring-boot-starter-excel** 是 Yudao 框架的 Excel 扩展模块，基于 [FastExcel](https://github.com/zhishan-labs/fastexcel) 实现，提供：

1. **Excel 导出**：将数据列表导出为 Excel 文件
2. **Excel 导入**：读取 Excel 文件为数据列表
3. **数据转换**：支持字典、金额、地区等数据的自动转换
4. **下拉框**：支持 Excel 列的下拉选择

### 核心特性

| 特性 | 说明 |
|-----|------|
| 简洁 API | 一行代码完成导出/导入 |
| 字典转换 | 自动将字典值转换为标签 |
| 金额转换 | 分转元，自动格式化 |
| 地区转换 | 地区名称自动解析为 ID |
| 下拉选择 | 支持字典和自定义数据源 |

### 核心类

| 类名 | 说明 |
|-----|------|
| `ExcelUtils` | Excel 工具类 |
| `DictConvert` | 数据字典转换器 |
| `MoneyConvert` | 金额转换器 |
| `AreaConvert` | 区域转换器 |
| `@DictFormat` | 字典格式化注解 |
| `@ExcelColumnSelect` | Excel 列下拉注解 |

## 快速链接

| 文档 | 说明 |
|-----|------|
| [快速开始](quick-start.md) | 5分钟上手 Excel 操作 |
| [ExcelUtils](excel-utils.md) | Excel 工具类详解 |
| [转换器](converters.md) | 数据转换器使用指南 |
| [注解](annotations.md) | 注解使用指南 |
| [字典集成](dict-integration.md) | 与数据字典的集成 |
