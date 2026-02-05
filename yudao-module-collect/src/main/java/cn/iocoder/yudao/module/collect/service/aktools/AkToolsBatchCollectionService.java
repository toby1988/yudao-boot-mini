package cn.iocoder.yudao.module.collect.service.aktools;

import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.job.executor.AkToolsTaskExecutor;
import cn.iocoder.yudao.module.collect.job.factory.AkToolsTaskExecutorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AkTools批量数据采集服务
 * 提供手动触发和批量执行功能
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AkToolsBatchCollectionService {

    private final AkToolsTaskExecutorFactory executorFactory;

    /**
     * 执行所有日维度数据采集任务
     *
     * @param tradeDate 交易日期
     * @return 采集结果统计
     */
    public BatchCollectionResult collectAllDailyData(LocalDate tradeDate) {
        log.info("[collectAllDailyData][开始执行日维度批量采集] tradeDate: {}", tradeDate);
        
        List<TaskExecutionResult> results = new ArrayList<>();
        int totalSuccess = 0;
        int totalFailure = 0;
        
        // 定义日维度API枚举
        AkApiEnum[] dailyApis = {
            AkApiEnum.STOCK_SPOT,
            AkApiEnum.STOCK_VALUATION,
            AkApiEnum.STOCK_FUND_FLOW,
            AkApiEnum.NORTH_FUND,
            AkApiEnum.INDEX_SPOT,
            AkApiEnum.MARKET_ACTIVITY
        };
        
        for (AkApiEnum apiEnum : dailyApis) {
            try {
                TaskExecutionResult result = executeTask(apiEnum, tradeDate);
                results.add(result);
                if (result.isSuccess()) {
                    totalSuccess++;
                } else {
                    totalFailure++;
                }
            } catch (Exception e) {
                log.error("[collectAllDailyData][任务执行异常] api: {}, error: {}", 
                    apiEnum.getApiPath(), e.getMessage(), e);
                results.add(new TaskExecutionResult(apiEnum, false, 0, e.getMessage()));
                totalFailure++;
            }
        }
        
        BatchCollectionResult batchResult = new BatchCollectionResult(results, totalSuccess, totalFailure);
        log.info("[collectAllDailyData][日维度批量采集完成] {}", batchResult);
        return batchResult;
    }

    /**
     * 执行所有月维度数据采集任务
     *
     * @param month 月份 (yyyy-MM格式)
     * @return 采集结果统计
     */
    public BatchCollectionResult collectAllMonthlyData(String month) {
        log.info("[collectAllMonthlyData][开始执行月维度批量采集] month: {}", month);
        
        List<TaskExecutionResult> results = new ArrayList<>();
        int totalSuccess = 0;
        int totalFailure = 0;
        
        // 定义月维度API枚举
        AkApiEnum[] monthlyApis = {
            AkApiEnum.FINANCIAL_REPORT,
            AkApiEnum.SHAREHOLDER_STATS,
            AkApiEnum.MACRO_CPI,
            AkApiEnum.MACRO_MONEY_SUPPLY
        };
        
        for (AkApiEnum apiEnum : monthlyApis) {
            try {
                TaskExecutionResult result = executeTask(apiEnum, month);
                results.add(result);
                if (result.isSuccess()) {
                    totalSuccess++;
                } else {
                    totalFailure++;
                }
            } catch (Exception e) {
                log.error("[collectAllMonthlyData][任务执行异常] api: {}, error: {}", 
                    apiEnum.getApiPath(), e.getMessage(), e);
                results.add(new TaskExecutionResult(apiEnum, false, 0, e.getMessage()));
                totalFailure++;
            }
        }
        
        BatchCollectionResult batchResult = new BatchCollectionResult(results, totalSuccess, totalFailure);
        log.info("[collectAllMonthlyData][月维度批量采集完成] {}", batchResult);
        return batchResult;
    }

    /**
     * 执行特定API的数据采集任务
     *
     * @param apiEnum API枚举
     * @param param 任务参数
     * @return 执行结果
     */
    public TaskExecutionResult executeTask(AkApiEnum apiEnum, Object param) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("[executeTask][开始执行任务] api: {}, param: {}", apiEnum.getApiPath(), param);
            
            AkToolsTaskExecutor executor = executorFactory.getExecutor(apiEnum);
            int recordCount = executor.execute(param);
            
            TaskExecutionResult result = new TaskExecutionResult(apiEnum, true, recordCount, null);
            log.info("[executeTask][任务执行成功] api: {}, duration: {}ms, records: {}", 
                apiEnum.getApiPath(), System.currentTimeMillis() - startTime, recordCount);
            
            return result;
            
        } catch (Exception e) {
            log.error("[executeTask][任务执行失败] api: {}, param: {}, error: {}", 
                apiEnum.getApiPath(), param, e.getMessage(), e);
            
            return new TaskExecutionResult(apiEnum, false, 0, e.getMessage());
        }
    }

    /**
     * 获取所有可用的任务API枚举
     *
     * @return API枚举列表
     */
    public List<AkApiEnum> getAvailableApis() {
        return executorFactory.getAllApiEnums();
    }

    /**
     * 任务执行结果
     */
    public static class TaskExecutionResult {
        private final AkApiEnum apiEnum;
        private final boolean success;
        private final int recordCount;
        private final String errorMessage;

        public TaskExecutionResult(AkApiEnum apiEnum, boolean success, int recordCount, String errorMessage) {
            this.apiEnum = apiEnum;
            this.success = success;
            this.recordCount = recordCount;
            this.errorMessage = errorMessage;
        }

        // Getters
        public AkApiEnum getApiEnum() { return apiEnum; }
        public boolean isSuccess() { return success; }
        public int getRecordCount() { return recordCount; }
        public String getErrorMessage() { return errorMessage; }

        @Override
        public String toString() {
            return String.format("TaskExecutionResult{api=%s, success=%s, records=%d, error='%s'}", 
                apiEnum.getApiPath(), success, recordCount, errorMessage);
        }
    }

    /**
     * 批量采集结果
     */
    public static class BatchCollectionResult {
        private final List<TaskExecutionResult> taskResults;
        private final int successCount;
        private final int failureCount;

        public BatchCollectionResult(List<TaskExecutionResult> taskResults, int successCount, int failureCount) {
            this.taskResults = taskResults;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        // Getters
        public List<TaskExecutionResult> getTaskResults() { return taskResults; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getTotalCount() { return successCount + failureCount; }

        @Override
        public String toString() {
            return String.format("BatchCollectionResult{total=%d, success=%d, failure=%d}", 
                getTotalCount(), successCount, failureCount);
        }
    }

}