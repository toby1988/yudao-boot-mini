package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.FinancialReportTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 财务报表数据转换器
 * 负责将原始财务数据转换为FinancialReportDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsFinancialReportDataConverter {

    /**
     * 转换单个财务报表数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期（用于数据标记）
     * @return 财务报表DO对象
     */
    public AkToolsFinancialReportDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始财务数据为空]");
            return null;
        }

        try {
            // 解析股票基本信息
            String symbol = getStringValue(rawData, "symbol");
            String name = getStringValue(rawData, "name");
            
            if (symbol == null || name == null) {
                log.warn("[convert][缺少必要字段] symbol: {}, name: {}", symbol, name);
                return null;
            }

            // 解析报告信息
            Integer reportYear = getIntValue(rawData, "report_year");
            String reportTypeName = getStringValue(rawData, "report_type");
            FinancialReportTypeEnum reportType = parseReportType(reportTypeName);
            
            // 解析财务数据
            BigDecimal netProfit = getBigDecimalValue(rawData, "net_profit");           // 净利润
            BigDecimal operatingRevenue = getBigDecimalValue(rawData, "operating_revenue"); // 营业收入
            BigDecimal totalAssets = getBigDecimalValue(rawData, "total_assets");       // 总资产
            BigDecimal totalLiabilities = getBigDecimalValue(rawData, "total_liabilities"); // 总负债
            BigDecimal shareholdersEquity = getBigDecimalValue(rawData, "shareholders_equity"); // 股东权益
            
            // 计算衍生指标
            BigDecimal eps = calculateEPS(netProfit, shareholdersEquity);              // 每股收益
            BigDecimal bps = calculateBPS(shareholdersEquity, totalAssets);            // 每股净资产
            BigDecimal grossMargin = calculateGrossMargin(operatingRevenue, netProfit); // 销售毛利率
            BigDecimal roe = calculateROE(netProfit, shareholdersEquity);              // 净资产收益率
            BigDecimal debtToAssetRatio = calculateDebtToAssetRatio(totalLiabilities, totalAssets); // 资产负债率

            return AkToolsFinancialReportDO.builder()
                .symbol(symbol)
                .name(name)
                .reportType(reportType)
                .reportYear(reportYear)
                .endDate(tradeDate)
                .netProfit(netProfit)
                .operatingRevenue(operatingRevenue)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .shareholdersEquity(shareholdersEquity)
                .eps(eps)
                .bps(bps)
                .grossMargin(grossMargin)
                .roe(roe)
                .debtToAssetRatio(debtToAssetRatio)
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][财务数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析报告类型
     */
    private FinancialReportTypeEnum parseReportType(String reportTypeName) {
        if (reportTypeName == null) return FinancialReportTypeEnum.ANNUAL;
        
        switch (reportTypeName.toLowerCase()) {
            case "annual":
            case "年报":
            case "年度报告":
                return FinancialReportTypeEnum.ANNUAL;
            case "semi_annual":
            case "半年报":
                return FinancialReportTypeEnum.SEMI_ANNUAL;
            case "quarterly":
            case "季报":
            case "第一季度":
            case "第二季度":
            case "第三季度":
            case "第四季度":
                return FinancialReportTypeEnum.QUARTERLY;
            case "monthly":
            case "月报":
                return FinancialReportTypeEnum.MONTHLY;
            default:
                log.warn("[parseReportType][未知报告类型] type: {}", reportTypeName);
                return FinancialReportTypeEnum.ANNUAL;
        }
    }

    /**
     * 计算每股收益 (EPS)
     */
    private BigDecimal calculateEPS(BigDecimal netProfit, BigDecimal shareholdersEquity) {
        if (netProfit == null || shareholdersEquity == null || shareholdersEquity.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        // 简化的EPS计算，实际应该基于股本计算
        return netProfit.divide(shareholdersEquity, 6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算每股净资产 (BPS)
     */
    private BigDecimal calculateBPS(BigDecimal shareholdersEquity, BigDecimal totalAssets) {
        if (shareholdersEquity == null || totalAssets == null || totalAssets.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        // 简化的BPS计算
        return shareholdersEquity.divide(totalAssets, 6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算销售毛利率
     */
    private BigDecimal calculateGrossMargin(BigDecimal operatingRevenue, BigDecimal netProfit) {
        if (operatingRevenue == null || operatingRevenue.compareTo(BigDecimal.ZERO) <= 0 || netProfit == null) {
            return null;
        }
        // 简化的毛利率计算（实际应该用毛利/营业收入）
        BigDecimal margin = netProfit.multiply(BigDecimal.valueOf(100))
            .divide(operatingRevenue, 2, BigDecimal.ROUND_HALF_UP);
        return margin.abs(); // 确保为正数
    }

    /**
     * 计算净资产收益率 (ROE)
     */
    private BigDecimal calculateROE(BigDecimal netProfit, BigDecimal shareholdersEquity) {
        if (netProfit == null || shareholdersEquity == null || shareholdersEquity.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal roe = netProfit.multiply(BigDecimal.valueOf(100))
            .divide(shareholdersEquity, 2, BigDecimal.ROUND_HALF_UP);
        return roe.abs(); // 确保为正数
    }

    /**
     * 计算资产负债率
     */
    private BigDecimal calculateDebtToAssetRatio(BigDecimal totalLiabilities, BigDecimal totalAssets) {
        if (totalLiabilities == null || totalAssets == null || totalAssets.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return totalLiabilities.multiply(BigDecimal.valueOf(100))
            .divide(totalAssets, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString().trim() : null;
    }

    /**
     * 安全获取Integer值
     */
    private Integer getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.debug("[getIntValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
    }

    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        try {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            log.debug("[getBigDecimalValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
    }

}