package cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.FinancialReportTypeEnum;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票财务报表数据实体类
 * 对应接口: stock_financial_report_sina
 * 获取上市公司的三大财务报表数据
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_financial_report")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsFinancialReportDO extends BaseDO {

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
     * 报告类型
     * @see FinancialReportTypeEnum
     */
    private FinancialReportTypeEnum reportType;

    /**
     * 报告年份
     */
    private Integer reportYear;

    /**
     * 截止日期
     */
    private LocalDate endDate;

    /**
     * 净利润
     * 单位: 元
     */
    private BigDecimal netProfit;

    /**
     * 营业收入
     * 单位: 元
     */
    private BigDecimal operatingRevenue;

    /**
     * 总资产
     * 单位: 元
     */
    private BigDecimal totalAssets;

    /**
     * 负债合计
     * 单位: 元
     */
    private BigDecimal totalLiabilities;

    /**
     * 股东权益合计
     * 单位: 元
     */
    private BigDecimal shareholdersEquity;

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
     * 销售毛利率
     * 单位: %
     */
    private BigDecimal grossMargin;

    /**
     * 净资产收益率
     * 单位: %
     */
    private BigDecimal roe;

    /**
     * 资产负债率
     * 单位: %
     */
    private BigDecimal debtToAssetRatio;

    /**
     * 数据来源标识
     */
    private String dataSource;

}