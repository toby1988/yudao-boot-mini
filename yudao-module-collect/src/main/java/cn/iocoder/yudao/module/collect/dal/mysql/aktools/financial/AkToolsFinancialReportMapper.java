package cn.iocoder.yudao.module.collect.dal.mysql.aktools.financial;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.financial.AkToolsFinancialReportDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务报表数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsFinancialReportMapper extends BaseMapperX<AkToolsFinancialReportDO> {

    /**
     * 根据股票代码和报告年份查询数据
     *
     * @param symbol 股票代码
     * @param reportYear 报告年份
     * @return 财务报表数据列表
     */
    List<AkToolsFinancialReportDO> selectBySymbolAndYear(String symbol, Integer reportYear);

    /**
     * 根据股票代码和截止日期查询数据
     *
     * @param symbol 股票代码
     * @param endDate 截止日期
     * @return 财务报表数据
     */
    AkToolsFinancialReportDO selectBySymbolAndDate(String symbol, LocalDate endDate);

    /**
     * 根据报告年份查询所有股票数据
     *
     * @param reportYear 报告年份
     * @return 财务报表数据列表
     */
    List<AkToolsFinancialReportDO> selectByReportYear(Integer reportYear);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsFinancialReportDO> dataList);

}