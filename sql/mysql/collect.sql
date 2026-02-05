/*
 * 数据采集模块数据库表结构
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for collect_task 数据采集任务表
-- ----------------------------
DROP TABLE IF EXISTS `collect_task`;
CREATE TABLE `collect_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编码',
  `type` tinyint NOT NULL COMMENT '采集类型：1-HTTP接口 2-数据库查询 3-文件读取 4-系统指标',
  `source_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据源配置(JSON格式)',
  `cron_expression` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Cron表达式',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-停用 1-启用',
  `last_execute_time` datetime NULL DEFAULT NULL COMMENT '最后执行时间',
  `next_execute_time` datetime NULL DEFAULT NULL COMMENT '下次执行时间',
  `execute_count` int NOT NULL DEFAULT '0' COMMENT '执行次数',
  `success_count` int NOT NULL DEFAULT '0' COMMENT '成功次数',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败次数',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`, `tenant_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据采集任务表';

-- ----------------------------
-- Table structure for collect_data 数据采集记录表
-- ----------------------------
DROP TABLE IF EXISTS `collect_data`;
CREATE TABLE `collect_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `data_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采集的数据内容(JSON格式)',
  `data_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据哈希值(用于去重)',
  `collect_time` datetime NOT NULL COMMENT '采集时间',
  `process_status` tinyint NOT NULL DEFAULT '0' COMMENT '处理状态：0-未处理 1-处理中 2-已处理 3-处理失败',
  `process_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理结果',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE,
  INDEX `idx_collect_time`(`collect_time`) USING BTREE,
  INDEX `idx_process_status`(`process_status`) USING BTREE,
  INDEX `idx_data_hash`(`data_hash`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据采集记录表';

-- ----------------------------
-- Table structure for collect_config 数据采集配置表
-- ----------------------------
DROP TABLE IF EXISTS `collect_config`;
CREATE TABLE `collect_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STRING' COMMENT '配置类型：STRING,JSON,NUMBER,BOOLEAN',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key`, `tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据采集配置表';

-- ----------------------------
-- Table structure for collect_log 数据采集日志表
-- ----------------------------
DROP TABLE IF EXISTS `collect_log`;
CREATE TABLE `collect_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `log_type` tinyint NOT NULL COMMENT '日志类型：1-执行开始 2-执行成功 3-执行失败 4-数据处理',
  `log_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志内容',
  `execute_time` datetime NOT NULL COMMENT '执行时间',
  `duration` int NULL DEFAULT NULL COMMENT '执行耗时(毫秒)',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE,
  INDEX `idx_execute_time`(`execute_time`) USING BTREE,
  INDEX `idx_log_type`(`log_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据采集日志表';

-- ----------------------------
-- Table structure for collect_aktools_task AkTools任务表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_task`;
CREATE TABLE `collect_aktools_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
    `api_enum` VARCHAR(50) NOT NULL COMMENT 'API枚举标识',
    `cron_expression` VARCHAR(50) NOT NULL COMMENT 'Cron表达式',
    `task_param` TEXT COMMENT '任务参数(JSON格式)',
    `status` TINYINT DEFAULT 1 COMMENT '任务状态(1启用,0禁用)',
    `description` VARCHAR(200) COMMENT '任务描述',
    `creator` VARCHAR(50) COMMENT '创建者',
    `updater` VARCHAR(50) COMMENT '更新者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_name` (`task_name`),
    KEY `idx_api_enum` (`api_enum`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AkTools任务表';

-- ----------------------------
-- Records of collect_aktools_task
-- ----------------------------
BEGIN;
INSERT INTO `collect_aktools_task` VALUES 
(1, '股票实时行情采集', 'STOCK_SPOT', '0 15 15 ? * MON-FRI', '{\"tradeDate\":\"yesterday\"}', 1, '每日收盘后采集股票实时行情数据', 'system', 'system', NOW(), NOW()),
(2, '个股估值数据采集', 'STOCK_VALUATION', '0 0 18 ? * MON-FRI', '{\"tradeDate\":\"yesterday\"}', 1, '每日晚间采集个股估值数据', 'system', 'system', NOW(), NOW()),
(3, '财务报表数据采集', 'FINANCIAL_REPORT', '0 0 2 1 * ?', '{\"month\":\"lastMonth\"}', 1, '每月1日采集财务报表数据', 'system', 'system', NOW(), NOW()),
(4, '北向资金数据采集', 'NORTH_FUND', '0 0 17 ? * MON-FRI', '{\"tradeDate\":\"yesterday\"}', 1, '每日采集北向资金流动数据', 'system', 'system', NOW(), NOW()),
(5, '宏观CPI数据采集', 'MACRO_CPI', '0 0 3 15 * ?', '{\"month\":\"lastMonth\"}', 1, '每月15日采集CPI数据', 'system', 'system', NOW(), NOW());
COMMIT;

-- ----------------------------
-- 初始数据插入
-- ----------------------------

-- 插入默认配置项
INSERT INTO `collect_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES 
('collect.default.timeout', '30000', 'NUMBER', '默认超时时间(毫秒)'),
('collect.default.retry.count', '3', 'NUMBER', '默认重试次数'),
('collect.default.batch.size', '100', 'NUMBER', '默认批处理大小'),
('collect.system.metrics.enabled', 'true', 'BOOLEAN', '是否启用系统指标采集');

-- 插入示例采集任务
INSERT INTO `collect_task` (`name`, `code`, `type`, `source_config`, `cron_expression`, `status`, `remark`) VALUES 
('系统CPU使用率采集', 'SYSTEM_CPU_METRICS', 4, '{\"metricsType\":\"cpu\",\"interval\":60}', '0 */5 * * * ?', 1, '每5分钟采集一次系统CPU使用率'),
('系统内存使用情况采集', 'SYSTEM_MEMORY_METRICS', 4, '{\"metricsType\":\"memory\",\"interval\":60}', '0 */5 * * * ?', 1, '每5分钟采集一次系统内存使用情况'),
('示例HTTP接口采集', 'EXAMPLE_HTTP_COLLECT', 1, '{\"url\":\"https://api.example.com/data\",\"method\":\"GET\",\"headers\":{\"Content-Type\":\"application/json\"}}', '0 0 */1 * * ?', 0, '示例HTTP接口数据采集任务');

