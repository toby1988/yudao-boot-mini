package cn.iocoder.yudao.module.collect.dal.mysql.aktools.market;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.market.AkToolsMarketActivityDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 市场活跃度数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsMarketActivityMapper extends BaseMapperX<AkToolsMarketActivityDO> {

    /**
     * 根据交易日期查询数据
     *
     * @param tradeDate 交易日期
     * @return 市场活跃度数据
     */
    AkToolsMarketActivityDO selectByTradeDate(LocalDate tradeDate);

    /**
     * 查询指定日期范围内的数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 市场活跃度数据列表
     */
    List<AkToolsMarketActivityDO> selectByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询最新的N条记录
     *
     * @param limit 数量限制
     * @return 市场活跃度数据列表
     */
    List<AkToolsMarketActivityDO> selectLatestRecords(int limit);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsMarketActivityDO> dataList);

}