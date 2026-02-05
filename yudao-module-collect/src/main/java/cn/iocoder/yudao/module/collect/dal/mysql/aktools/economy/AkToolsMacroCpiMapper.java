package cn.iocoder.yudao.module.collect.dal.mysql.aktools.economy;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroCpiDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 宏观经济CPI数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsMacroCpiMapper extends BaseMapperX<AkToolsMacroCpiDO> {

    /**
     * 根据发布月份查询数据
     *
     * @param publishMonth 发布月份 (yyyy-MM格式)
     * @return CPI数据
     */
    AkToolsMacroCpiDO selectByPublishMonth(String publishMonth);

    /**
     * 查询指定时间段内的CPI数据
     *
     * @param startMonth 开始月份 (yyyy-MM格式)
     * @param endMonth 结束月份 (yyyy-MM格式)
     * @return CPI数据列表
     */
    List<AkToolsMacroCpiDO> selectByMonthRange(String startMonth, String endMonth);

    /**
     * 查询最新的N条记录
     *
     * @param limit 数量限制
     * @return CPI数据列表
     */
    List<AkToolsMacroCpiDO> selectLatestRecords(int limit);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsMacroCpiDO> dataList);

}