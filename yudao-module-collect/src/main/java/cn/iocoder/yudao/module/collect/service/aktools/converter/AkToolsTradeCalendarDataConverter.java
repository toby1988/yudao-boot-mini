package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTradeCalendarDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * 交易日历数据转换器
 * 负责将原始交易日历数据转换为TradeCalendarDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsTradeCalendarDataConverter {

    /**
     * 转换单个交易日历数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期
     * @return 交易日历DO对象
     */
    public AkToolsTradeCalendarDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始交易日历数据为空]");
            return null;
        }

        try {
            // 解析交易日期
            LocalDate parsedDate = parseTradeDate(rawData, tradeDate);
            
            return AkToolsTradeCalendarDO.builder()
                .tradeDate(parsedDate)
                .isTradingDay(getBooleanValue(rawData, "is_trading_day", true))  // 是否交易日
                .weekDay(getIntValue(rawData, "week_day"))                      // 星期几 (1-7)
                .month(getIntValue(rawData, "month"))                           // 月份
                .quarter(getIntValue(rawData, "quarter"))                       // 季度
                .isMonthStart(getBooleanValue(rawData, "is_month_start", false)) // 是否月初
                .isMonthEnd(getBooleanValue(rawData, "is_month_end", false))     // 是否月末
                .isQuarterStart(getBooleanValue(rawData, "is_quarter_start", false)) // 是否季度初
                .isQuarterEnd(getBooleanValue(rawData, "is_quarter_end", false))   // 是否季度末
                .isYearStart(getBooleanValue(rawData, "is_year_start", false))     // 是否年初
                .isYearEnd(getBooleanValue(rawData, "is_year_end", false))         // 是否年末
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][交易日历数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析交易日期
     */
    private LocalDate parseTradeDate(Map<String, Object> data, LocalDate defaultDate) {
        // 尝试从数据中解析日期
        Object dateObj = data.get("trade_date");
        if (dateObj != null) {
            try {
                String dateStr = dateObj.toString().trim();
                if (dateStr.matches("\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}")) {
                    return LocalDate.parse(dateStr.replace("/", "-"));
                }
            } catch (Exception e) {
                log.debug("[parseTradeDate][日期解析失败] value: {}", dateObj);
            }
        }
        return defaultDate;
    }

    /**
     * 安全获取Boolean值
     */
    private Boolean getBooleanValue(Map<String, Object> data, String key, Boolean defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;
        
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        String strValue = value.toString().toLowerCase().trim();
        switch (strValue) {
            case "true":
            case "1":
            case "yes":
            case "是":
            case "y":
                return true;
            case "false":
            case "0":
            case "no":
            case "否":
            case "n":
                return false;
            default:
                return defaultValue;
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

}