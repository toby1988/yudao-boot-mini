package cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 中国CPI数据实体类
 * 对应接口: macro_china_cpi
 * 获取消费者物价指数数据，用于通胀分析和市场择时
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_macro_cpi")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsMacroCpiDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 发布月份
     * 格式: yyyy-MM
     */
    private String publishMonth;

    /**
     * 全国同比 (%)
     * 与去年同期相比的增长率
     */
    private BigDecimal nationalYoY;

    /**
     * 全国环比 (%)
     * 与上月相比的增长率
     */
    private BigDecimal nationalMoM;

    /**
     * 城市同比 (%)
     */
    private BigDecimal urbanYoY;

    /**
     * 农村同比 (%)
     */
    private BigDecimal ruralYoY;

    /**
     * 食品同比 (%)
     */
    private BigDecimal foodYoY;

    /**
     * 非食品同比 (%)
     */
    private BigDecimal nonFoodYoY;

    /**
     * 消费品同比 (%)
     */
    private BigDecimal consumerGoodsYoY;

    /**
     * 服务同比 (%)
     */
    private BigDecimal servicesYoY;

    /**
     * 数据发布时间
     */
    private LocalDate publishDate;

    /**
     * 数据来源标识
     */
    private String dataSource;

}