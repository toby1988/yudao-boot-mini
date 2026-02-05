package cn.iocoder.yudao.module.collect.framework.aktools.enums;

/**
 * 财务报告类型枚举
 *
 * @author ToBy.Qoder
 */
public enum FinancialReportTypeEnum {

    /**
     * 年报
     */
    ANNUAL_REPORT(1, "年报"),

    /**
     * 中报
     */
    SEMI_ANNUAL_REPORT(2, "中报"),

    /**
     * 一季报
     */
    FIRST_QUARTER_REPORT(3, "一季报"),

    /**
     * 三季报
     */
    THIRD_QUARTER_REPORT(4, "三季报");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 构造函数
     */
    FinancialReportTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取类型编码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值
     *
     * @param code 类型编码
     * @return 对应的枚举值，未找到返回 null
     */
    public static FinancialReportTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FinancialReportTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断编码是否存在
     *
     * @param code 类型编码
     * @return 是否存在
     */
    public static boolean exists(Integer code) {
        return getByCode(code) != null;
    }

    /**
     * 获取所有类型编码
     *
     * @return 类型编码数组
     */
    public static Integer[] getAllCodes() {
        return new Integer[]{1, 2, 3, 4};
    }

}