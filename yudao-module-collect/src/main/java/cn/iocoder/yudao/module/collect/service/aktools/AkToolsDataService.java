package cn.iocoder.yudao.module.collect.service.aktools;

import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;

import java.time.LocalDate;
import java.util.List;

/**
 * AkTools 股票数据服务接口
 *
 * @author ToBy.Qoder
 */
public interface AkToolsDataService {

    // ========== 股票实时行情服务 ==========

    /**
     * 抓取并保存股票实时行情数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectStockSpotData(LocalDate tradeDate);

    /**
     * 查询股票实时行情数据
     *
     * @param symbol 股票代码
     * @param tradeDate 交易日期
     * @return 股票行情数据
     */
    AkToolsStockSpotDO getStockSpotData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票行情数据
     *
     * @param tradeDate 交易日期
     * @return 股票行情数据列表
     */
    List<AkToolsStockSpotDO> listStockSpotDataByDate(LocalDate tradeDate);

    // ========== 股票估值服务 ==========

    /**
     * 抓取并保存股票估值数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectStockValuationData(LocalDate tradeDate);

    /**
     * 查询股票估值数据
     *
     * @param symbol 股票代码
     * @param tradeDate 交易日期
     * @return 股票估值数据
     */
    AkToolsStockValuationDO getStockValuationData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票估值数据
     *
     * @param tradeDate 交易日期
     * @return 股票估值数据列表
     */
    List<AkToolsStockValuationDO> listStockValuationDataByDate(LocalDate tradeDate);

    // ========== 资金流向服务 ==========

    /**
     * 抓取并保存股票资金流向数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectStockFundFlowData(LocalDate tradeDate);

    /**
     * 查询股票资金流向数据
     *
     * @param symbol 股票代码
     * @param tradeDate 交易日期
     * @return 资金流向数据
     */
    AkToolsStockFundFlowDO getStockFundFlowData(String symbol, LocalDate tradeDate);

    /**
     * 查询某日所有股票资金流向数据
     *
     * @param tradeDate 交易日期
     * @return 资金流向数据列表
     */
    List<AkToolsStockFundFlowDO> listStockFundFlowDataByDate(LocalDate tradeDate);

    // ========== 北向资金服务 ==========

    /**
     * 抓取并保存北向资金数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectNorthFundData(LocalDate tradeDate);

    /**
     * 查询北向资金数据
     *
     * @param tradeDate 交易日期
     * @return 北向资金数据
     */
    AkToolsNorthFundDO getNorthFundData(LocalDate tradeDate);

    /**
     * 查询日期范围内的北向资金数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 北向资金数据列表
     */
    List<AkToolsNorthFundDO> listNorthFundDataByRange(LocalDate startDate, LocalDate endDate);

    // ========== 指数行情服务 ==========

    /**
     * 抓取并保存指数行情数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectIndexSpotData(LocalDate tradeDate);

    /**
     * 查询指数行情数据
     *
     * @param indexCode 指数代码
     * @param tradeDate 交易日期
     * @return 指数行情数据
     */
    AkToolsIndexSpotDO getIndexSpotData(String indexCode, LocalDate tradeDate);

    /**
     * 查询某日所有指数行情数据
     *
     * @param tradeDate 交易日期
     * @return 指数行情数据列表
     */
    List<AkToolsIndexSpotDO> listIndexSpotDataByDate(LocalDate tradeDate);

    // ========== 财务报表服务 ==========

    /**
     * 抓取并保存财务报表数据
     *
     * @param reportYear 报告年份
     * @return 抓取的记录数
     */
    int collectFinancialReportData(Integer reportYear);

    /**
     * 查询股票财务报表数据
     *
     * @param symbol 股票代码
     * @param reportYear 报告年份
     * @return 财务报表数据列表
     */
    List<AkToolsFinancialReportDO> listFinancialReportData(String symbol, Integer reportYear);

    /**
     * 查询某年所有股票财务报表数据
     *
     * @param reportYear 报告年份
     * @return 财务报表数据列表
     */
    List<AkToolsFinancialReportDO> listFinancialReportDataByYear(Integer reportYear);

    // ========== 股东户数服务 ==========

    /**
     * 抓取并保存股东户数数据
     *
     * @param endDate 截止日期
     * @return 抓取的记录数
     */
    int collectShareholderStatsData(LocalDate endDate);

    /**
     * 查询股票股东户数数据
     *
     * @param symbol 股票代码
     * @param endDate 截止日期
     * @return 股东户数数据
     */
    AkToolsShareholderStatsDO getShareholderStatsData(String symbol, LocalDate endDate);

