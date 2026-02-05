package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsNorthFundDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 北向资金数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsNorthFundMapper extends BaseMapperX<AkToolsNorthFundDO> {

    /**
     * 根据交易日期查询数据
     *
     * @param tradeDate 交易日期
     * @return 北向资金数据
     */
    AkToolsNorthFundDO selectByTradeDate(LocalDate tradeDate);

    /**
     * 查询指定日期范围内的数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 北向资金数据列表
     */
    List<AkToolsNorthFundDO> selectByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsNorthFundDO> dataList);

}
