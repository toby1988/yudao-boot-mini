package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockSpotDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.converter.StockSpotDataConverter;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * 股票实时行情数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockSpotDataStrategy extends AbstractAkToolsDataStrategy<AkToolsStockSpotDO> {

    private final StockSpotDataConverter stockSpotDataConverter;

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.STOCK_SPOT;
    }

    @Override
    public Map<String, Object> buildRequestParams(Object param) {
        if (param instanceof LocalDate) {
            LocalDate tradeDate = (LocalDate) param;
            return Map.of("date", DateUtil.format(tradeDate, DatePattern.NORM_DATE_PATTERN));
        }
        return super.buildRequestParams(param);
    }

    @Override
    public AkToolsStockSpotDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        
        return stockSpotDataConverter.convert(dataMap, context.getTradeDate());
    }

    @Override
    public String getDefaultCollectTime() {
        return "15:15";
    }

}