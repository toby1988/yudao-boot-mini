package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsShareholderStatsDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 股东户数数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsShareholderStatsMapper extends BaseMapperX<AkToolsShareholderStatsDO> {

    /**
     * 根据股票代码和截止日期查询数据
     *
     * @param symbol 股票代码
     * @param endDate 截止日期
     * @return 股东户数数据
     */
    AkToolsShareholderStatsDO selectBySymbolAndDate(String symbol, LocalDate endDate);

    /**
     * 根据股票代码查询历史数据
     *
     * @param symbol 股票代码
     * @return 股东户数数据列表
     */
    List<AkToolsShareholderStatsDO> selectBySymbol(String symbol);

    /**
     * 根据截止日期查询所有股票数据
     *
     * @param endDate 截止日期
     * @return 股东户数数据列表
     */
    List<AkToolsShareholderStatsDO> selectByEndDate(LocalDate endDate);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsShareholderStatsDO> dataList);

}