package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockValuationDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票估值数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class StockValuationDataStrategy extends AbstractAkToolsDataStrategy<AkToolsStockValuationDO> {

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.STOCK_VALUATION;
    }

    @Override
    public AkToolsStockValuationDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        
        return AkToolsStockValuationDO.builder()
            .symbol(getStringValue(dataMap, "symbol"))
            .name(getStringValue(dataMap, "name"))
            .tradeDate(context.getTradeDate())
            .peTtm(getBigDecimalValue(dataMap, "pe_ttm"))
            .pbRatio(getBigDecimalValue(dataMap, "pb"))
            .psRatio(getBigDecimalValue(dataMap, "ps"))
            .dividendYield(getBigDecimalValue(dataMap, "dv_ratio"))
            .totalMarketValue(getBigDecimalValue(dataMap, "total_mv"))
            .circulatingMarketValue(getBigDecimalValue(dataMap, "circulating_mv"))
            .dataSource("aktools")
            .build();
    }

    @Override
    public String getDefaultCollectTime() {
        return "18:00";
    }

    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

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