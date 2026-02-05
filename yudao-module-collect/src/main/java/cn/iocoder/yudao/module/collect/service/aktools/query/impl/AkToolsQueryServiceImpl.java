package cn.iocoder.yudao.module.collect.service.aktools.query.impl;

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
import cn.iocoder.yudao.module.collect.service.aktools.query.AkToolsQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * AkTools 数据查询服务实现
 * 负责所有数据的查询操作实现
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AkToolsQueryServiceImpl implements AkToolsQueryService {

    // Mapper依赖注入
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

    // ========== 股票实时行情查询 ==========

    @Override
    public AkToolsStockSpotDO getStockSpotData(String symbol, LocalDate tradeDate) {
        validateSymbolAndDate(symbol, tradeDate);
        log.debug("[getStockSpotData][查询股票行情数据] symbol: {}, tradeDate: {}", symbol, tradeDate);
        return stockSpotMapper.selectBySymbolAndDate(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockSpotDO> listStockSpotDataByDate(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[listStockSpotDataByDate][查询日期行情数据] tradeDate: {}", tradeDate);
        return stockSpotMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsStockSpotDO> listStockSpotDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate) {
        validateSymbol(symbol);
        validateDateRange(startDate, endDate);
        log.debug("[listStockSpotDataBySymbol][查询股票历史行情] symbol: {}, dateRange: {} to {}", 
            symbol, startDate, endDate);
        return stockSpotMapper.selectBySymbolAndDateRange(symbol, startDate, endDate);
    }

    // ========== 股票估值查询 ==========

    @Override
    public AkToolsStockValuationDO getStockValuationData(String symbol, LocalDate tradeDate) {
        validateSymbolAndDate(symbol, tradeDate);
        log.debug("[getStockValuationData][查询股票估值数据] symbol: {}, tradeDate: {}", symbol, tradeDate);
        return stockValuationMapper.selectBySymbolAndDate(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockValuationDO> listStockValuationDataByDate(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[listStockValuationDataByDate][查询日期估值数据] tradeDate: {}", tradeDate);
        return stockValuationMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsStockValuationDO> listStockValuationDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate) {
        validateSymbol(symbol);
        validateDateRange(startDate, endDate);
        log.debug("[listStockValuationDataBySymbol][查询股票历史估值] symbol: {}, dateRange: {} to {}", 
            symbol, startDate, endDate);
        return stockValuationMapper.selectBySymbolAndDateRange(symbol, startDate, endDate);
    }

    // ========== 资金流向查询 ==========

    @Override
    public AkToolsStockFundFlowDO getStockFundFlowData(String symbol, LocalDate tradeDate) {
        validateSymbolAndDate(symbol, tradeDate);
        log.debug("[getStockFundFlowData][查询资金流向数据] symbol: {}, tradeDate: {}", symbol, tradeDate);
        return stockFundFlowMapper.selectBySymbolAndDate(symbol, tradeDate);
    }

    @Override
    public List<AkToolsStockFundFlowDO> listStockFundFlowDataByDate(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[listStockFundFlowDataByDate][查询日期资金流向] tradeDate: {}", tradeDate);
        return stockFundFlowMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsStockFundFlowDO> listStockFundFlowDataBySymbol(String symbol, LocalDate startDate, LocalDate endDate) {
        validateSymbol(symbol);
        validateDateRange(startDate, endDate);
        log.debug("[listStockFundFlowDataBySymbol][查询股票历史资金流向] symbol: {}, dateRange: {} to {}", 
            symbol, startDate, endDate);
        return stockFundFlowMapper.selectBySymbolAndDateRange(symbol, startDate, endDate);
    }

    // ========== 北向资金查询 ==========

    @Override
    public AkToolsNorthFundDO getNorthFundData(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[getNorthFundData][查询北向资金数据] tradeDate: {}", tradeDate);
        return northFundMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsNorthFundDO> listNorthFundDataByRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        log.debug("[listNorthFundDataByRange][查询北向资金范围数据] dateRange: {} to {}", startDate, endDate);
        return northFundMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public List<AkToolsNorthFundDO> listLatestNorthFundData(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("查询数量必须大于0");
        }
        log.debug("[listLatestNorthFundData][查询最新北向资金数据] limit: {}", limit);
        return northFundMapper.selectLatestRecords(limit);
    }

    // ========== 指数行情查询 ==========

    @Override
    public AkToolsIndexSpotDO getIndexSpotData(String indexCode, LocalDate tradeDate) {
        validateIndexCode(indexCode);
        validateTradeDate(tradeDate);
        log.debug("[getIndexSpotData][查询指数行情数据] indexCode: {}, tradeDate: {}", indexCode, tradeDate);
        return indexSpotMapper.selectByIndexCodeAndDate(indexCode, tradeDate);
    }

    @Override
    public List<AkToolsIndexSpotDO> listIndexSpotDataByDate(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[listIndexSpotDataByDate][查询日期指数行情] tradeDate: {}", tradeDate);
        return indexSpotMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsIndexSpotDO> listIndexSpotDataByCode(String indexCode, LocalDate startDate, LocalDate endDate) {
        validateIndexCode(indexCode);
        validateDateRange(startDate, endDate);
        log.debug("[listIndexSpotDataByCode][查询指数历史行情] indexCode: {}, dateRange: {} to {}", 
            indexCode, startDate, endDate);
        return indexSpotMapper.selectByIndexCodeAndDateRange(indexCode, startDate, endDate);
    }

    // ========== 财务报表查询 ==========

    @Override
    public List<AkToolsFinancialReportDO> listFinancialReportData(String symbol, Integer reportYear) {
        validateSymbol(symbol);
        validateReportYear(reportYear);
        log.debug("[listFinancialReportData][查询财务报表数据] symbol: {}, reportYear: {}", symbol, reportYear);
        return financialReportMapper.selectBySymbolAndYear(symbol, reportYear);
    }

    @Override
    public List<AkToolsFinancialReportDO> listFinancialReportDataByYear(Integer reportYear) {
        validateReportYear(reportYear);
        log.debug("[listFinancialReportDataByYear][查询年度财务报表] reportYear: {}", reportYear);
        return financialReportMapper.selectByReportYear(reportYear);
    }

    @Override
    public List<AkToolsFinancialReportDO> listFinancialReportDataByType(Integer reportType, Integer reportYear) {
        validateReportType(reportType);
        validateReportYear(reportYear);
        log.debug("[listFinancialReportDataByType][按类型查询财务报表] reportType: {}, reportYear: {}", 
            reportType, reportYear);
        return financialReportMapper.selectByReportTypeAndYear(reportType, reportYear);
    }

    // ========== 股东户数查询 ==========

    @Override
    public AkToolsShareholderStatsDO getShareholderStatsData(String symbol, LocalDate endDate) {
        validateSymbol(symbol);
        log.debug("[getShareholderStatsData][查询股东户数数据] symbol: {}, endDate: {}", symbol, endDate);
        return shareholderStatsMapper.selectBySymbolAndDate(symbol, endDate);
    }

    @Override
    public List<AkToolsShareholderStatsDO> listShareholderStatsDataBySymbol(String symbol) {
        validateSymbol(symbol);
        log.debug("[listShareholderStatsDataBySymbol][查询股票股东户数历史] symbol: {}", symbol);
        return shareholderStatsMapper.selectBySymbol(symbol);
    }

    @Override
    public List<AkToolsShareholderStatsDO> listShareholderStatsDataByDate(LocalDate endDate) {
        log.debug("[listShareholderStatsDataByDate][查询日期股东户数] endDate: {}", endDate);
        return shareholderStatsMapper.selectByEndDate(endDate);
    }

    // ========== 市场活跃度查询 ==========

    @Override
    public AkToolsMarketActivityDO getMarketActivityData(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[getMarketActivityData][查询市场活跃度数据] tradeDate: {}", tradeDate);
        return marketActivityMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsMarketActivityDO> listMarketActivityDataByRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        log.debug("[listMarketActivityDataByRange][查询市场活跃度范围数据] dateRange: {} to {}", 
            startDate, endDate);
        return marketActivityMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public List<AkToolsMarketActivityDO> listLatestMarketActivityData(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("查询数量必须大于0");
        }
        log.debug("[listLatestMarketActivityData][查询最新市场活跃度] limit: {}", limit);
        return marketActivityMapper.selectLatestRecords(limit);
    }

    // ========== 宏观经济数据查询 ==========

    @Override
    public AkToolsMacroCpiDO getMacroCpiData(String month) {
        validateMonth(month);
        log.debug("[getMacroCpiData][查询CPI数据] month: {}", month);
        return macroCpiMapper.selectByPublishMonth(month);
    }

    @Override
    public List<AkToolsMacroCpiDO> listMacroCpiDataByRange(String startMonth, String endMonth) {
        validateMonth(startMonth);
        validateMonth(endMonth);
        log.debug("[listMacroCpiDataByRange][查询CPI范围数据] monthRange: {} to {}", startMonth, endMonth);
        return macroCpiMapper.selectByMonthRange(startMonth, endMonth);
    }

    @Override
    public AkToolsMacroMoneySupplyDO getMacroMoneySupplyData(String month) {
        validateMonth(month);
        log.debug("[getMacroMoneySupplyData][查询货币供应量数据] month: {}", month);
        return macroMoneySupplyMapper.selectByStatMonth(month);
    }

    @Override
    public List<AkToolsMacroMoneySupplyDO> listMacroMoneySupplyDataByRange(String startMonth, String endMonth) {
        validateMonth(startMonth);
        validateMonth(endMonth);
        log.debug("[listMacroMoneySupplyDataByRange][查询货币供应量范围数据] monthRange: {} to {}", 
            startMonth, endMonth);
        return macroMoneySupplyMapper.selectByMonthRange(startMonth, endMonth);
    }

    // ========== 交易日历查询 ==========

    @Override
    public AkToolsTradeCalendarDO getTradeCalendarData(LocalDate tradeDate) {
        validateTradeDate(tradeDate);
        log.debug("[getTradeCalendarData][查询交易日历数据] tradeDate: {}", tradeDate);
        return tradeCalendarMapper.selectByTradeDate(tradeDate);
    }

    @Override
    public List<AkToolsTradeCalendarDO> listTradeCalendarDataByRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        log.debug("[listTradeCalendarDataByRange][查询交易日历范围数据] dateRange: {} to {}", 
            startDate, endDate);
        return tradeCalendarMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public List<AkToolsTradeCalendarDO> listTradeCalendarDataByYear(Integer year) {
        if (year == null || year < 1900 || year > 2100) {
            throw new IllegalArgumentException("年份参数无效: " + year);
        }
        log.debug("[listTradeCalendarDataByYear][查询年份交易日历] year: {}", year);
        return tradeCalendarMapper.selectByYear(year);
    }

    @Override
    public boolean isTradingDay(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        log.debug("[isTradingDay][判断是否为交易日] date: {}", date);
        return tradeCalendarMapper.isTradingDay(date);
    }

    @Override
    public LocalDate getNextTradingDay(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        log.debug("[getNextTradingDay][获取下一个交易日] currentDate: {}", currentDate);
        return tradeCalendarMapper.selectNextTradingDay(currentDate);
    }

    @Override
    public LocalDate getPreviousTradingDay(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        log.debug("[getPreviousTradingDay][获取上一个交易日] currentDate: {}", currentDate);
        return tradeCalendarMapper.selectPreviousTradingDay(currentDate);
    }

    // ========== 私有验证方法 ==========

    private void validateSymbolAndDate(String symbol, LocalDate tradeDate) {
        validateSymbol(symbol);
        validateTradeDate(tradeDate);
    }

    private void validateSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
    }

    private void validateIndexCode(String indexCode) {
        if (indexCode == null || indexCode.trim().isEmpty()) {
            throw new IllegalArgumentException("指数代码不能为空");
        }
    }

    private void validateTradeDate(LocalDate tradeDate) {
        if (tradeDate == null) {
            throw new IllegalArgumentException("交易日期不能为空");
        }
        if (tradeDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("交易日期不能晚于当前日期");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        validateTradeDate(startDate);
        validateTradeDate(endDate);
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
    }

    private void validateMonth(String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("月份格式错误，应为yyyy-MM格式: " + month);
        }
    }

    private void validateReportYear(Integer reportYear) {
        if (reportYear == null || reportYear < 1900 || reportYear > 2100) {
            throw new IllegalArgumentException("报告年份参数无效: " + reportYear);
        }
    }

    private void validateReportType(Integer reportType) {
        if (reportType == null || reportType < 1 || reportType > 4) {
            throw new IllegalArgumentException("报告类型参数无效: " + reportType);
        }
    }

}