package cn.iocoder.yudao.module.collect.service.aktools.query;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;

import java.time.LocalDate;
import java.util.List;

/**
 * AkTools 数据查询服务接口
 * 负责所有数据的查询操作
 *
 * @author ToBy.Qoder
 */
public interface AkToolsQueryService {

    // ========== 股票实时行情查询 ==========

    /**
     * 根据股票代码和交易日期查询行情数据
     */
    AkToolsStockSpotDO getStockSpotData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票行情数据
     */
    List<AkToolsStockSpotDO> listStockSpotDataByDate(LocalDate tradeDate);

    /**
     * 根据股票代码查询历史行情数据
     */
    List<AkToolsStockSpotDO> listStockSpotDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate);

    // ========== 股票估值查询 ==========

    /**
     * 根据股票代码和交易日期查询估值数据
     */
    AkToolsStockValuationDO getStockValuationData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票估值数据
     */
    List<AkToolsStockValuationDO> listStockValuationDataByDate(LocalDate tradeDate);

    /**
     * 根据股票代码查询历史估值数据
     */
    List<AkToolsStockValuationDO> listStockValuationDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate);

    // ========== 资金流向查询 ==========

    /**
     * 根据股票代码和交易日期查询资金流向数据
     */
    AkToolsStockFundFlowDO getStockFundFlowData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票资金流向数据
     */
    List<AkToolsStockFundFlowDO> listStockFundFlowDataByDate(LocalDate tradeDate);

    /**
     * 根据股票代码查询历史资金流向数据
     */
    List<AkToolsStockFundFlowDO> listStockFundFlowDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate);

    // ========== 北向资金查询 ==========

    /**
     * 根据交易日期查询北向资金数据
     */
    AkToolsNorthFundDO getNorthFundData(LocalDate tradeDate);

    /**
     * 查询日期范围内的北向资金数据
     */
    List<AkToolsNorthFundDO> listNorthFundDataByRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询最新的N条北向资金数据
     */
    List<AkToolsNorthFundDO> listLatestNorthFundData(int limit);

    // ========== 指数行情查询 ==========

    /**
     * 根据指数代码和交易日期查询行情数据
     */
    AkToolsIndexSpotDO getIndexSpotData(String indexCode, LocalDate tradeDate);

    /**
     * 查询某日所有指数行情数据
     */
    List<AkToolsIndexSpotDO> listIndexSpotDataByDate(LocalDate tradeDate);

    /**
     * 根据指数代码查询历史行情数据
     */
    List<AkToolsIndexSpotDO> listIndexSpotDataByCode(String indexCode, LocalDate startDate, LocalDate endDate);

    // ========== 财务报表查询 ==========

    /**
     * 根据股票代码和报告年份查询财务报表数据
     */
    List<AkToolsFinancialReportDO> listFinancialReportData(String symbol, Integer reportYear);

    /**
     * 查询某年所有股票财务报表数据
     */
    List<AkToolsFinancialReportDO> listFinancialReportDataByYear(Integer reportYear);

    /**
     * 根据报告类型查询财务报表数据
     */
    List<AkToolsFinancialReportDO> listFinancialReportDataByType(Integer reportType, Integer reportYear);

    // ========== 股东户数查询 ==========

    /**
     * 根据股票代码和截止日期查询股东户数数据
     */
    AkToolsShareholderStatsDO getShareholderStatsData(String symbol, LocalDate endDate);

    /**
     * 根据股票代码查询历史股东户数数据
     */
    List<AkToolsShareholderStatsDO> listShareholderStatsDataBySymbol(String symbol);

    /**
     * 查询某日所有股票股东户数数据
     */
    List<AkToolsShareholderStatsDO> listShareholderStatsDataByDate(LocalDate endDate);

    // ========== 市场活跃度查询 ==========

    /**
     * 根据交易日期查询市场活跃度数据
     */
    AkToolsMarketActivityDO getMarketActivityData(LocalDate tradeDate);

    /**
     * 查询日期范围内的市场活跃度数据
     */
    List<AkToolsMarketActivityDO> listMarketActivityDataByRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询最新的市场活跃度数据
     */
    List<AkToolsMarketActivityDO> listLatestMarketActivityData(int limit);

    // ========== 宏观经济数据查询 ==========

    /**
     * 根据月份查询CPI数据
     */
    AkToolsMacroCpiDO getMacroCpiData(String month);

    /**
     * 查询指定时间段内的CPI数据
     */
    List<AkToolsMacroCpiDO> listMacroCpiDataByRange(String startMonth, String endMonth);

    /**
     * 根据月份查询货币供应量数据
     */
    AkToolsMacroMoneySupplyDO getMacroMoneySupplyData(String month);

    /**
     * 查询指定时间段内的货币供应量数据
     */
    List<AkToolsMacroMoneySupplyDO> listMacroMoneySupplyDataByRange(String startMonth, String endMonth);

    // ========== 交易日历查询 ==========

    /**
     * 根据交易日期查询交易日历数据
     */
    AkToolsTradeCalendarDO getTradeCalendarData(LocalDate tradeDate);

    /**
     * 查询日期范围内的交易日历数据
     */
    List<AkToolsTradeCalendarDO> listTradeCalendarDataByRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询某年的所有交易日
     */
    List<AkToolsTradeCalendarDO> listTradeCalendarDataByYear(Integer year);

    /**
     * 判断是否为交易日
     */
    boolean isTradingDay(LocalDate date);

    /**
     * 获取下一个交易日
     */
    LocalDate getNextTradingDay(LocalDate currentDate);

    /**
     * 获取上一个交易日
     */
    LocalDate getPreviousTradingDay(LocalDate currentDate);

}