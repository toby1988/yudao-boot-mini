package cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据采集任务更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CollectTaskUpdateReqVO extends CollectTaskBaseVO {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "任务ID不能为空")
    private Long id;

}
