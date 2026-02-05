package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsNorthFundDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 北向资金数据转换器
 * 负责将原始北向资金数据转换为NorthFundDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsNorthFundDataConverter {

    /**
     * 转换单个北向资金数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期
     * @return 北向资金DO对象
     */
    public AkToolsNorthFundDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始北向资金数据为空]");
            return null;
        }

        try {
            return AkToolsNorthFundDO.builder()
                .tradeDate(tradeDate)
                .netInflow(getBigDecimalValue(rawData, "net_inflow"))           // 当日净买入额
                .cumulativePosition(getBigDecimalValue(rawData, "cumulative_position")) // 累计持仓市值
                .shanghaiInflow(getBigDecimalValue(rawData, "shanghai_inflow"))  // 沪股通净买入
                .shenzhenInflow(getBigDecimalValue(rawData, "shenzhen_inflow"))  // 深股通净买入
                .holdingMarketValue(getBigDecimalValue(rawData, "holding_value")) // 持股市值
                .holdingRatio(getBigDecimalValue(rawData, "holding_ratio"))     // 持股占流通股比例
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][北向资金数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
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