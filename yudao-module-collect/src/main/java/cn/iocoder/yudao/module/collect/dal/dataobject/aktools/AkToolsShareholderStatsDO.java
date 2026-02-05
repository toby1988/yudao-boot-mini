package cn.iocoder.yudao.module.collect.dal.dataobject.aktools;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票股东户数数据实体类
 * 对应接口: stock_zh_a_gdhs
 * 获取股票的股东户数变化情况
 *
 * @author ToBy.Qoder
 */
@TableName("collect_aktools_shareholder_stats")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AkToolsShareholderStatsDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 股票代码
     * 例如: 000001
     */
    private String symbol;

    /**
     * 股票名称
     * 例如: 平安银行
     */
    private String name;

    /**
     * 截止日期
     */
    private LocalDate endDate;

    /**
     * 股东户数
     * 单位: 户
     */
    private Long shareholderCount;

    /**
     * 户均持股数
     * 单位: 股
     */
    private BigDecimal avgSharesPerAccount;

    /**
     * 户均持股金额
     * 单位: 元
     */
    private BigDecimal avgHoldingsPerAccount;

    /**
     * 总股本
     * 单位: 股
     */
    private BigDecimal totalShares;

    /**
     * 流通股本
     * 单位: 股
     */
    private BigDecimal circulatingShares;

    /**
     * 股东户数环比变化
     * 单位: %
     */
    private BigDecimal countChangeRatio;

    /**
     * 户均持股数环比变化
     * 单位: %
     */
    private BigDecimal avgSharesChangeRatio;

    /**
     * 数据来源标识
     */
    private String dataSource;

}