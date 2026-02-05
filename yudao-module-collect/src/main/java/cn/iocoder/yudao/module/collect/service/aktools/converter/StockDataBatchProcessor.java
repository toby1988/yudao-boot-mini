package cn.iocoder.yudao.module.collect.service.aktools.converter;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockSpotDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 股票数据批量处理器
 * 负责批量数据的处理和转换
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataBatchProcessor {

    private final StockSpotDataConverter stockSpotDataConverter;

    /**
     * 批量转换股票行情数据
     *
     * @param rawDataList 原始数据列表
     * @param tradeDate 交易日期
     * @return 转换后的DO对象列表
     */
    public List<AkToolsStockSpotDO> batchConvertStockSpotData(List<Map<String, Object>> rawDataList, LocalDate tradeDate) {
        if (CollUtil.isEmpty(rawDataList)) {
            log.warn("[batchConvertStockSpotData][原始数据为空]");
            return Collections.emptyList();
        }

        log.info("[batchConvertStockSpotData][开始批量转换] dataCount: {}, tradeDate: {}", 
            rawDataList.size(), tradeDate);

        List<AkToolsStockSpotDO> resultList = rawDataList.parallelStream()
            .map(data -> {
                try {
                    return stockSpotDataConverter.convert(data, tradeDate);
                } catch (Exception e) {
                    log.error("[batchConvertStockSpotData][单条数据转换失败] data: {}, error: {}", 
                        data, e.getMessage(), e);
                    return null;
                }
            })
            .filter(item -> item != null && item.getSymbol() != null)
            .collect(Collectors.toList());

        log.info("[batchConvertStockSpotData][批量转换完成] originalCount: {}, convertedCount: {}", 
            rawDataList.size(), resultList.size());

        return resultList;
    }

    /**
     * 批量保存数据（带重试机制）
     *
     * @param dataList 数据列表
     * @param saveFunction 保存函数
     * @param maxRetries 最大重试次数
     * @param <T> 数据类型
     * @return 保存成功的记录数
     */
    public <T> int batchSaveWithRetry(List<T> dataList, SaveFunction<T> saveFunction, int maxRetries) {
        if (CollUtil.isEmpty(dataList)) {
            return 0;
        }

        int successCount = 0;
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                successCount = saveFunction.save(dataList);
                log.info("[batchSaveWithRetry][保存成功] attempt: {}, successCount: {}", 
                    attempt + 1, successCount);
                return successCount;
            } catch (Exception e) {
                lastException = e;
                attempt++;
                if (attempt <= maxRetries) {
                    log.warn("[batchSaveWithRetry][保存失败，准备重试] attempt: {}, error: {}", 
                        attempt, e.getMessage());
                    try {
                        Thread.sleep(1000L * attempt); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("[batchSaveWithRetry][保存最终失败] maxRetries: {}, error: {}", 
            maxRetries, lastException.getMessage(), lastException);
        throw new RuntimeException("批量保存数据失败", lastException);
    }

    /**
     * 保存函数接口
     */
    @FunctionalInterface
    public interface SaveFunction<T> {
        int save(List<T> dataList) throws Exception;
    }

}