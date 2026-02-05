package cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 腾落指数/市场活跃度数据实体类
 * 对应接口: stock_zh_a_tr_matrix_report
 * 获取市场的涨停、跌停、炸板等活跃度指标
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_market_activity")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsMarketActivityDO extends BaseDO {

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
     * 涨停家数
     * 当日触及涨停板的股票数量
     */
    private Integer limitUpCount;

    /**
     * 跌停家数
     * 当日触及跌停板的股票数量
     */
    private Integer limitDownCount;

    /**
     * 炸板率
     * 单位: %
     * 曾经涨停但最终未封板的股票比例
     */
    private BigDecimal炸板率;

    /**
     * 自然涨停家数
     * 非一字涨停的涨停股票数量
     */
    private Integer naturalLimitUpCount;

    /**
     * 一字涨停家数
     * 开盘即涨停且全天未开板的股票数量
     */
    private Integer oneWordLimitUpCount;

    /**
     * 连板家数
     * 连续涨停的股票数量
     */
    private Integer continuousLimitUpCount;

    /**
     * 连板高度
     * 最大连板天数
     */
    private Integer maxContinuousDays;

    /**
     * 上涨家数
     * 当日收盘上涨的股票数量
     */
    private Integer riseCount;

    /**
     * 下跌家数
     * 当日收盘下跌的股票数量
     */
    private Integer fallCount;

    /**
     * 平盘家数
     * 当日收盘平盘的股票数量
     */
    private Integer flatCount;

    /**
     * 涨跌比
     * 上涨家数与下跌家数的比例
     */
    private BigDecimal riseFallRatio;

    /**
     * 市场热度指数
     * 综合活跃度评分 (0-100)
     */
    private BigDecimal marketHeatIndex;

    /**
     * 数据来源标识
     */
    private String dataSource;

}