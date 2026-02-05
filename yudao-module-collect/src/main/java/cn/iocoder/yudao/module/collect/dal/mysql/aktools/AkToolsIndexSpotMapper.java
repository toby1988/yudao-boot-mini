package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsIndexSpotDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 指数行情数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsIndexSpotMapper extends BaseMapperX<AkToolsIndexSpotDO> {

    /**
     * 根据指数代码和交易日期查询数据
     *
     * @param indexCode 指数代码
     * @param tradeDate 交易日期
     * @return 指数行情数据
     */
    AkToolsIndexSpotDO selectByIndexCodeAndDate(String indexCode, LocalDate tradeDate);

    /**
     * 根据交易日期查询所有指数数据
     *
     * @param tradeDate 交易日期
     * @return 指数行情数据列表
     */
    List<AkToolsIndexSpotDO> selectByTradeDate(LocalDate tradeDate);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsIndexSpotDO> dataList);

}
