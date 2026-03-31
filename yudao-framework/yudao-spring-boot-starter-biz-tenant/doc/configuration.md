# 配置说明

## 配置属性

```yaml
yudao:
  tenant:
    enable: true                          # 是否开启多租户（默认 true）
    ignore-urls:                          # 忽略租户的 URL 列表
      - /admin-api/system/auth/login
      - /admin-api/system/auth/refresh-token
      - /admin-api/infra/file/get/**
    ignore-visit-urls:                    # 忽略跨租户访问的 URL 列表
      - /admin-api/system/user/info
    ignore-tables:                        # 忽略租户的表列表
      - system_tenant
      - system_tenant_package
      - system_dict_type
      - system_dict_data
      - system_config
      - system_menu
      - system_role
      - system_role_menu
    ignore-caches:                        # 忽略租户的缓存列表
      - system_dict
      - system_config
```

## 配置详解

### enable - 是否开启

```yaml
yudao:
  tenant:
    enable: true  # true: 开启多租户，false: 关闭多租户
```

### ignore-urls - 忽略 URL

```yaml
yudao:
  tenant:
    ignore-urls:
      # 登录相关
      - /admin-api/system/auth/login
      - /admin-api/system/auth/refresh-token
      # 文件上传下载
      - /admin-api/infra/file/get/**
      - /admin-api/infra/file/upload
      # 第三方回调
      - /admin-api/pay/callback/**
      - /admin-api/mp/callback/**
```

### ignore-visit-urls - 忽略跨租户访问

```yaml
yudao:
  tenant:
    ignore-visit-urls:
      # 用户个人信息（跨租户无法获取）
      - /admin-api/system/user/info
      - /admin-api/system/user/update-password
```

### ignore-tables - 忽略表

```yaml
yudao:
  tenant:
    ignore-tables:
      # 租户管理相关
      - system_tenant
      - system_tenant_package
      # 字典相关
      - system_dict_type
      - system_dict_data
      # 系统配置
      - system_config
      # 菜单权限
      - system_menu
      - system_role
      - system_role_menu
```

### ignore-caches - 忽略缓存

```yaml
yudao:
  tenant:
    ignore-caches:
      - system_dict    # 字典缓存
      - system_config  # 配置缓存
```

---

## 使用场景

### 场景1：第三方回调

```yaml
yudao:
  tenant:
    ignore-urls:
      - /admin-api/pay/notify/**       # 支付回调
      - /admin-api/mp/callback/**      # 微信回调
      - /admin-api/sms/callback/**     # 短信回调
```

### 场景2：系统管理

```yaml
yudao:
  tenant:
    ignore-tables:
      - system_tenant                  # 租户表
      - system_tenant_package          # 租户套餐表
      - system_menu                    # 菜单表
      - system_role                    # 角色表
```

### 场景3：公共数据

```yaml
yudao:
  tenant:
    ignore-tables:
      - system_dict_type               # 字典类型
      - system_dict_data               # 字典数据
      - system_config                  # 系统配置
```

---

## 注意事项

### 1. 新增忽略配置

```yaml
# 如果新增了系统表，需要添加到 ignore-tables
yudao:
  tenant:
    ignore-tables:
      - your_new_table
```

### 2. 新增 URL

```yaml
# 如果新增了不需要租户的接口，需要添加到 ignore-urls
yudao:
  tenant:
    ignore-urls:
      - /admin-api/your/api/**
```

### 3. 性能影响

```yaml
# ignore-tables 和 ignore-urls 不宜过多
# 否则会影响 SQL 过滤性能
```
