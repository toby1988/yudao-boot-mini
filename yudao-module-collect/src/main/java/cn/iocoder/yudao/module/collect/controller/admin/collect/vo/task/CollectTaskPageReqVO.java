package cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据采集任务分页 Request VO")
@Data
public class CollectTaskPageReqVO {

    @Schema(description = "任务名称", example = "系统监控采集")
    private String name;

    @Schema(description = "任务编码", example = "SYSTEM_MONITOR")
    private String code;

    @Schema(description = "采集类型", example = "4")
    private Integer type;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}