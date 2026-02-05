package cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AkTools任务更新 Request VO")
@Data
public class AkToolsTaskUpdateReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "股票实时行情采集")
    private String taskName;

    @Schema(description = "API枚举标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "STOCK_SPOT")
    private String apiEnum;

    @Schema(description = "Cron表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 15 15 ? * MON-FRI")
    private String cronExpression;

    @Schema(description = "任务参数(JSON格式)", example = "{\"tradeDate\":\"yesterday\"}")
    private String taskParam;

    @Schema(description = "任务状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "任务描述", example = "每日收盘后采集股票实时行情数据")
    private String description;

}