package cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AkTools任务创建 Request VO")
@Data
public class AkToolsTaskCreateReqVO {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "股票实时行情采集")
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @Schema(description = "API枚举标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "STOCK_SPOT")
    @NotBlank(message = "API枚举标识不能为空")
    private String apiEnum;

    @Schema(description = "Cron表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 15 15 ? * MON-FRI")
    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;

    @Schema(description = "任务参数(JSON格式)", example = "{\"tradeDate\":\"yesterday\"}")
    private String taskParam;

    @Schema(description = "任务状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "任务状态不能为空")
    private Integer status;

    @Schema(description = "任务描述", example = "每日收盘后采集股票实时行情数据")
    private String description;

}