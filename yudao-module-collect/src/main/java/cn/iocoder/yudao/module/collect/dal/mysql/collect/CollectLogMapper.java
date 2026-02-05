package cn.iocoder.yudao.module.collect.dal.mysql.collect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.log.CollectLogPageReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据采集日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CollectLogMapper extends BaseMapperX<CollectLogDO> {

    default PageResult<CollectLogDO> selectPage(CollectLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CollectLogDO>()
                .eqIfPresent(CollectLogDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(CollectLogDO::getLogType, reqVO.getLogType())
                .betweenIfPresent(CollectLogDO::getExecuteTime, reqVO.getExecuteTime())
                .orderByDesc(CollectLogDO::getId));
    }

    default List<CollectLogDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<CollectLogDO>()
                .eq(CollectLogDO::getTaskId, taskId)
                .orderByDesc(CollectLogDO::getId));
    }

    default int deleteByTaskIdAndCreateTimeLessThan(Long taskId, LocalDateTime expireTime) {
        return delete(new LambdaQueryWrapperX<CollectLogDO>()
                .eq(CollectLogDO::getTaskId, taskId)
                .lt(CollectLogDO::getCreateTime, expireTime));
    }

}