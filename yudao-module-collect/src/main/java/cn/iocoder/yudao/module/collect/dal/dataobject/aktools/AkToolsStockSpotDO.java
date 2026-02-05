package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票实时行情数据实体类
 * 对应接口: stock_zh_a_spot_em
 * 抓取时间: 15:15 (收盘后)
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_stock_spot")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsStockSpotDO extends BaseDO {

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
     * 最新价
     * 单位: 元
     */
    private BigDecimal currentPrice;

    /**
     * 涨跌幅
     * 单位: %
     */
    private BigDecimal changePercent;

    /**
     * 涨跌额
     * 单位: 元
     */
    private BigDecimal changeAmount;

    /**
     * 成交量
     * 单位: 手
     */
    private Long volume;

    /**
     * 成交额
     * 单位: 元
     */
    private BigDecimal amount;

    /**
     * 换手率
     * 单位: %
     */
    private BigDecimal turnoverRate;

    /**
     * 最高价
     * 单位: 元
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     * 单位: 元
     */
    private BigDecimal lowPrice;

    /**
     * 开盘价
     * 单位: 元
     */
    private BigDecimal openPrice;

    /**
     * 昨收价
     * 单位: 元
     */
    private BigDecimal preClosePrice;

    /**
     * 市盈率
     */
    private BigDecimal peRatio;

    /**
     * 市净率
     */
    private BigDecimal pbRatio;

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
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 数据来源标识
     * 用于区分不同数据源
     */
    private String dataSource;

}
