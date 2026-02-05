package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票资金流向数据实体类
 * 对应接口: stock_individual_fund_flow
 * 抓取时间: 16:30
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_stock_fund_flow")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsStockFundFlowDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 股票代码
     * 例如: 000001
     */
    private String symbol;

    /**
     * 股票名称
     * 例如: 平安银行
     */
    private String name;

    /**
     * 主力净流入额
     * 单位: 元
     */
    private BigDecimal mainNetInflow;

    /**
     * 超大单净流入额
     * 单位: 元
     */
    private BigDecimal superLargeNetInflow;

    /**
     * 大单净流入额
     * 单位: 元
     */
    private BigDecimal largeNetInflow;

    /**
     * 中单净流入额
     * 单位: 元
     */
    private BigDecimal mediumNetInflow;

    /**
     * 小单净流入额
     * 单位: 元
     */
    private BigDecimal smallNetInflow;

    /**
     * 主力净流入占比
     * 单位: %
     */
    private BigDecimal mainNetInflowRatio;

    /**
     * 超大单净流入占比
     * 单位: %
     */
    private BigDecimal superLargeNetInflowRatio;

    /**
     * 大单净流入占比
     * 单位: %
     */
    private BigDecimal largeNetInflowRatio;

    /**
     * 中单净流入占比
     * 单位: %
     */
    private BigDecimal mediumNetInflowRatio;

    /**
     * 小单净流入占比
     * 单位: %
     */
    private BigDecimal smallNetInflowRatio;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 数据来源标识
     */
    private String dataSource;

}
