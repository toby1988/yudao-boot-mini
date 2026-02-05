package cn.iocoder.yudao.module.collect.framework.aktools.enums;

/**
 * AkTools API 接口枚举
 * 定义所有 AkTools 接口的标识和描述
 *
 * @author ToBy.Qoder
 */
public enum AkApiEnum {

    /**
     * 股票实时行情接口
     * 抓取时间: 15:15 (收盘后)
     * 核心字段: 最新价、成交量、成交额、换手率
     */
    STOCK_SPOT("stock_zh_a_spot_em", "实时行情"),

    /**
     * 个股估值接口 (乐咕乐股)
     * 抓取时间: 18:00 (处理后)
     * 核心字段: trade_date, pe, pe_ttm, pb, dv_ratio (股息率)
     */
    STOCK_VALUATION("stock_a_lg_indicator", "个股估值"),

    /**
     * 股票资金流向接口
     * 抓取时间: 16:30
     * 核心字段: 主力流入、超大单流入、净流入额
     */
    STOCK_FUND_FLOW("stock_individual_fund_flow", "资金流向"),

    /**
     * 北向资金接口
     * 抓取时间: 17:00
     * 核心字段: 当日净买入额、累计持仓市值
     */
    NORTH_FUND("stock_hsgt_north_acc_flow_em", "北向资金"),

    /**
     * 指数行情接口
     * 抓取时间: 15:30
     * 核心字段: 沪深300、上证50等指数的日表现
     */
    INDEX_SPOT("stock_zh_index_spot_em", "指数行情"),

    /**
     * 财务报表接口 (三大表)
     * 核心字段: 截止日期, 净利润, 营业收入, 总资产, 负债合计
     */
    FINANCIAL_REPORT("stock_financial_report_sina", "财务报表"),

    /**
     * 股东户数接口
     * 核心字段: 股东户数, 户均持股数, 截止日期
     */
    SHAREHOLDER_STATS("stock_zh_a_gdhs", "股东户数"),

    /**
     * 市场活跃度接口 (腾落指数)
     * 核心字段: 日期, 涨停家数, 跌停家数, 炸板率
     */
    MARKET_ACTIVITY("stock_zh_a_tr_matrix_report", "市场活跃度"),

    /**
     * 中国CPI接口 (消费者物价指数)
     * 核心字段: 月份, 全国同比, 全国环比
     * 用途: 通胀分析, 市场择时
     */
    MACRO_CPI("macro_china_cpi", "中国CPI"),

    /**
     * 货币供应量接口 (M0/M1/M2)
     * 核心字段: 月份, M2数值, M2同比, M1同比
     * 用途: 流动性分析, 市场择时
     */
    MACRO_MONEY_SUPPLY("macro_china_money_supply", "货币供应量"),

    /**
     * 交易日历接口 (历史)
     * 核心字段: trade_date
     * 用途: 过滤非交易日抓取任务
     */
    TRADE_CALENDAR("tool_trade_date_hist_sina", "交易日历");

    /**
     * 接口路径
     */
    private final String apiPath;

    /**
     * 接口描述
     */
    private final String description;

    /**
     * 构造函数
     */
    AkApiEnum(String apiPath, String description) {
        this.apiPath = apiPath;
        this.description = description;
    }

    /**
     * 获取接口路径
     */
    public String getApiPath() {
        return apiPath;
    }

    /**
     * 获取接口描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据接口路径获取枚举值
     *
     * @param apiPath 接口路径
     * @return 对应的枚举值，未找到返回 null
     */
    public static AkApiEnum getByApiPath(String apiPath) {
        for (AkApiEnum api : values()) {
            if (api.getApiPath().equals(apiPath)) {
                return api;
            }
        }
        return null;
    }

    /**
     * 判断接口路径是否存在
     *
     * @param apiPath 接口路径
     * @return 是否存在
     */
    public static boolean exists(String apiPath) {
        return getByApiPath(apiPath) != null;
    }

}