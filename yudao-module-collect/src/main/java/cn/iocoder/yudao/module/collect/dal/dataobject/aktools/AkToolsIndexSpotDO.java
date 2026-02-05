package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 指数行情数据实体类
 * 对应接口: stock_zh_index_spot_em
 * 抓取时间: 15:30
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_index_spot")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsIndexSpotDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 指数代码
     * 例如: 000001 (上证指数)
     */
    private String indexCode;

    /**
     * 指数名称
     * 例如: 上证指数
     */
    private String indexName;

    /**
     * 最新点位
     */
    private BigDecimal currentPoint;

    /**
     * 涨跌幅
     * 单位: %
     */
    private BigDecimal changePercent;

    /**
     * 涨跌点数
     */
    private BigDecimal changePoint;

    /**
     * 成交量
     * 单位: 亿手
     */
    private BigDecimal volume;

    /**
     * 成交额
     * 单位: 亿元
     */
    private BigDecimal amount;

    /**
     * 换手率
     * 单位: %
     */
    private BigDecimal turnoverRate;

    /**
     * 市盈率
     */
    private BigDecimal peRatio;

    /**
     * 市净率
     */
    private BigDecimal pbRatio;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 数据来源标识
     */
    private String dataSource;

}
