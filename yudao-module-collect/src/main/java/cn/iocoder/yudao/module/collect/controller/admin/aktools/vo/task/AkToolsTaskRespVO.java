package cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AkTools任务 Response VO")
@Data
public class AkToolsTaskRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
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

    @Schema(description = "创建者", example = "system")
    private String creator;

    @Schema(description = "更新者", example = "system")
    private String updater;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}