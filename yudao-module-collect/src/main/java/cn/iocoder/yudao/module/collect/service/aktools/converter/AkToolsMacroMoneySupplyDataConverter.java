package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 货币供应量数据转换器
 * 负责将原始货币供应量数据转换为MacroMoneySupplyDO对象
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsMacroMoneySupplyDataConverter {

    /**
     * 转换单个货币供应量数据
     *
     * @param rawData 原始数据Map
     * @param tradeDate 交易日期（用于数据标记）
     * @return 货币供应量DO对象
     */
    public AkToolsMacroMoneySupplyDO convert(Map<String, Object> rawData, LocalDate tradeDate) {
        if (rawData == null || rawData.isEmpty()) {
            log.warn("[convert][原始货币供应量数据为空]");
            return null;
        }

        try {
            // 解析统计月份
            String statMonth = parseStatMonth(rawData);
            if (statMonth == null) {
                log.warn("[convert][无法解析统计月份] rawData: {}", rawData);
                return null;
            }

            // 解析各项货币供应量指标 (单位: 亿元)
            BigDecimal m0 = getBigDecimalValue(rawData, "m0");           // 流通中现金
            BigDecimal m1 = getBigDecimalValue(rawData, "m1");           // 狭义货币供应量
            BigDecimal m2 = getBigDecimalValue(rawData, "m2");           // 广义货币供应量
            
            // 解析同比增长率 (%)
            BigDecimal m0YoY = getBigDecimalPercentage(rawData, "m0_yoy");     // M0同比
            BigDecimal m1YoY = getBigDecimalPercentage(rawData, "m1_yoy");     // M1同比
            BigDecimal m2YoY = getBigDecimalPercentage(rawData, "m2_yoy");     // M2同比
            
            // 解析环比增长率 (%)
            BigDecimal m0MoM = getBigDecimalPercentage(rawData, "m0_mom");     // M0环比
            BigDecimal m1MoM = getBigDecimalPercentage(rawData, "m1_mom");     // M1环比
            BigDecimal m2MoM = getBigDecimalPercentage(rawData, "m2_mom");     // M2环比

            // 计算货币乘数等衍生指标
            BigDecimal moneyMultiplier = calculateMoneyMultiplier(m1, m0);     // 货币乘数
            BigDecimal quasiMoney = calculateQuasiMoney(m2, m1);               // 准货币(M2-M1)

            // 解析发布时间
            LocalDate publishDate = parsePublishDate(rawData, tradeDate);

            return AkToolsMacroMoneySupplyDO.builder()
                .statMonth(statMonth)
                .m2Value(m2)
                .m2YoY(m2YoY)
                .m2MoM(m2MoM)
                .m1Value(m1)
                .m1YoY(m1YoY)
                .m1MoM(m1MoM)
                .m0Value(m0)
                .m0YoY(m0YoY)
                .m0MoM(m0MoM)
                .quasiMoneyValue(quasiMoney)
                .quasiMoneyYoY(calculateQuasiMoneyYoY(m2YoY, m1YoY))
                .statDate(publishDate)
                .dataSource("aktools")
                .build();

        } catch (Exception e) {
            log.error("[convert][货币供应量数据转换失败] rawData: {}, error: {}", rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析统计月份
     */
    private String parseStatMonth(Map<String, Object> data) {
        // 尝试多种可能的字段名
        String[] monthKeys = {"month", "stat_month", "publish_month", "date", "period"};
        
        for (String key : monthKeys) {
            Object value = data.get(key);
            if (value != null) {
                String monthStr = value.toString().trim();
                if (isValidMonthFormat(monthStr)) {
                    return monthStr;
                }
            }
        }
        
        // 从日期字段提取月份
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
                    if (dateStr.matches("\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}")) {
                        return LocalDate.parse(dateStr.replace("/", "-"));
                    } else if (dateStr.matches("\\d{4}-\\d{2}")) {
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
     * 计算准货币同比变化率
     */
    private BigDecimal calculateQuasiMoneyYoY(BigDecimal m2YoY, BigDecimal m1YoY) {
        if (m2YoY == null || m1YoY == null) {
            return null;
        }
        // 简化的计算方式：准货币同比 ≈ M2同比 - M1同比
        return m2YoY.subtract(m1YoY).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算准货币 (M2-M1)
     */
    private BigDecimal calculateQuasiMoney(BigDecimal m2, BigDecimal m1) {
        if (m2 == null || m1 == null) {
            return null;
        }
        return m2.subtract(m1);
    }
    private BigDecimal getBigDecimalValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        
        try {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            return new BigDecimal(value.toString().trim());
        } catch (NumberFormatException e) {
            log.debug("[getBigDecimalValue][数值转换失败] key: {}, value: {}", key, value);
            return null;
        }
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
                if (strValue.endsWith("%")) {
                    strValue = strValue.substring(0, strValue.length() - 1);
                }
                decimalValue = new BigDecimal(strValue);
            }
            
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