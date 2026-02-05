package cn.iocoder.yudao.module.collect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AkTools 数据采集配置
 * 管理数据采集的各种配置参数
 *
 * @author ToBy.Qoder
 */
@Data
@Component
@ConfigurationProperties(prefix = "aktools.collect")
public class AkToolsCollectConfig {

    /**
     * 默认重试次数
     */
    private int defaultRetryCount = 3;

    /**
     * 默认超时时间（毫秒）
     */
    private int defaultTimeoutMs = 30000;

    /**
     * 批量处理大小
     */
    private int batchSize = 1000;

    /**
     * 是否启用并行处理
     */
    private boolean enableParallelProcessing = true;

    /**
     * 并行处理线程数
     */
    private int parallelThreadCount = 8;

    /**
     * 数据采集时间配置
     */
    private TimeConfig time = new TimeConfig();

    /**
     * 重试配置
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * 时间配置
     */
    @Data
    public static class TimeConfig {
        /**
         * 股票实时行情采集时间 (HH:mm)
         */
        private String stockSpotTime = "15:15";

        /**
         * 股票估值采集时间 (HH:mm)
         */
        private String stockValuationTime = "18:00";

        /**
         * 资金流向采集时间 (HH:mm)
         */
        private String fundFlowTime = "16:30";

        /**
         * 北向资金采集时间 (HH:mm)
         */
        private String northFundTime = "17:00";

        /**
         * 指数行情采集时间 (HH:mm)
         */
        private String indexSpotTime = "15:30";
    }

    /**
     * 重试配置
     */
    @Data
    public static class RetryConfig {
        /**
         * 最大重试次数
         */
        private int maxAttempts = 3;

        /**
         * 初始延迟时间（毫秒）
         */
        private long initialDelayMs = 1000;

        /**
         * 最大延迟时间（毫秒）
         */
        private long maxDelayMs = 10000;

        /**
         * 退避乘数
         */
        private double multiplier = 2.0;
    }

}