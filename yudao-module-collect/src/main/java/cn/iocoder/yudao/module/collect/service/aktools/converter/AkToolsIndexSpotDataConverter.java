package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsIndexSpotDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 指数行情数据转换器
 * 负责将原始指数数据转换为IndexSpotDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsIndexSpotDataConverter {

    /**
     * 转换单个指数行情数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期
     * @return 指数行情DO对象
     */
    public AkToolsIndexSpotDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始指数数据为空]");
            return null;
        }

        try {
            return AkToolsIndexSpotDO.builder()
                .indexCode(getStringValue(rawData, "index_code"))           // 指数代码
                .indexName(getStringValue(rawData, "index_name"))           // 指数名称
                .tradeDate(tradeDate)
                .openPrice(getBigDecimalValue(rawData, "open"))             // 开盘价
                .closePrice(getBigDecimalValue(rawData, "close"))           // 收盘价
                .highPrice(getBigDecimalValue(rawData, "high"))             // 最高价
                .lowPrice(getBigDecimalValue(rawData, "low"))               // 最低价
                .latestPrice(getBigDecimalValue(rawData, "price"))          // 最新价
                .volume(getLongValue(rawData, "volume"))                    // 成交量
                .turnover(getBigDecimalValue(rawData, "turnover"))          // 成交额
                .changeAmount(getBigDecimalValue(rawData, "change"))        // 涨跌额
                .changePercent(getBigDecimalValue(rawData, "change_percent")) // 涨跌幅
                .turnoverRate(getBigDecimalValue(rawData, "turnover_rate"))   // 换手率
                .peRatio(getBigDecimalValue(rawData, "pe"))                 // 市盈率
                .pbRatio(getBigDecimalValue(rawData, "pb"))                 // 市净率
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][指数数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
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