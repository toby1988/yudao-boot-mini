package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AkTools任务实体类
 * 用于存储可配置的任务调度信息
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_task")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsTaskDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务名称
     * 例如: 股票实时行情采集
     */
    private String taskName;

    /**
     * API枚举标识
     * 对应AkApiEnum中的枚举值
     * 例如: STOCK_SPOT, FINANCIAL_REPORT
     */
    private String apiEnum;

    /**
     * Cron表达式
     * 用于定义任务执行时间
     * 例如: 0 15 15 ? * MON-FRI (每周一至周五 15:15执行)
     */
    private String cronExpression;

    /**
     * 任务参数
     * JSON格式字符串，包含任务执行所需的具体参数
     * 例如: {"tradeDate":"yesterday", "symbols":["000001","000002"]}
     */
    private String taskParam;

    /**
     * 任务状态
     * 1: 启用, 0: 禁用
     */
    private Integer status;

    /**
     * 任务描述
     * 对任务功能的简要说明
     */
    private String description;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

}