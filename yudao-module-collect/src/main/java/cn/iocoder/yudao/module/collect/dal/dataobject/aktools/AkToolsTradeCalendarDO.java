package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 交易日历数据实体类
 * 对应接口: tool_trade_date_hist_sina
 * 获取历史交易日数据，用于过滤非交易日任务
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_trade_calendar")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsTradeCalendarDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 是否为交易日
     * 1: 是交易日, 0: 非交易日(周末/节假日)
     */
    private Integer isTradingDay;

    /**
     * 星期几
     * 1: 周一, 2: 周二, ..., 7: 周日
     */
    private Integer weekDay;

    /**
     * 是否为月末交易日
     * 1: 是, 0: 否
     */
    private Integer isMonthEnd;

    /**
     * 是否为季末交易日
     * 1: 是, 0: 否
     */
    private Integer isQuarterEnd;

    /**
     * 是否为年末交易日
     * 1: 是, 0: 否
     */
    private Integer isYearEnd;

    /**
     * 节假日名称 (如果是节假日)
     */
    private String holidayName;

    /**
     * 数据来源标识
     */
    private String dataSource;

}