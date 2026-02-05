package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 宏观经济CPI数据转换器
 * 负责将原始CPI数据转换为MacroCpiDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsMacroCpiDataConverter {

    /**
     * 转换单个CPI数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期（用于数据标记）
     * @return CPI数据DO对象
     */
    public AkToolsMacroCpiDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始CPI数据为空]");
            return null;
        }

        try {
            // 解析月份信息
            String publishMonth = parsePublishMonth(rawData);
            if (publishMonth == null) {
                log.warn("[convert][无法解析发布月份] rawData: {}", rawData);
                return null;
            }

            // 解析各项CPI指标
            BigDecimal nationalYoY = getBigDecimalPercentage(rawData, "national_yoy");     // 全国同比
            BigDecimal nationalMoM = getBigDecimalPercentage(rawData, "national_mom");     // 全国环比
            BigDecimal urbanYoY = getBigDecimalPercentage(rawData, "urban_yoy");           // 城市同比
            BigDecimal ruralYoY = getBigDecimalPercentage(rawData, "rural_yoy");           // 农村同比
            BigDecimal foodYoY = getBigDecimalPercentage(rawData, "food_yoy");             // 食品同比
            BigDecimal nonFoodYoY = getBigDecimalPercentage(rawData, "non_food_yoy");      // 非食品同比
            BigDecimal consumerGoodsYoY = getBigDecimalPercentage(rawData, "consumer_goods_yoy"); // 消费品同比
            BigDecimal servicesYoY = getBigDecimalPercentage(rawData, "services_yoy");     // 服务同比

            // 解析发布时间
            LocalDate publishDate = parsePublishDate(rawData, tradeDate);

            return AkToolsMacroCpiDO.builder()
                .publishMonth(publishMonth)
                .nationalYoY(nationalYoY)
                .nationalMoM(nationalMoM)
                .urbanYoY(urbanYoY)
                .ruralYoY(ruralYoY)
                .foodYoY(foodYoY)
                .nonFoodYoY(nonFoodYoY)
                .consumerGoodsYoY(consumerGoodsYoY)
                .servicesYoY(servicesYoY)
                .publishDate(publishDate)
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][CPI数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析发布月份
     */
    private String parsePublishMonth(Map<String, Object> data) {
        // 尝试多种可能的字段名
        String[] monthKeys = {"month", "publish_month", "stat_month", "date", "period"};
        
        for (String key : monthKeys) {
            Object value = data.get(key);
            if (value != null) {
                String monthStr = value.toString().trim();
                if (isValidMonthFormat(monthStr)) {
                    return monthStr;
                }
            }
        }
        
        // 如果找不到标准格式，尝试从日期字段提取
        return extractMonthFromDateString(data);
    }

    /**
     * 从日期字符串中提取月份
     */
    private String extractMonthFromDateString(Map<String, Object> data) {
        String[] dateKeys = {"publish_date", "release_date", "date", "time"};
        
        for (String key : dateKeys) {
            Object value = data.get(key);
            if (value != null) {
                String dateStr = value.toString().trim();
                if (dateStr.matches("\\d{4}[-./]\\d{1,2}([-./]\\d{1,2})?")) {
                    // 提取年月部分
                    String[] parts = dateStr.split("[-./]");
                    if (parts.length >= 2) {
                        String year = parts[0];
                        String month = String.format("%02d", Integer.parseInt(parts[1]));
                        return year + "-" + month;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 验证月份格式是否正确 (yyyy-MM)
     */
    private boolean isValidMonthFormat(String month) {
        return month != null && month.matches("\\d{4}-\\d{2}");
    }

    /**
     * 解析发布时间
     */
    private LocalDate parsePublishDate(Map<String, Object> data, LocalDate defaultDate) {
        String[] dateKeys = {"publish_date", "release_date", "date"};
        
        for (String key : dateKeys) {
            Object value = data.get(key);
            if (value != null) {
                try {
                    String dateStr = value.toString().trim();
                    // 处理不同日期格式
                    if (dateStr.matches("\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}")) {
                        return LocalDate.parse(dateStr.replace("/", "-"));
                    } else if (dateStr.matches("\\d{4}-\\d{2}")) {
                        // 只有年月的情况，默认为当月第一天
                        return LocalDate.parse(dateStr + "-01");
                    }
                } catch (Exception e) {
                    log.debug("[parsePublishDate][日期解析失败] key: {}, value: {}", key, value);
                }
            }
        }
        
        return defaultDate;
    }

    /**
     * 安全获取百分比数值
     */
    private BigDecimal getBigDecimalPercentage(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        
        try {
            BigDecimal decimalValue;
            if (value instanceof Number) {
                decimalValue = new BigDecimal(value.toString());
            } else {
                String strValue = value.toString().trim();
                // 处理带%号的情况
                if (strValue.endsWith("%")) {
                    strValue = strValue.substring(0, strValue.length() - 1);
                }
                decimalValue = new BigDecimal(strValue);
            }
            
            // 保留2位小数
            return decimalValue.setScale(2, BigDecimal.ROUND_HALF_UP);
            
        } catch (NumberFormatException e) {
            log.debug("[getBigDecimalPercentage][百分比转换失败] key: {}, value: {}", key, value);
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

}