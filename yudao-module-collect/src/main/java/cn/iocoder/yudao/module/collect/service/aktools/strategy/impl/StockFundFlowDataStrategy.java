package cn.iocoder.yudao.module.collect.service.aktools.strategy.impl;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockFundFlowDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AbstractAkToolsDataStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票资金流向数据采集策略实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class StockFundFlowDataStrategy extends AbstractAkToolsDataStrategy<AkToolsStockFundFlowDO> {

    @Override
    public AkApiEnum getApiEnum() {
        return AkApiEnum.STOCK_FUND_FLOW;
    }

    @Override
    public AkToolsStockFundFlowDO convertData(Object rawData, ConversionContext context) {
        if (!(rawData instanceof Map)) {
            throw new IllegalArgumentException("原始数据格式错误，期望Map类型");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) rawData;
        
        return AkToolsStockFundFlowDO.builder()
            .symbol(getStringValue(dataMap, "symbol"))
            .name(getStringValue(dataMap, "name"))
            .tradeDate(context.getTradeDate())
            .mainInflow(getBigDecimalValue(dataMap, "main_net"))
            .superLargeInflow(getBigDecimalValue(dataMap, "super_large_net"))
            .largeInflow(getBigDecimalValue(dataMap, "large_net"))
            .mediumInflow(getBigDecimalValue(dataMap, "medium_net"))
            .smallInflow(getBigDecimalValue(dataMap, "small_net"))
            .mainInflowRatio(getBigDecimalValue(dataMap, "main_net_ratio"))
            .superLargeInflowRatio(getBigDecimalValue(dataMap, "super_large_net_ratio"))
            .dataSource("aktools")
            .build();
    }

    @Override
    public String getDefaultCollectTime() {
        return "16:30";
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