package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsNorthFundDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.converter.AkToolsNorthFundDataConverter;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 北向资金数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NorthFundDataStrategy extends AbstractAkToolsDataStrategy<AkToolsNorthFundDO> {

    private final AkToolsNorthFundDataConverter northFundDataConverter;

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.NORTH_FUND;
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
    public AkToolsNorthFundDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        return northFundDataConverter.convert(dataMap, context.getTradeDate());
    }

}