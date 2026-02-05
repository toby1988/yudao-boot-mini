package cn.iocoder.yudao.module.collect.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 数据采集日志 DO
 *
 * @author 芋道源码
 */
@TableName("collect_log")
@KeySequence("collect_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectLogDO extends BaseDO {

    /**
     * 日志ID
     */
    @TableId
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 日志类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.collect.enums.LogTypeEnum}
     */
    private Integer logType;

    /**
     * 日志内容
     */
    private String logContent;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 执行耗时(毫秒)
     */
    private Integer duration;

}