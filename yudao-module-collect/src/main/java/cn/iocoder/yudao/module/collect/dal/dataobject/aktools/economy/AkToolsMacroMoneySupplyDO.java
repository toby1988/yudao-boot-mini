package cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 货币供应量数据实体类
 * 对应接口: macro_china_money_supply
 * 获取M0/M1/M2货币供应量数据，用于流动性分析
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_macro_money_supply")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsMacroMoneySupplyDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 统计月份
     * 格式: yyyy-MM
     */
    private String statMonth;

    /**
     * M2数值 (亿元)
     */
    private BigDecimal m2Value;

    /**
     * M2同比 (%)
     */
    private BigDecimal m2YoY;

    /**
     * M2环比 (%)
     */
    private BigDecimal m2MoM;

    /**
     * M1数值 (亿元)
     */
    private BigDecimal m1Value;

    /**
     * M1同比 (%)
     */
    private BigDecimal m1YoY;

    /**
     * M1环比 (%)
     */
    private BigDecimal m1MoM;

    /**
     * M0数值 (亿元)
     */
    private BigDecimal m0Value;

    /**
     * M0同比 (%)
     */
    private BigDecimal m0YoY;

    /**
     * M0环比 (%)
     */
    private BigDecimal m0MoM;

    /**
     * 准货币 (M2-M1) 数值 (亿元)
     */
    private BigDecimal quasiMoneyValue;

    /**
     * 准货币同比 (%)
     */
    private BigDecimal quasiMoneyYoY;

    /**
     * 数据统计时间
     */
    private LocalDate statDate;

    /**
     * 数据来源标识
     */
    private String dataSource;

}