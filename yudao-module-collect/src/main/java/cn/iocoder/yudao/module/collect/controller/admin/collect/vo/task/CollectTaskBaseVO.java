package cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据采集任务 Base VO")
@Data
public class CollectTaskBaseVO {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统监控采集")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "任务编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM_MONITOR")
    @NotEmpty(message = "任务编码不能为空")
    private String code;

    @Schema(description = "采集类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    @NotNull(message = "采集类型不能为空")
    private Integer type;

    @Schema(description = "数据源配置(JSON格式)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "数据源配置不能为空")
    private String sourceConfig;

    @Schema(description = "Cron表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 */5 * * * ?")
    @NotEmpty(message = "Cron表达式不能为空")
    private String cronExpression;

    @Schema(description = "备注", example = "用于采集系统监控指标")
    private String remark;

}