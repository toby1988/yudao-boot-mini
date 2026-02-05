package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.converter.AkToolsMarketActivityDataConverter;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 市场活跃度数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketActivityDataStrategy extends AbstractAkToolsDataStrategy<AkToolsMarketActivityDO> {

    private final AkToolsMarketActivityDataConverter marketActivityDataConverter;

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.MARKET_ACTIVITY;
    }

    @Override
    public Map<String, Object> buildRequestParams(Object param) {
        LocalDate tradeDate = (LocalDate) param;
        return MapUtil.builder("date", tradeDate.toString()).build();
    }

    @Override
    public List<Object> extractDataFromResponse(Object response) {
        if (response instanceof List) {
            return (List<Object>) response;
        }
        return List.of();
    }

    @Override
    public AkToolsMarketActivityDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        return marketActivityDataConverter.convert(dataMap, context.getTradeDate());
    }

}