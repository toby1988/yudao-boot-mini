package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票估值数据实体类
 * 对应接口: stock_a_lg_indicator
 * 抓取时间: 18:00 (处理后)
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_stock_valuation")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsStockValuationDO extends BaseDO {

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
     * PE-TTM (滚动市盈率)
     */
    private BigDecimal peTtm;

    /**
     * PB (市净率)
     */
    private BigDecimal pb;

    /**
     * PS (市销率)
     */
    private BigDecimal ps;

    /**
     * 股息率
     * 单位: %
     */
    private BigDecimal dividendYield;

    /**
     * 总市值
     * 单位: 亿元
     */
    private BigDecimal marketCap;

    /**
     * 流通市值
     * 单位: 亿元
     */
    private BigDecimal circulatingMarketCap;

    /**
     * 每股收益
     * 单位: 元
     */
    private BigDecimal eps;

    /**
     * 每股净资产
     * 单位: 元
     */
    private BigDecimal bps;

    /**
     * ROE (净资产收益率)
     * 单位: %
     */
    private BigDecimal roe;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 数据来源标识
     */
    private String dataSource;

}
