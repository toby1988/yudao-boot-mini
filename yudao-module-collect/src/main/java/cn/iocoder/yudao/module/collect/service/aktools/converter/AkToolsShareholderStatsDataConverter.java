package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsShareholderStatsDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 股东户数数据转换器
 * 负责将原始股东户数数据转换为ShareholderStatsDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsShareholderStatsDataConverter {

    /**
     * 转换单个股东户数数据
     *
     * @param rawData 原始数据Map
     * @param endDate 截止日期
     * @return 股东户数DO对象
     */
    public AkToolsShareholderStatsDO convert(Map<String, Object> rawData, LocalDate endDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始股东户数数据为空]");
            return null;
        }

        try {
            String symbol = getStringValue(rawData, "symbol");
            String name = getStringValue(rawData, "name");
            
            if (symbol == null || name == null) {
                log.warn("[convert][缺少必要字段] symbol: {}, name: {}", symbol, name);
                return null;
            }

            return AkToolsShareholderStatsDO.builder()
                .symbol(symbol)
                .name(name)
                .endDate(endDate)
                .shareholderCount(getLongValue(rawData, "shareholder_count"))      // 股东户数
                .avgSharesPerAccount(getBigDecimalValue(rawData, "avg_shares_per_account")) // 户均持股数
                .totalShares(getBigDecimalValue(rawData, "total_shares"))          // 总股本
                .circulatingShares(getBigDecimalValue(rawData, "circulating_shares")) // 流通股本
                .changePercent(getBigDecimalValue(rawData, "change_percent"))       // 环比变化
                .concentrationLevel(parseConcentrationLevel(rawData))              // 集中度等级
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][股东户数数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析集中度等级
     */
    private String parseConcentrationLevel(Map<String, Object> data) {
        Object value = data.get("concentration_level");
        if (value != null) {
            return value.toString().trim();
        }
        
        // 根据股东户数变化推断集中度
        BigDecimal changePercent = getBigDecimalValue(data, "change_percent");
        if (changePercent != null) {
            if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
                return "集中";  // 股东户数减少，筹码集中
            } else if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
                return "分散";  // 股东户数增加，筹码分散
            }
        }
        return "稳定";
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString().trim() : null;
    }

    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            log.debug("[getBigDecimalValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
    }

    /**
     * 安全获取Long值
     */
    private Long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.debug("[getLongValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
    }

}