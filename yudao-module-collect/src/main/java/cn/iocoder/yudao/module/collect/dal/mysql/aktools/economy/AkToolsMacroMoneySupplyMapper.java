package cn.iocoder.yudao.module.collect.dal.mysql.aktools.economy;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.economy.AkToolsMacroMoneySupplyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 货币供应量数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsMacroMoneySupplyMapper extends BaseMapperX<AkToolsMacroMoneySupplyDO> {

    /**
     * 根据统计月份查询数据
     *
     * @param statMonth 统计月份 (yyyy-MM格式)
     * @return 货币供应量数据
     */
    AkToolsMacroMoneySupplyDO selectByStatMonth(String statMonth);

    /**
     * 查询指定时间段内的货币供应量数据
     *
     * @param startMonth 开始月份 (yyyy-MM格式)
     * @param endMonth 结束月份 (yyyy-MM格式)
     * @return 货币供应量数据列表
     */
    List<AkToolsMacroMoneySupplyDO> selectByMonthRange(String startMonth, String endMonth);

    /**
     * 查询最新的N条记录
     *
     * @param limit 数量限制
     * @return 货币供应量数据列表
     */
    List<AkToolsMacroMoneySupplyDO> selectLatestRecords(int limit);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsMacroMoneySupplyDO> dataList);

}