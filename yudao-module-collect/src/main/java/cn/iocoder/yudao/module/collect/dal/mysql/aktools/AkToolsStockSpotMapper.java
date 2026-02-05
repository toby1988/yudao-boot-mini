package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsStockSpotDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 股票实时行情数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsStockSpotMapper extends BaseMapperX<AkToolsStockSpotDO> {

    /**
     * 根据股票代码和交易日期查询数据
     *
     * @param symbol 股票代码
     * @param tradeDate 交易日期
     * @return 股票行情数据
     */
    AkToolsStockSpotDO selectBySymbolAndDate(String symbol, LocalDate tradeDate);

    /**
     * 根据交易日期查询所有股票数据
     *
     * @param tradeDate 交易日期
     * @return 股票行情数据列表
     */
    List<AkToolsStockSpotDO> selectByTradeDate(LocalDate tradeDate);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsStockSpotDO> dataList);

}
