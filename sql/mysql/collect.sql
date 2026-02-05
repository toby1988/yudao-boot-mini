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

SET FOREIGN_KEY_CHECKS = 1;