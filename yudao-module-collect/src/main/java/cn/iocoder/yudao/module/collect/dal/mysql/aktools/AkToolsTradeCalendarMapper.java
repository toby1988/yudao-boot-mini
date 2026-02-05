package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTradeCalendarDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 交易日历数据 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsTradeCalendarMapper extends BaseMapperX<AkToolsTradeCalendarDO> {

    /**
     * 根据交易日期查询数据
     *
     * @param tradeDate 交易日期
     * @return 交易日历数据
     */
    AkToolsTradeCalendarDO selectByTradeDate(LocalDate tradeDate);

    /**
     * 查询指定日期范围内的交易日数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易日历数据列表
     */
    List<AkToolsTradeCalendarDO> selectByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询某年的所有交易日
     *
     * @param year 年份
     * @return 交易日历数据列表
     */
    List<AkToolsTradeCalendarDO> selectByYear(Integer year);

    /**
     * 查询某月的所有交易日
     *
     * @param year 年份
     * @param month 月份
     * @return 交易日历数据列表
     */
    List<AkToolsTradeCalendarDO> selectByMonth(Integer year, Integer month);

    /**
     * 查询下一个交易日
     *
     * @param currentDate 当前日期
     * @return 下一个交易日
     */
    LocalDate selectNextTradingDay(LocalDate currentDate);

    /**
     * 查询上一个交易日
     *
     * @param currentDate 当前日期
     * @return 上一个交易日
     */
    LocalDate selectPreviousTradingDay(LocalDate currentDate);

    /**
     * 判断是否为交易日
     *
     * @param date 日期
     * @return true: 是交易日, false: 非交易日
     */
    boolean isTradingDay(LocalDate date);

    /**
     * 批量插入或更新数据
     *
     * @param dataList 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(List<AkToolsTradeCalendarDO> dataList);

}