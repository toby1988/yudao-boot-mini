package cn.iocoder.yudao.module.collect.service.aktools.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * AkTools 数据采集监控指标
 * 负责收集和上报数据采集的各项指标
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
public class AkToolsMetricsCollector {

    private final MeterRegistry meterRegistry;

    // 计数器
    private final Counter collectSuccessCounter;
    private final Counter collectFailureCounter;
    private final Counter dataConvertSuccessCounter;
    private final Counter dataConvertFailureCounter;

    // 计时器
    private final Timer collectTimer;
    private final Timer convertTimer;

    public AkToolsMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // 初始化计数器
        this.collectSuccessCounter = Counter.builder("aktools.collect.success")
            .description("AkTools数据采集成功次数")
            .register(meterRegistry);

        this.collectFailureCounter = Counter.builder("aktools.collect.failure")
            .description("AkTools数据采集失败次数")
            .register(meterRegistry);

        this.dataConvertSuccessCounter = Counter.builder("aktools.convert.success")
            .description("AkTools数据转换成功次数")
            .register(meterRegistry);

        this.dataConvertFailureCounter = Counter.builder("aktools.convert.failure")
            .description("AkTools数据转换失败次数")
            .register(meterRegistry);

        // 初始化计时器
        this.collectTimer = Timer.builder("aktools.collect.duration")
            .description("AkTools数据采集耗时")
            .register(meterRegistry);

        this.convertTimer = Timer.builder("aktools.convert.duration")
            .description("AkTools数据转换耗时")
            .register(meterRegistry);
    }

    /**
     * 记录采集成功
     */
    public void recordCollectSuccess(String apiName, int dataCount) {
        collectSuccessCounter.increment();
        log.info("[recordCollectSuccess][采集成功] api: {}, dataCount: {}", apiName, dataCount);
    }

    /**
     * 记录采集失败
     */
    public void recordCollectFailure(String apiName, String errorCode, Throwable throwable) {
        collectFailureCounter.increment();
        log.error("[recordCollectFailure][采集失败] api: {}, errorCode: {}, error: {}", 
            apiName, errorCode, throwable.getMessage(), throwable);
    }

    /**
     * 记录数据转换成功
     */
    public void recordConvertSuccess(String apiName, int originalCount, int convertedCount) {
        dataConvertSuccessCounter.increment();
        log.info("[recordConvertSuccess][转换成功] api: {}, original: {}, converted: {}", 
            apiName, originalCount, convertedCount);
    }

    /**
     * 记录数据转换失败
     */
    public void recordConvertFailure(String apiName, int dataCount, Throwable throwable) {
        dataConvertFailureCounter.increment();
        log.error("[recordConvertFailure][转换失败] api: {}, dataCount: {}, error: {}", 
            apiName, dataCount, throwable.getMessage(), throwable);
    }

    /**
     * 记录采集耗时
     */
    public void recordCollectDuration(String apiName, long durationMs) {
        collectTimer.record(durationMs, TimeUnit.MILLISECONDS);
        log.debug("[recordCollectDuration][采集耗时] api: {}, duration: {}ms", apiName, durationMs);
    }

    /**
     * 记录转换耗时
     */
    public void recordConvertDuration(String apiName, long durationMs) {
        convertTimer.record(durationMs, TimeUnit.MILLISECONDS);
        log.debug("[recordConvertDuration][转换耗时] api: {}, duration: {}ms", apiName, durationMs);
    }

    /**
     * 获取采集成功率
     */
    public double getCollectSuccessRate() {
        long successCount = (long) collectSuccessCounter.count();
        long failureCount = (long) collectFailureCounter.count();
        long totalCount = successCount + failureCount;
        
        return totalCount > 0 ? (double) successCount / totalCount : 0.0;
    }

    /**
     * 获取转换成功率
     */
    public double getConvertSuccessRate() {
        long successCount = (long) dataConvertSuccessCounter.count();
        long failureCount = (long) dataConvertFailureCounter.count();
        long totalCount = successCount + failureCount;
        
        return totalCount > 0 ? (double) successCount / totalCount : 0.0;
    }

}