    /**
     * 查询股票历史股东户数数据
     *
     * @param symbol 股票代码
     * @return 股东户数数据列表
     */
    List<AkToolsShareholderStatsDO> listShareholderStatsDataBySymbol(String symbol);

    /**
     * 查询某日所有股票股东户数数据
     *
     * @param endDate 截止日期
     * @return 股东户数数据列表
     */
    List<AkToolsShareholderStatsDO> listShareholderStatsDataByDate(LocalDate endDate);

    // ========== 市场活跃度服务 ==========

    /**
     * 抓取并保存市场活跃度数据
     *
     * @param tradeDate 交易日期
     * @return 抓取的记录数
     */
    int collectMarketActivityData(LocalDate tradeDate);

    /**
     * 查询市场活跃度数据
     *
     * @param tradeDate 交易日期
     * @return 市场活跃度数据
     */
    AkToolsMarketActivityDO getMarketActivityData(LocalDate tradeDate);

    /**
     * 查询日期范围内的市场活跃度数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 市场活跃度数据列表
     */
    List<AkToolsMarketActivityDO> listMarketActivityDataByRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询最新的市场活跃度数据
     *
     * @param limit 数量限制
     * @return 市场活跃度数据列表
     */
    List<AkToolsMarketActivityDO> listLatestMarketActivityData(int limit);

    // ========== 宏观经济数据服务 ==========

    /**
     * 抓取并保存中国CPI数据
     *
     * @param month 月份 (yyyy-MM格式)
     * @return 抓取的记录数
     */
    int collectMacroCpiData(String month);

    /**
     * 查询CPI数据
     *
     * @param month 月份 (yyyy-MM格式)
     * @return CPI数据
     */
    AkToolsMacroCpiDO getMacroCpiData(String month);

    /**
     * 查询指定时间段内的CPI数据
     *
     * @param startMonth 开始月份 (yyyy-MM格式)
     * @param endMonth 结束月份 (yyyy-MM格式)
     * @return CPI数据列表
     */
    List<AkToolsMacroCpiDO> listMacroCpiDataByRange(String startMonth, String endMonth);

    /**
     * 抓取并保存货币供应量数据
     *
     * @param month 月份 (yyyy-MM格式)
     * @return 抓取的记录数
     */
    int collectMacroMoneySupplyData(String month);

    /**
     * 查询货币供应量数据
     *
     * @param month 月份 (yyyy-MM格式)
     * @return 货币供应量数据
     */
    AkToolsMacroMoneySupplyDO getMacroMoneySupplyData(String month);

    /**
     * 查询指定时间段内的货币供应量数据
     *
     * @param startMonth 开始月份 (yyyy-MM格式)
     * @param endMonth 结束月份 (yyyy-MM格式)
     * @return 货币供应量数据列表
     */
    List<AkToolsMacroMoneySupplyDO> listMacroMoneySupplyDataByRange(String startMonth, String endMonth);

    // ========== 交易日历服务 ==========

    /**
     * 抓取并保存交易日历数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 抓取的记录数
     */
    int collectTradeCalendarData(LocalDate startDate, LocalDate endDate);

    /**
     * 查询交易日历数据
     *
     * @param tradeDate 交易日期
     * @return 交易日历数据
     */
    AkToolsTradeCalendarDO getTradeCalendarData(LocalDate tradeDate);

    /**
     * 查询日期范围内的交易日历数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易日历数据列表
     */
    List<AkToolsTradeCalendarDO> listTradeCalendarDataByRange(LocalDate startDate, LocalDate endDate);

    /**
     * 判断是否为交易日
     *
     * @param date 日期
     * @return true: 是交易日, false: 非交易日
     */
    boolean isTradingDay(LocalDate date);

    /**
     * 获取下一个交易日
     *
     * @param currentDate 当前日期
     * @return 下一个交易日
     */
    LocalDate getNextTradingDay(LocalDate currentDate);

    /**
     * 获取上一个交易日
     *
     * @param currentDate 当前日期
     * @return 上一个交易日
     */
    LocalDate getPreviousTradingDay(LocalDate currentDate);

    // ========== 批量采集服务 ==========

    /**
     * 执行每日数据采集任务
     * 按照最优时间顺序执行所有数据采集
     *
     * @param tradeDate 交易日期
     * @return 各接口采集记录数统计
     */
    AkToolsCollectResult collectAllDataDaily(LocalDate tradeDate);

}
