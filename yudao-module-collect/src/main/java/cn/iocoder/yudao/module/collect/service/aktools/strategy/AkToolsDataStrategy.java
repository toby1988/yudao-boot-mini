package cn.iocoder.yudao.module.collect.service.aktools.strategy;

import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;

import java.time.LocalDate;
import java.util.Map;

/**
 * AkTools 数据采集策略接口
 * 定义统一的数据采集策略规范
 *
 * @param <T> 返回的数据类型
 * @author ToBy.Qoder
 */
public interface AkToolsDataStrategy<T> {

    /**
     * 获取策略对应的API枚举
     *
     * @return API枚举
     */
    AkApiEnum getApiEnum();

    /**
     * 构建请求参数
     *
     * @param param 业务参数
     * @return 请求参数Map
     */
    Map<String, Object> buildRequestParams(Object param);

    /**
     * 转换原始数据为业务对象
     *
     * @param rawData 原始数据
     * @param context 转换上下文
     * @return 业务对象
     */
    T convertData(Object rawData, ConversionContext context);

    /**
     * 获取默认采集时间
     *
     * @return 默认采集时间
     */
    default String getDefaultCollectTime() {
        return "18:00";
    }

    /**
     * 数据转换上下文
     */
    class ConversionContext {
        private final LocalDate tradeDate;
        private final String month;
        private final Object additionalParams;

        public ConversionContext(LocalDate tradeDate, String month, Object additionalParams) {
            this.tradeDate = tradeDate;
            this.month = month;
            this.additionalParams = additionalParams;
        }

        public LocalDate getTradeDate() {
            return tradeDate;
        }

        public String getMonth() {
            return month;
        }

        public Object getAdditionalParams() {
            return additionalParams;
        }

        public static ConversionContext forTradeDate(LocalDate tradeDate) {
            return new ConversionContext(tradeDate, null, null);
        }

        public static ConversionContext forMonth(String month) {
            return new ConversionContext(null, month, null);
        }

        public static ConversionContext forTradeDateWithParams(LocalDate tradeDate, Object params) {
            return new ConversionContext(tradeDate, null, params);
        }
    }

}