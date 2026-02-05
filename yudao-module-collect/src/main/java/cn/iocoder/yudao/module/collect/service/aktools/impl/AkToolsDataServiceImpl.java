package cn.iocoder.yudao.module.collect.service.aktools.impl;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsCollectResult;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsDataService;
import cn.iocoder.yudao.module.collect.service.aktools.core.AkToolsDataCoordinator;
import cn.iocoder.yudao.module.collect.service.aktools.query.AkToolsQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * AkTools 数据服务策略模式实现
 * 基于策略+工厂模式的全新实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AkToolsDataServiceImpl implements AkToolsDataService {

    private final AkToolsDataCoordinator coordinator;
    private final AkToolsQueryService queryService;

    // ========== 股票实时行情服务 ==========

    @Override
    public int collectStockSpotData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.STOCK_SPOT, 
            tradeDate, 
            coordinator.getSaveFunction(AkApiEnum.STOCK_SPOT)
        );
    }

    @Override
    public AkToolsStockSpotDO getStockSpotData(String symbol, LocalDate tradeDate) {
        return queryService.getStockSpotData(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockSpotDO> listStockSpotDataByDate(LocalDate tradeDate) {
        return queryService.listStockSpotDataByDate(tradeDate);
    }

    // ========== 股票估值服务 ==========

    @Override
    public int collectStockValuationData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.STOCK_VALUATION,
            tradeDate,
            coordinator.getSaveFunction(AkApiEnum.STOCK_VALUATION)
        );
    }

    @Override
    public AkToolsStockValuationDO getStockValuationData(String symbol, LocalDate tradeDate) {
        return queryService.getStockValuationData(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockValuationDO> listStockValuationDataByDate(LocalDate tradeDate) {
        return queryService.listStockValuationDataByDate(tradeDate);
    }

    // ========== 资金流向服务 ==========

    @Override
    public int collectStockFundFlowData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.STOCK_FUND_FLOW,
            tradeDate,
            coordinator.getSaveFunction(AkApiEnum.STOCK_FUND_FLOW)
        );
    }

    @Override
    public AkToolsStockFundFlowDO getStockFundFlowData(String symbol, LocalDate tradeDate) {
        return queryService.getStockFundFlowData(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockFundFlowDO> listStockFundFlowDataByDate(LocalDate tradeDate) {
        return queryService.listStockFundFlowDataByDate(tradeDate);
    }

    // ========== 北向资金服务 ==========

    @Override
    public int collectNorthFundData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.NORTH_FUND,
            tradeDate,
            coordinator.getSaveFunction(AkApiEnum.NORTH_FUND)
        );
    }

    @Override
    public AkToolsNorthFundDO getNorthFundData(LocalDate tradeDate) {
        return queryService.getNorthFundData(tradeDate);
    }

    @Override
    public List<AkToolsNorthFundDO> listNorthFundDataByRange(LocalDate startDate, LocalDate endDate) {
        return queryService.listNorthFundDataByRange(startDate, endDate);
    }

    // ========== 指数行情服务 ==========

    @Override
    public int collectIndexSpotData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.INDEX_SPOT,
            tradeDate,
            coordinator.getSaveFunction(AkApiEnum.INDEX_SPOT)
        );
    }

    @Override
    public AkToolsIndexSpotDO getIndexSpotData(String indexCode, LocalDate tradeDate) {
        return queryService.getIndexSpotData(indexCode, tradeDate);
    }

    @Override
    public List<AkToolsIndexSpotDO> listIndexSpotDataByDate(LocalDate tradeDate) {
        return queryService.listIndexSpotDataByDate(tradeDate);
    }

    // ========== 财务报表服务 ==========

    @Override
    public int collectFinancialReportData(Integer reportYear) {
        return coordinator.collectData(
            AkApiEnum.FINANCIAL_REPORT,
            reportYear,
            coordinator.getSaveFunction(AkApiEnum.FINANCIAL_REPORT)
        );
    }

    @Override
    public List<AkToolsFinancialReportDO> listFinancialReportData(String symbol, Integer reportYear) {
        return queryService.listFinancialReportData(symbol, reportYear);
    }

    @Override
    public List<AkToolsFinancialReportDO> listFinancialReportDataByYear(Integer reportYear) {
        return queryService.listFinancialReportDataByYear(reportYear);
    }

    // ========== 股东户数服务 ==========

    @Override
    public int collectShareholderStatsData(LocalDate endDate) {
        return coordinator.collectData(
            AkApiEnum.SHAREHOLDER_STATS,
            endDate,
            coordinator.getSaveFunction(AkApiEnum.SHAREHOLDER_STATS)
        );
    }

    @Override
    public AkToolsShareholderStatsDO getShareholderStatsData(String symbol, LocalDate endDate) {
        return queryService.getShareholderStatsData(symbol, endDate);
    }

    @Override
    public List<AkToolsShareholderStatsDO> listShareholderStatsDataBySymbol(String symbol) {
        return queryService.listShareholderStatsDataBySymbol(symbol);
    }

    @Override
    public List<AkToolsShareholderStatsDO> listShareholderStatsDataByDate(LocalDate endDate) {
        return queryService.listShareholderStatsDataByDate(endDate);
    }

    // ========== 市场活跃度服务 ==========

    @Override
    public int collectMarketActivityData(LocalDate tradeDate) {
        return coordinator.collectData(
            AkApiEnum.MARKET_ACTIVITY,
            tradeDate,
            coordinator.getSaveFunction(AkApiEnum.MARKET_ACTIVITY)
        );
    }

    @Override
    public AkToolsMarketActivityDO getMarketActivityData(LocalDate tradeDate) {
        return queryService.getMarketActivityData(tradeDate);
    }

    @Override
    public List<AkToolsMarketActivityDO> listMarketActivityDataByRange(LocalDate startDate, LocalDate endDate) {
        return queryService.listMarketActivityDataByRange(startDate, endDate);
    }

    @Override
    public List<AkToolsMarketActivityDO> listLatestMarketActivityData(int limit) {
        return queryService.listLatestMarketActivityData(limit);
    }

    // ========== 宏观经济数据服务 ==========

    @Override
    public int collectMacroCpiData(String month) {
        return coordinator.collectData(
            AkApiEnum.MACRO_CPI,
            month,
            coordinator.getSaveFunction(AkApiEnum.MACRO_CPI)
        );
    }

    @Override
    public AkToolsMacroCpiDO getMacroCpiData(String month) {
        return queryService.getMacroCpiData(month);
    }

    @Override
    public List<AkToolsMacroCpiDO> listMacroCpiDataByRange(String startMonth, String endMonth) {
        return queryService.listMacroCpiDataByRange(startMonth, endMonth);
    }

    @Override
    public int collectMacroMoneySupplyData(String month) {
        return coordinator.collectData(
            AkApiEnum.MACRO_MONEY_SUPPLY,
            month,
            coordinator.getSaveFunction(AkApiEnum.MACRO_MONEY_SUPPLY)
        );
    }

    @Override
    public AkToolsMacroMoneySupplyDO getMacroMoneySupplyData(String month) {
        return queryService.getMacroMoneySupplyData(month);
    }

    @Override
    public List<AkToolsMacroMoneySupplyDO> listMacroMoneySupplyDataByRange(String startMonth, String endMonth) {
        return queryService.listMacroMoneySupplyDataByRange(startMonth, endMonth);
    }

    // ========== 交易日历服务 ==========

    @Override
    public int collectTradeCalendarData(LocalDate startDate, LocalDate endDate) {
        // 交易日历数据通常是一次性初始化或定期更新
        // 这里可以根据需要实现具体的采集逻辑
        log.info("[collectTradeCalendarData][交易日历数据采集] startDate: {}, endDate: {}", startDate, endDate);
        return 0; // 暂时返回0，实际使用时需要实现具体逻辑
    }

    @Override
    public AkToolsTradeCalendarDO getTradeCalendarData(LocalDate tradeDate) {
        return queryService.getTradeCalendarData(tradeDate);
    }

    @Override
    public List<AkToolsTradeCalendarDO> listTradeCalendarDataByRange(LocalDate startDate, LocalDate endDate) {
        return queryService.listTradeCalendarDataByRange(startDate, endDate);
    }

    @Override
    public boolean isTradingDay(LocalDate date) {
        return queryService.isTradingDay(date);
    }

    @Override
    public LocalDate getNextTradingDay(LocalDate currentDate) {
        return queryService.getNextTradingDay(currentDate);
    }

    @Override
    public LocalDate getPreviousTradingDay(LocalDate currentDate) {
        return queryService.getPreviousTradingDay(currentDate);
    }

    // ========== 批量采集服务 ==========

    @Override
    public AkToolsCollectResult collectAllDataDaily(LocalDate tradeDate) {
        log.info("[collectAllDataDaily][开始执行每日数据采集任务] tradeDate: {}", tradeDate);
        
        AkToolsCollectResult result = new AkToolsCollectResult();
        
        try {
            // 按照最优时间顺序执行采集
            result.setStockSpotCount(collectStockSpotData(tradeDate));
            result.setIndexSpotCount(collectIndexSpotData(tradeDate));
            result.setFundFlowCount(collectStockFundFlowData(tradeDate));
            result.setNorthFundCount(collectNorthFundData(tradeDate));
            result.setStockValuationCount(collectStockValuationData(tradeDate));
            
            result.calculateTotalCount();
            result.setAllSuccess(true);
            
            log.info("[collectAllDataDaily][每日数据采集任务完成] tradeDate: {}, result: {}", 
                tradeDate, result);
            
            return result;
        } catch (Exception e) {
            log.error("[collectAllDataDaily][每日数据采集任务失败] tradeDate: {}, error: {}", 
                tradeDate, e.getMessage(), e);
            
            result.setAllSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

}