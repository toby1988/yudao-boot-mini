# JSON 工具类使用指南

## 概述

`JsonUtils` 基于 Jackson 实现 JSON 的序列化和反序列化。

## API 方法

### 序列化

```java
// 对象转 JSON 字符串
String json = JsonUtils.toJsonString(user);

// 对象转 JSON 字节
byte[] bytes = JsonUtils.toJsonByte(user);

// 对象转格式化 JSON
String prettyJson = JsonUtils.toJsonPrettyString(user);
```

### 反序列化

```java
// JSON 字符串转对象
UserVO user = JsonUtils.parseObject(json, UserVO.class);

// JSON 字节转对象
UserVO user = JsonUtils.parseObject(bytes, UserVO.class);

// JSON 转泛型对象
List<UserVO> users = JsonUtils.parseObject(json, new TypeReference<List<UserVO>>() {});

// JSON 转数组
List<UserVO> users = JsonUtils.parseArray(json, UserVO.class);

// 解析 JSON 路径
String name = JsonUtils.parseObject(json, "user.name", String.class);
```

### JSON 树操作

```java
// 解析为 JsonNode
JsonNode node = JsonUtils.parseTree(json);

// 获取子节点
JsonNode userNode = node.get("user");
String name = userNode.get("name").asText();
```

### 类型转换

```java
// Object 转目标类型（避免序列化再反序列化的性能损耗）
UserVO user = JsonUtils.convertObject(map, UserVO.class);

// Object 转泛型类型
List<UserVO> users = JsonUtils.convertObject(obj, new TypeReference<List<UserVO>>() {});

// Object 转 List
List<UserVO> users = JsonUtils.convertList(obj, UserVO.class);
```

### JSON 判断

```java
// 判断是否为 JSON
boolean isJson = JsonUtils.isJson(str);

// 判断是否为 JSON 对象
boolean isJsonObject = JsonUtils.isJsonObject(str);
```

## 使用示例

### 场景1：Redis 序列化

```java
// 存储
redisTemplate.opsForValue().set("user:" + id, JsonUtils.toJsonString(user));

// 读取
String json = redisTemplate.opsForValue().get("user:" + id);
UserVO user = JsonUtils.parseObject(json, UserVO.class);
```

### 场景2：MQ 消息序列化

```java
// 发送消息
String message = JsonUtils.toJsonString(orderMessage);
rabbitTemplate.convertAndSend("order.exchange", "order.key", message);

// 消费消息
@RabbitListener(queues = "order.queue")
public void handleMessage(String message) {
    OrderMessage order = JsonUtils.parseObject(message, OrderMessage.class);
    // 处理消息
}
```

### 场景3：HTTP 请求

```java
// 发送 POST 请求
UserCreateDTO dto = new UserCreateDTO();
dto.setUsername("admin");
String body = JsonUtils.toJsonString(dto);
HttpUtils.post(url, body);

// 解析响应
String response = HttpUtils.get(url);
UserVO user = JsonUtils.parseObject(response, UserVO.class);
```

### 场景4：Map 转对象

```java
// Map 转 POJO（避免序列化性能损耗）
Map<String, Object> map = new HashMap<>();
map.put("id", 1);
map.put("name", "admin");

UserVO user = JsonUtils.convertObject(map, UserVO.class);
```

## 配置说明

### 默认配置

```java
// 工具类已配置：
// 1. 忽略空值
// 2. 忽略未知属性
// 3. 支持 LocalDateTime
// 4. 支持 Timestamp 格式
```

### 自定义配置

```java
// 使用 Spring 的 ObjectMapper
@JsonUtils.init(objectMapper);
```

## 常见问题

### Q: LocalDateTime 序列化为时间戳？

A: 工具类默认配置了 LocalDateTime 序列化为时间戳。如需格式化输出，使用 `@JsonFormat` 注解：

```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createTime;
```

### Q: 忽略某些字段？

A: 使用 Jackson 注解：

```java
@JsonIgnore
private String password;

@JsonInclude(JsonInclude.Include.NON_NULL)
private String email;
```
