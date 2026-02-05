package cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 数据采集任务 Response VO")
@Data
public class CollectTaskRespVO {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统监控采集")
    private String name;

    @Schema(description = "任务编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM_MONITOR")
    private String code;

    @Schema(description = "采集类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer type;

    @Schema(description = "数据源配置(JSON格式)")
    private String sourceConfig;

    @Schema(description = "Cron表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 */5 * * * ?")
    private String cronExpression;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "最后执行时间")
    private LocalDateTime lastExecuteTime;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecuteTime;

    @Schema(description = "执行次数")
    private Integer executeCount;

    @Schema(description = "成功次数")
    private Integer successCount;

    @Schema(description = "失败次数")
    private Integer failCount;

    @Schema(description = "备注", example = "用于采集系统监控指标")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}