-- ----------------------------
-- 菜单权限数据插入
-- ----------------------------

-- 插入数据采集顶级菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2400, '数据采集', '', 1, 30, 0, '/collect', 'ep:data-analysis', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集任务菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2401, '采集任务', 'collect:task:query', 2, 1, 2400, 'task', 'ep:collection', 'collect/task/index', 'CollectTask', 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集任务按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2402, '采集任务查询', 'collect:task:query', 3, 1, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2403, '采集任务创建', 'collect:task:create', 3, 2, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2404, '采集任务更新', 'collect:task:update', 3, 3, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2405, '采集任务删除', 'collect:task:delete', 3, 4, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2406, '采集任务执行', 'collect:task:execute', 3, 5, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2407, '采集任务导出', 'collect:task:export', 3, 6, 2401, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集数据菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2410, '采集数据', 'collect:data:query', 2, 2, 2400, 'data', 'ep:data-line', 'collect/data/index', 'CollectData', 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集数据按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2411, '采集数据查询', 'collect:data:query', 3, 1, 2410, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2412, '采集数据删除', 'collect:data:delete', 3, 4, 2410, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2413, '采集数据导出', 'collect:data:export', 3, 5, 2410, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集配置菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2420, '采集配置', 'collect:config:query', 2, 3, 2400, 'config', 'ep:setting', 'collect/config/index', 'CollectConfig', 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集配置按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2421, '采集配置查询', 'collect:config:query', 3, 1, 2420, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2422, '采集配置更新', 'collect:config:update', 3, 3, 2420, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集日志菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2430, '采集日志', 'collect:log:query', 2, 4, 2400, 'log', 'ep:document', 'collect/log/index', 'CollectLog', 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 插入采集日志按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2431, '采集日志查询', 'collect:log:query', 3, 1, 2430, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2432, '采集日志删除', 'collect:log:delete', 3, 4, 2430, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES (2433, '采集日志导出', 'collect:log:export', 3, 5, 2430, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 为超级管理员角色分配菜单权限
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2400, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2401, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2402, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2403, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2404, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2405, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2406, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2407, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2410, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2411, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2412, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2413, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2420, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2421, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2422, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2430, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2431, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2432, 'admin', NOW(), '1', NOW(), b'0', 1);
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) 
VALUES (1, 2433, 'admin', NOW(), '1', NOW(), b'0', 1);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for collect_aktools_stock_spot 股票实时行情表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_stock_spot`;
CREATE TABLE `collect_aktools_stock_spot` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `name` VARCHAR(100) NOT NULL COMMENT '股票名称',
    `current_price` DECIMAL(10,2) COMMENT '最新价',
    `change_percent` DECIMAL(8,2) COMMENT '涨跌幅(%)',
    `change_amount` DECIMAL(10,2) COMMENT '涨跌额',
    `volume` BIGINT COMMENT '成交量(手)',
    `amount` DECIMAL(15,2) COMMENT '成交额(元)',
    `turnover_rate` DECIMAL(8,2) COMMENT '换手率(%)',
    `high_price` DECIMAL(10,2) COMMENT '最高价',
    `low_price` DECIMAL(10,2) COMMENT '最低价',
    `open_price` DECIMAL(10,2) COMMENT '开盘价',
    `pre_close_price` DECIMAL(10,2) COMMENT '昨收价',
    `pe_ratio` DECIMAL(10,2) COMMENT '市盈率',
    `pb_ratio` DECIMAL(10,2) COMMENT '市净率',
    `market_cap` DECIMAL(15,2) COMMENT '总市值(亿元)',
    `circulating_market_cap` DECIMAL(15,2) COMMENT '流通市值(亿元)',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_date` (`symbol`, `trade_date`, `tenant_id`),
    KEY `idx_trade_date` (`trade_date`),
    KEY `idx_current_price` (`current_price`),
    KEY `idx_change_percent` (`change_percent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票实时行情表';

-- ----------------------------
-- Table structure for collect_aktools_stock_valuation 个股估值数据表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_stock_valuation`;
CREATE TABLE `collect_aktools_stock_valuation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `name` VARCHAR(100) NOT NULL COMMENT '股票名称',
    `pe_ttm` DECIMAL(10,2) COMMENT '市盈率(TTM)',
    `pe_lyr` DECIMAL(10,2) COMMENT '市盈率(静)',
    `pb` DECIMAL(10,2) COMMENT '市净率',
    `ps_ttm` DECIMAL(10,2) COMMENT '市销率(TTM)',
    `pcf_ttm` DECIMAL(10,2) COMMENT '市现率(TTM)',
    `dividend_yield` DECIMAL(8,2) COMMENT '股息率(%)',
    `enterprise_value` DECIMAL(15,2) COMMENT '企业价值(亿元)',
    `ev_ebitda` DECIMAL(10,2) COMMENT 'EV/EBITDA',
    `peg` DECIMAL(10,2) COMMENT 'PEG比率',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_date` (`symbol`, `trade_date`, `tenant_id`),
    KEY `idx_trade_date` (`trade_date`),
    KEY `idx_pe_ttm` (`pe_ttm`),
    KEY `idx_pb` (`pb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个股估值数据表';

-- ----------------------------
-- Table structure for collect_aktools_north_fund 北向资金数据表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_north_fund`;
CREATE TABLE `collect_aktools_north_fund` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `sh_amount` DECIMAL(15,2) COMMENT '沪股通当日资金流入(亿元)',
    `sz_amount` DECIMAL(15,2) COMMENT '深股通当日资金流入(亿元)',
    `total_amount` DECIMAL(15,2) COMMENT '北向资金当日合计流入(亿元)',
    `sh_balance` DECIMAL(15,2) COMMENT '沪股通累计余额(亿元)',
    `sz_balance` DECIMAL(15,2) COMMENT '深股通累计余额(亿元)',
    `total_balance` DECIMAL(15,2) COMMENT '北向资金累计余额(亿元)',
    `sh_quota_used` DECIMAL(5,2) COMMENT '沪股通额度使用率(%)',
    `sz_quota_used` DECIMAL(5,2) COMMENT '深股通额度使用率(%)',
    `holding_value` DECIMAL(15,2) COMMENT '北向资金持股市值(亿元)',
    `holding_ratio` DECIMAL(8,2) COMMENT '占A股流通市值比重(%)',
    `top10_increase` TEXT COMMENT '增持前10大个股(JSON格式)',
    `top10_decrease` TEXT COMMENT '减持前10大个股(JSON格式)',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_trade_date` (`trade_date`, `tenant_id`),
    KEY `idx_total_amount` (`total_amount`),
    KEY `idx_holding_value` (`holding_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='北向资金数据表';

-- ----------------------------
-- Table structure for collect_aktools_shareholder_stats 股东统计表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_shareholder_stats`;
CREATE TABLE `collect_aktools_shareholder_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `name` VARCHAR(100) NOT NULL COMMENT '股票名称',
    `shareholder_count` INT COMMENT '股东总数',
    `avg_shares_per_holder` DECIMAL(15,2) COMMENT '户均持股数',
    `top10_holders_ratio` DECIMAL(8,2) COMMENT '前十大股东持股比例(%)',
    `circulating_shares_ratio` DECIMAL(8,2) COMMENT '流通股东占比(%)',
    `institution_holders_ratio` DECIMAL(8,2) COMMENT '机构投资者占比(%)',
    `individual_holders_ratio` DECIMAL(8,2) COMMENT '个人投资者占比(%)',
    `avg_holding_change` DECIMAL(8,2) COMMENT '户均持股变化(%)',
    `concentration_index` DECIMAL(8,2) COMMENT '持股集中度指数',
    `statistics_date` DATE NOT NULL COMMENT '统计截止日期',
    `announcement_date` DATE COMMENT '公告日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_date` (`symbol`, `statistics_date`, `tenant_id`),
    KEY `idx_statistics_date` (`statistics_date`),
    KEY `idx_shareholder_count` (`shareholder_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股东统计表';

-- ----------------------------
-- Table structure for collect_aktools_stock_fund_flow 个股资金流向表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_stock_fund_flow`;
CREATE TABLE `collect_aktools_stock_fund_flow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `name` VARCHAR(100) NOT NULL COMMENT '股票名称',
    `main_net_inflow` DECIMAL(15,2) COMMENT '主力净流入(万元)',
    `main_net_inflow_ratio` DECIMAL(8,2) COMMENT '主力净流入占比(%)',
    `super_large_net_inflow` DECIMAL(15,2) COMMENT '超大单净流入(万元)',
    `large_net_inflow` DECIMAL(15,2) COMMENT '大单净流入(万元)',
    `medium_net_inflow` DECIMAL(15,2) COMMENT '中单净流入(万元)',
    `small_net_inflow` DECIMAL(15,2) COMMENT '小单净流入(万元)',
    `main_buy_amount` DECIMAL(15,2) COMMENT '主力买入金额(万元)',
    `main_sell_amount` DECIMAL(15,2) COMMENT '主力卖出金额(万元)',
    `net_amount_ratio` DECIMAL(8,2) COMMENT '净额占流通市值比(%)',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_date` (`symbol`, `trade_date`, `tenant_id`),
    KEY `idx_trade_date` (`trade_date`),
    KEY `idx_main_net_inflow` (`main_net_inflow`),
    KEY `idx_net_amount_ratio` (`net_amount_ratio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个股资金流向表';

-- ----------------------------
-- Table structure for collect_aktools_index_spot 指数实时行情表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_index_spot`;
CREATE TABLE `collect_aktools_index_spot` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `index_code` VARCHAR(20) NOT NULL COMMENT '指数代码',
    `index_name` VARCHAR(100) NOT NULL COMMENT '指数名称',
    `current_point` DECIMAL(10,2) COMMENT '最新点位',
    `change_point` DECIMAL(10,2) COMMENT '涨跌点',
    `change_percent` DECIMAL(8,2) COMMENT '涨跌幅(%)',
    `volume` BIGINT COMMENT '成交量(手)',
    `amount` DECIMAL(15,2) COMMENT '成交额(亿元)',
    `high_point` DECIMAL(10,2) COMMENT '最高点位',
    `low_point` DECIMAL(10,2) COMMENT '最低点位',
    `open_point` DECIMAL(10,2) COMMENT '开盘点位',
    `pre_close_point` DECIMAL(10,2) COMMENT '昨收点位',
    `pe_ratio` DECIMAL(10,2) COMMENT '市盈率',
    `pb_ratio` DECIMAL(10,2) COMMENT '市净率',
    `dividend_yield` DECIMAL(8,2) COMMENT '股息率(%)',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_index_date` (`index_code`, `trade_date`, `tenant_id`),
    KEY `idx_trade_date` (`trade_date`),
    KEY `idx_change_percent` (`change_percent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指数实时行情表';

-- ----------------------------
-- Table structure for collect_aktools_market_activity 市场活跃度表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_market_activity`;
CREATE TABLE `collect_aktools_market_activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `limit_up_count` INT COMMENT '涨停家数',
    `limit_down_count` INT COMMENT '跌停家数',
    `炸板率` DECIMAL(8,2) COMMENT '炸板率(%)',
    `natural_limit_up_count` INT COMMENT '自然涨停家数',
    `one_word_limit_up_count` INT COMMENT '一字涨停家数',
    `continuous_limit_up_count` INT COMMENT '连板家数',
    `max_continuous_days` INT COMMENT '连板高度',
    `rise_count` INT COMMENT '上涨家数',
    `fall_count` INT COMMENT '下跌家数',
    `flat_count` INT COMMENT '平盘家数',
    `rise_fall_ratio` DECIMAL(8,2) COMMENT '涨跌比',
    `market_heat_index` DECIMAL(8,2) COMMENT '市场热度指数(0-100)',
    `total_trading_stocks` INT COMMENT '总交易股票数',
    `limit_up_ratio` DECIMAL(8,2) COMMENT '涨停占比(%)',
    `limit_down_ratio` DECIMAL(8,2) COMMENT '跌停占比(%)',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_trade_date` (`trade_date`, `tenant_id`),
    KEY `idx_limit_up_count` (`limit_up_count`),
    KEY `idx_market_heat_index` (`market_heat_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场活跃度表';

-- ----------------------------
-- Table structure for collect_aktools_trade_calendar 交易日历表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_trade_calendar`;
CREATE TABLE `collect_aktools_trade_calendar` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `calendar_date` DATE NOT NULL COMMENT '日历日期',
    `is_trading_day` TINYINT NOT NULL COMMENT '是否交易日(1是,0否)',
    `week_day` TINYINT COMMENT '星期几(1-7)',
    `market_type` VARCHAR(20) COMMENT '市场类型(SSE:上交所,SZSE:深交所)',
    `holiday_name` VARCHAR(100) COMMENT '节假日名称',
    `settlement_date` DATE COMMENT '结算日期',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_calendar_date_market` (`calendar_date`, `market_type`, `tenant_id`),
    KEY `idx_is_trading_day` (`is_trading_day`),
    KEY `idx_settlement_date` (`settlement_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易日历表';

-- ----------------------------
-- Table structure for collect_aktools_financial_report 财务报表表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_financial_report`;
CREATE TABLE `collect_aktools_financial_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(20) NOT NULL COMMENT '股票代码',
    `name` VARCHAR(100) NOT NULL COMMENT '股票名称',
    `report_type` TINYINT NOT NULL COMMENT '报告类型(1年报,2中报,3一季报,4三季报)',
    `report_year` INT NOT NULL COMMENT '报告年份',
    `end_date` DATE NOT NULL COMMENT '截止日期',
    `net_profit` DECIMAL(15,2) COMMENT '净利润(元)',
    `operating_revenue` DECIMAL(15,2) COMMENT '营业收入(元)',
    `total_assets` DECIMAL(15,2) COMMENT '总资产(元)',
    `total_liabilities` DECIMAL(15,2) COMMENT '负债合计(元)',
    `shareholders_equity` DECIMAL(15,2) COMMENT '股东权益合计(元)',
    `eps` DECIMAL(10,4) COMMENT '每股收益(元)',
    `bps` DECIMAL(10,4) COMMENT '每股净资产(元)',
    `gross_margin` DECIMAL(8,2) COMMENT '销售毛利率(%)',
    `roe` DECIMAL(8,2) COMMENT '净资产收益率(%)',
    `debt_to_asset_ratio` DECIMAL(8,2) COMMENT '资产负债率(%)',
    `operating_cash_flow` DECIMAL(15,2) COMMENT '经营活动现金流(元)',
    `investment_cash_flow` DECIMAL(15,2) COMMENT '投资活动现金流(元)',
    `financing_cash_flow` DECIMAL(15,2) COMMENT '筹资活动现金流(元)',
    `cash_flow_net` DECIMAL(15,2) COMMENT '现金流量净额(元)',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_type_year_end` (`symbol`, `report_type`, `report_year`, `end_date`, `tenant_id`),
    KEY `idx_report_year` (`report_year`),
    KEY `idx_end_date` (`end_date`),
    KEY `idx_net_profit` (`net_profit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务报表表';

-- ----------------------------
-- Table structure for collect_aktools_macro_cpi 宏观CPI数据表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_macro_cpi`;
CREATE TABLE `collect_aktools_macro_cpi` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `publish_month` VARCHAR(10) NOT NULL COMMENT '发布月份(yyyy-MM)',
    `national_yoy` DECIMAL(8,2) COMMENT '全国同比(%)',
    `national_mom` DECIMAL(8,2) COMMENT '全国环比(%)',
    `urban_yoy` DECIMAL(8,2) COMMENT '城市同比(%)',
    `rural_yoy` DECIMAL(8,2) COMMENT '农村同比(%)',
    `food_yoy` DECIMAL(8,2) COMMENT '食品同比(%)',
    `non_food_yoy` DECIMAL(8,2) COMMENT '非食品同比(%)',
    `consumer_goods_yoy` DECIMAL(8,2) COMMENT '消费品同比(%)',
    `services_yoy` DECIMAL(8,2) COMMENT '服务同比(%)',
    `core_cpi_yoy` DECIMAL(8,2) COMMENT '核心CPI同比(%)',
    `core_cpi_mom` DECIMAL(8,2) COMMENT '核心CPI环比(%)',
    `publish_date` DATE COMMENT '数据发布时间',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_publish_month` (`publish_month`, `tenant_id`),
    KEY `idx_publish_date` (`publish_date`),
    KEY `idx_national_yoy` (`national_yoy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宏观CPI数据表';

-- ----------------------------
-- Table structure for collect_aktools_macro_money_supply 货币供应量表
-- ----------------------------
DROP TABLE IF EXISTS `collect_aktools_macro_money_supply`;
CREATE TABLE `collect_aktools_macro_money_supply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `statistics_date` DATE NOT NULL COMMENT '统计日期',
    `m0` DECIMAL(15,2) COMMENT 'M0货币供应量(亿元)',
    `m0_yoy` DECIMAL(8,2) COMMENT 'M0同比增长(%)',
    `m1` DECIMAL(15,2) COMMENT 'M1货币供应量(亿元)',
    `m1_yoy` DECIMAL(8,2) COMMENT 'M1同比增长(%)',
    `m2` DECIMAL(15,2) COMMENT 'M2货币供应量(亿元)',
    `m2_yoy` DECIMAL(8,2) COMMENT 'M2同比增长(%)',
    `m1_m2_diff` DECIMAL(8,2) COMMENT 'M1-M2剪刀差(%)',
    `quasi_currency` DECIMAL(15,2) COMMENT '准货币(亿元)',
    `quasi_currency_yoy` DECIMAL(8,2) COMMENT '准货币同比增长(%)',
    `publish_date` DATE COMMENT '数据发布时间',
    `data_source` VARCHAR(50) COMMENT '数据来源标识',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_statistics_date` (`statistics_date`, `tenant_id`),
    KEY `idx_publish_date` (`publish_date`),
    KEY `idx_m2_yoy` (`m2_yoy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货币供应量表';