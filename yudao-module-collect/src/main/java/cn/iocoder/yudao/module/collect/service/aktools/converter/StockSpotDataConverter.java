package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockSpotDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 股票实时行情数据转换器
 * 负责将原始数据转换为StockSpotDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class StockSpotDataConverter {

    /**
     * 转换单个股票行情数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期
     * @return 股票行情DO对象
     */
    public AkToolsStockSpotDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始数据为空]");
            return null;
        }

        try {
            return AkToolsStockSpotDO.builder()
                .symbol(getStringValue(rawData, "symbol"))
                .name(getStringValue(rawData, "name"))
                .tradeDate(tradeDate)
                .openPrice(getBigDecimalValue(rawData, "open"))
                .closePrice(getBigDecimalValue(rawData, "close"))
                .highPrice(getBigDecimalValue(rawData, "high"))
                .lowPrice(getBigDecimalValue(rawData, "low"))
                .latestPrice(getBigDecimalValue(rawData, "price"))
                .volume(getLongValue(rawData, "volume"))
                .turnover(getBigDecimalValue(rawData, "turnover"))
                .changeAmount(getBigDecimalValue(rawData, "change"))
                .changePercent(getBigDecimalValue(rawData, "change_percent"))
                .turnoverRate(getBigDecimalValue(rawData, "turnover_rate"))
                .peRatio(getBigDecimalValue(rawData, "pe"))
                .pbRatio(getBigDecimalValue(rawData, "pb"))
                .totalMarketValue(getBigDecimalValue(rawData, "total_market_value"))
                .circulatingMarketValue(getBigDecimalValue(rawData, "circulating_market_value"))
                .dataSource("aktools")
                .build();
        } catch (Exception e) {
            log.error("[convert][数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
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