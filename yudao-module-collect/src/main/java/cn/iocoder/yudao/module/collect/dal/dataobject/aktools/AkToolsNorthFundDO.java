package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 北向资金数据实体类
 * 对应接口: stock_hsgt_north_acc_flow_em
 * 抓取时间: 17:00
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_north_fund")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsNorthFundDO extends BaseDO {

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
     * 当日净买入额
     * 单位: 亿元
     */
    private BigDecimal dailyNetBuy;

    /**
     * 累计净买入额
     * 单位: 亿元
     */
    private BigDecimal cumulativeNetBuy;

    /**
     * 持仓市值
     * 单位: 亿元
     */
    private BigDecimal holdingMarketValue;

    /**
     * 持仓占流通市值比
     * 单位: %
     */
    private BigDecimal holdingRatio;

    /**
     * 沪股通当日净买入
     * 单位: 亿元
     */
    private BigDecimal shNetBuy;

    /**
     * 深股通当日净买入
     * 单位: 亿元
     */
    private BigDecimal szNetBuy;

    /**
     * 数据来源标识
     */
    private String dataSource;

}
