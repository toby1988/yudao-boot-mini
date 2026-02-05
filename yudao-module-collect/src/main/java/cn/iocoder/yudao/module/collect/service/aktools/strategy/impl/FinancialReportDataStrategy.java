package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.converter.AkToolsFinancialReportDataConverter;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 财务报表数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialReportDataStrategy extends AbstractAkToolsDataStrategy<AkToolsFinancialReportDO> {

    private final AkToolsFinancialReportDataConverter financialReportDataConverter;

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.FINANCIAL_REPORT;
    }

    @Override
    public Map<String, Object> buildRequestParams(Object param) {
        String month = (String) param;
        return MapUtil.builder("month", month).build();
    }

    @Override
    public List<Object> extractDataFromResponse(Object response) {
        if (response instanceof List) {
            return (List<Object>) response;
        }
        return List.of();
    }

    @Override
    public AkToolsFinancialReportDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        return financialReportDataConverter.convert(dataMap, context.getTradeDate());
    }

}