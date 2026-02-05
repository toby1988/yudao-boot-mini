package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 市场活跃度数据转换器
 * 负责将原始市场活跃度数据转换为MarketActivityDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsMarketActivityDataConverter {

    /**
     * 转换单个市场活跃度数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期
     * @return 市场活跃度DO对象
     */
    public AkToolsMarketActivityDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始市场活跃度数据为空]");
            return null;
        }

        try {
            return AkToolsMarketActivityDO.builder()
                .tradeDate(tradeDate)
                .limitUpCount(getIntValue(rawData, "limit_up_count"))           // 涨停家数
                .limitDownCount(getIntValue(rawData, "limit_down_count"))       // 跌停家数
                .upCount(getIntValue(rawData, "up_count"))                      // 上涨家数
                .downCount(getIntValue(rawData, "down_count"))                  // 下跌家数
                .flatCount(getIntValue(rawData, "flat_count"))                  // 平盘家数
                .explosionRate(getBigDecimalValue(rawData, "explosion_rate"))   // 炸板率
                .limitUpRatio(getBigDecimalValue(rawData, "limit_up_ratio"))    // 涨停占比
                .limitDownRatio(getBigDecimalValue(rawData, "limit_down_ratio")) // 跌停占比
                .turnoverRate(getBigDecimalValue(rawData, "turnover_rate"))     // 换手率
                .volumeRatio(getBigDecimalValue(rawData, "volume_ratio"))       // 量比
                .activityScore(calculateActivityScore(rawData))                 // 活跃度评分
                .marketTemperature(parseMarketTemperature(rawData))             // 市场温度
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][市场活跃度数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 计算市场活跃度评分 (0-100分)
     */
    private Integer calculateActivityScore(Map<String, Object> data) {
        int score = 50; // 基础分
        
        // 涨停家数加分
        Integer limitUpCount = getIntValue(data, "limit_up_count");
        if (limitUpCount != null) {
            score += Math.min(limitUpCount, 20); // 最多加20分
        }
        
        // 跌停家数减分
        Integer limitDownCount = getIntValue(data, "limit_down_count");
        if (limitDownCount != null) {
            score -= Math.min(limitDownCount * 2, 30); // 最多减30分
        }
        
        // 炸板率影响
        BigDecimal explosionRate = getBigDecimalValue(data, "explosion_rate");
        if (explosionRate != null) {
            if (explosionRate.compareTo(new BigDecimal("30")) > 0) {
                score -= 10; // 炸板率高，扣分
            } else if (explosionRate.compareTo(new BigDecimal("10")) < 0) {
                score += 5;  // 炸板率低，加分
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }

    /**
     * 解析市场温度等级
     */
    private String parseMarketTemperature(Map<String, Object> data) {
        Integer activityScore = calculateActivityScore(data);
        
        if (activityScore == null) return "未知";
        
        if (activityScore >= 80) {
            return "过热";
        } else if (activityScore >= 60) {
            return "活跃";
        } else if (activityScore >= 40) {
            return "温和";
        } else if (activityScore >= 20) {
            return "冷淡";
        } else {
            return "冰点";
        }
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
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            log.debug("[getBigDecimalValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
    }

}