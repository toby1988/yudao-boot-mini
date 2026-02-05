package cn.iocoder.yudao.module.collect.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 数据采集任务 DO
 *
 * @author 芋道源码
 */
@TableName("collect_task")
@KeySequence("collect_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectTaskDO extends BaseDO {

    /**
     * 任务ID
     */
    @TableId
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务编码
     */
    private String code;

    /**
     * 采集类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.collect.enums.CollectTypeEnum}
     */
    private Integer type;

    /**
     * 数据源配置(JSON格式)
     */
    private String sourceConfig;

    /**
     * Cron表达式
     */
    private String cronExpression;

    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;

    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecuteTime;

    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 执行次数
     */
    private Integer executeCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failCount;

    /**
     * 备注
     */
    private String remark;

}