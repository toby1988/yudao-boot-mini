package cn.iocoder.yudao.module.collect.service.aktools;

import lombok.Data;

/**
 * AkTools 数据采集结果
 *
 * @author ToBy.Qoder
 */
@Data
public class AkToolsCollectResult {

    /**
     * 股票实时行情采集记录数
     */
    private int stockSpotCount;

    /**
     * 股票估值数据采集记录数
     */
    private int stockValuationCount;

    /**
     * 资金流向数据采集记录数
     */
    private int fundFlowCount;

    /**
     * 北向资金数据采集记录数
     */
    private int northFundCount;

    /**
     * 指数行情数据采集记录数
     */
    private int indexSpotCount;

    /**
     * 财务报表数据采集记录数
     */
    private int financialReportCount;

    /**
     * 股东户数数据采集记录数
     */
    private int shareholderStatsCount;

    /**
     * 市场活跃度数据采集记录数
     */
    private int marketActivityCount;

    /**
     * 宏观经济CPI数据采集记录数
     */
    private int macroCpiCount;

    /**
     * 货币供应量数据采集记录数
     */
    private int macroMoneySupplyCount;

    /**
     * 交易日历数据采集记录数
     */
    private int tradeCalendarCount;

    /**
     * 总采集记录数
     */
    private int totalCount;

    /**
     * 是否全部成功
     */
    private boolean allSuccess;

    /**
     * 错误信息
     */
    private String errorMessage;

    public AkToolsCollectResult() {
        this.allSuccess = true;
    }

    /**
     * 计算总记录数
     */
    public void calculateTotalCount() {
        this.totalCount = stockSpotCount + stockValuationCount + 
                         fundFlowCount + northFundCount + indexSpotCount +
                         financialReportCount + shareholderStatsCount + marketActivityCount +
                         macroCpiCount + macroMoneySupplyCount + tradeCalendarCount;
    }
    
    // 添加 getter 方法以解决编译问题
    public boolean isAllSuccess() {
        return allSuccess;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public int getStockSpotCount() {
        return stockSpotCount;
    }
    
    public int getStockValuationCount() {
        return stockValuationCount;
    }
    
    public int getFundFlowCount() {
        return fundFlowCount;
    }
    
    public int getNorthFundCount() {
        return northFundCount;
    }
    
    public int getIndexSpotCount() {
        return indexSpotCount;
    }
    
    public int getFinancialReportCount() {
        return financialReportCount;
    }
    
    public int getShareholderStatsCount() {
        return shareholderStatsCount;
    }
    
    public int getMarketActivityCount() {
        return marketActivityCount;
    }
    
    public int getMacroCpiCount() {
        return macroCpiCount;
    }
    
    public int getMacroMoneySupplyCount() {
        return macroMoneySupplyCount;
    }
    
    public int getTradeCalendarCount() {
        return tradeCalendarCount;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    // Setter 方法
    public void setStockSpotCount(int stockSpotCount) {
        this.stockSpotCount = stockSpotCount;
    }
    
    public void setStockValuationCount(int stockValuationCount) {
        this.stockValuationCount = stockValuationCount;
    }
    
    public void setFundFlowCount(int fundFlowCount) {
        this.fundFlowCount = fundFlowCount;
    }
    
    public void setNorthFundCount(int northFundCount) {
        this.northFundCount = northFundCount;
    }
    
    public void setIndexSpotCount(int indexSpotCount) {
        this.indexSpotCount = indexSpotCount;
    }
    
    public void setFinancialReportCount(int financialReportCount) {
        this.financialReportCount = financialReportCount;
    }
    
    public void setShareholderStatsCount(int shareholderStatsCount) {
        this.shareholderStatsCount = shareholderStatsCount;
    }
    
    public void setMarketActivityCount(int marketActivityCount) {
        this.marketActivityCount = marketActivityCount;
    }
    
    public void setMacroCpiCount(int macroCpiCount) {
        this.macroCpiCount = macroCpiCount;
    }
    
    public void setMacroMoneySupplyCount(int macroMoneySupplyCount) {
        this.macroMoneySupplyCount = macroMoneySupplyCount;
    }
    
    public void setTradeCalendarCount(int tradeCalendarCount) {
        this.tradeCalendarCount = tradeCalendarCount;
    }
    
    public void setAllSuccess(boolean allSuccess) {
        this.allSuccess = allSuccess;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
