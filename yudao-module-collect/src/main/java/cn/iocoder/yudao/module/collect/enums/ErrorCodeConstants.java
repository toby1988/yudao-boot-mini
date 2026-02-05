package cn.iocoder.yudao.module.collect.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 数据采集模块错误码
 *
 * @author ToBy.Qoder
 */
public interface ErrorCodeConstants {

    // ========== 数据采集任务相关错误码 ==========
    ErrorCode TASK_NOT_EXISTS = new ErrorCode(2000000001, "数据采集任务不存在");
    ErrorCode TASK_CODE_DUPLICATE = new ErrorCode(2000000002, "任务编码已存在");
    ErrorCode TASK_TYPE_INVALID = new ErrorCode(2000000003, "无效的采集类型");
    ErrorCode TASK_CRON_INVALID = new ErrorCode(2000000004, "无效的Cron表达式");
    ErrorCode TASK_NOT_ENABLED = new ErrorCode(2000000005, "任务未启用");

    // ========== AkTools任务配置相关错误码 ==========
    ErrorCode JOB_CONFIG_NOT_EXISTS = new ErrorCode(2000001001, "任务配置不存在");
    ErrorCode JOB_CONFIG_NAME_DUPLICATE = new ErrorCode(2000001002, "任务名称已存在");
    ErrorCode JOB_CONFIG_API_ENUM_NOT_FOUND = new ErrorCode(2000001003, "API枚举不存在");
    ErrorCode JOB_CONFIG_CRON_INVALID = new ErrorCode(2000001004, "无效的Cron表达式");
    ErrorCode JOB_CONFIG_DISABLED = new ErrorCode(2000001005, "任务配置已禁用");
    ErrorCode JOB_CONFIG_EXECUTE_FAILED = new ErrorCode(2000001006, "任务执行失败: {}");

}
