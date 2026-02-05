package cn.iocoder.yudao.module.collect.service.aktools.core;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.*;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.economy.AkToolsMacroCpiMapper;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.economy.AkToolsMacroMoneySupplyMapper;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.financial.AkToolsFinancialReportMapper;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.market.AkToolsMarketActivityMapper;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.framework.aktools.exception.AkToolsException;
import cn.iocoder.yudao.module.collect.framework.aktools.http.AkToolsHttpClient;
import cn.iocoder.yudao.module.collect.service.aktools.converter.StockDataBatchProcessor;
import cn.iocoder.yudao.module.collect.service.aktools.factory.AkToolsStrategyFactory;
import cn.iocoder.yudao.module.collect.service.aktools.metrics.AkToolsMetricsCollector;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AkTools 数据采集协调器
 * 基于策略+工厂模式的核心服务实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AkToolsDataCoordinator {

    private final AkToolsHttpClient akToolsHttpClient;
    private final AkToolsStrategyFactory strategyFactory;
    private final AkToolsMetricsCollector metricsCollector;
    private final StockDataBatchProcessor stockDataBatchProcessor;

    // Mapper依赖
    private final AkToolsStockSpotMapper stockSpotMapper;
    private final AkToolsStockValuationMapper stockValuationMapper;
    private final AkToolsStockFundFlowMapper stockFundFlowMapper;
    private final AkToolsNorthFundMapper northFundMapper;
    private final AkToolsIndexSpotMapper indexSpotMapper;
    private final AkToolsFinancialReportMapper financialReportMapper;
    private final AkToolsShareholderStatsMapper shareholderStatsMapper;
    private final AkToolsMarketActivityMapper marketActivityMapper;
    private final AkToolsMacroCpiMapper macroCpiMapper;
    private final AkToolsMacroMoneySupplyMapper macroMoneySupplyMapper;
    private final AkToolsTradeCalendarMapper tradeCalendarMapper;

    /**
     * 通用数据采集方法
     *
     * @param apiEnum API枚举
     * @param param 业务参数
     * @param saveFunction 保存函数
     * @param <T> 数据类型
     * @return 采集记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> int collectData(AkApiEnum apiEnum, Object param, SaveFunction<T> saveFunction) {
        long startTime = System.currentTimeMillis();
        String apiName = apiEnum.getApiPath();

        try {
            log.info("[collectData][开始采集数据] api: {}, param: {}", apiName, param);

            // 获取对应策略
            @SuppressWarnings("unchecked")
            AkToolsDataStrategy<T> strategy = (AkToolsDataStrategy<T>) strategyFactory.getStrategy(apiEnum);

            // 构建请求参数
            Map<String, Object> requestParams = strategy.buildRequestParams(param);

            // 调用API获取数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawDataList = (List<Map<String, Object>>) 
                akToolsHttpClient.getForObject("/" + apiName, requestParams, List.class);

            if (CollUtil.isEmpty(rawDataList)) {
                log.warn("[collectData][获取到空数据] api: {}", apiName);
                metricsCollector.recordCollectSuccess(apiName, 0);
                return 0;
            }

            // 转换数据
            long convertStart = System.currentTimeMillis();
            List<T> dataList = convertRawData(strategy, rawDataList, param);
            metricsCollector.recordConvertDuration(apiName, System.currentTimeMillis() - convertStart);

            if (CollUtil.isEmpty(dataList)) {
                throw new AkToolsException("数据转换后为空");
            }

            // 保存数据
            int savedCount = stockDataBatchProcessor.batchSaveWithRetry(
                dataList, saveFunction, 3);

            // 记录成功指标
            metricsCollector.recordCollectSuccess(apiName, savedCount);
            metricsCollector.recordCollectDuration(apiName, System.currentTimeMillis() - startTime);

            log.info("[collectData][数据采集完成] api: {}, rawDataCount: {}, savedCount: {}", 
                apiName, rawDataList.size(), savedCount);

            return savedCount;

        } catch (Exception e) {
            metricsCollector.recordCollectFailure(apiName, e.getClass().getSimpleName(), e);
            log.error("[collectData][数据采集失败] api: {}, param: {}, error: {}", 
                apiName, param, e.getMessage(), e);
            throw new AkToolsException("数据采集失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换原始数据
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> convertRawData(AkToolsDataStrategy<T> strategy, 
                                     List<Map<String, Object>> rawDataList, 
                                     Object param) {
        AkToolsDataStrategy.ConversionContext context = createContext(param);
        return (List<T>) strategy.convertData(rawDataList, context);
    }

    /**
     * 创建转换上下文
     */
    private AkToolsDataStrategy.ConversionContext createContext(Object param) {
        if (param instanceof LocalDate) {
            return AkToolsDataStrategy.ConversionContext.forTradeDate((LocalDate) param);
        } else if (param instanceof String) {
            return AkToolsDataStrategy.ConversionContext.forMonth((String) param);
        }
        return new AkToolsDataStrategy.ConversionContext(null, null, param);
    }

    /**
     * 保存函数接口
     */
    @FunctionalInterface
    public interface SaveFunction<T> {
        int save(List<T> dataList) throws Exception;
    }

    // ========== Mapper获取方法 ==========

    @SuppressWarnings("unchecked")
    public <T> SaveFunction<T> getSaveFunction(AkApiEnum apiEnum) {
        switch (apiEnum) {
            case STOCK_SPOT:
                return (SaveFunction<T>) stockSpotMapper::insertOrUpdateBatch;
            case STOCK_VALUATION:
                return (SaveFunction<T>) stockValuationMapper::insertOrUpdateBatch;
            case STOCK_FUND_FLOW:
                return (SaveFunction<T>) stockFundFlowMapper::insertOrUpdateBatch;
            case NORTH_FUND:
                return (SaveFunction<T>) northFundMapper::insertOrUpdateBatch;
            case INDEX_SPOT:
                return (SaveFunction<T>) indexSpotMapper::insertOrUpdateBatch;
            case FINANCIAL_REPORT:
                return (SaveFunction<T>) financialReportMapper::insertOrUpdateBatch;
            case SHAREHOLDER_STATS:
                return (SaveFunction<T>) shareholderStatsMapper::insertOrUpdateBatch;
            case MARKET_ACTIVITY:
                return (SaveFunction<T>) marketActivityMapper::insertOrUpdateBatch;
            case MACRO_CPI:
                return (SaveFunction<T>) macroCpiMapper::insertOrUpdateBatch;
            case MACRO_MONEY_SUPPLY:
                return (SaveFunction<T>) macroMoneySupplyMapper::insertOrUpdateBatch;
            case TRADE_CALENDAR:
                return (SaveFunction<T>) tradeCalendarMapper::insertOrUpdateBatch;
            default:
                throw new IllegalArgumentException("不支持的API类型: " + apiEnum);
        }
    }

}