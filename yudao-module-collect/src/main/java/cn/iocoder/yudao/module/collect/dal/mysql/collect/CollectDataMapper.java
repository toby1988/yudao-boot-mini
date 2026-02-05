package cn.iocoder.yudao.module.collect.dal.mysql.collect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.data.CollectDataPageReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectDataDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据采集记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CollectDataMapper extends BaseMapperX<CollectDataDO> {

    default PageResult<CollectDataDO> selectPage(CollectDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CollectDataDO>()
                .eqIfPresent(CollectDataDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(CollectDataDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(CollectDataDO::getDataHash, reqVO.getDataHash())
                .betweenIfPresent(CollectDataDO::getCollectTime, reqVO.getCollectTime())
                .orderByDesc(CollectDataDO::getId));
    }

    default List<CollectDataDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<CollectDataDO>()
                .eq(CollectDataDO::getTaskId, taskId)
                .orderByDesc(CollectDataDO::getId));
    }

    default boolean existsByDataHash(String dataHash) {
        return selectCount(new LambdaQueryWrapperX<CollectDataDO>()
                .eq(CollectDataDO::getDataHash, dataHash)) > 0;
    }

    default int deleteByTaskIdAndCreateTimeLessThan(Long taskId, LocalDateTime expireTime) {
        return delete(new LambdaQueryWrapperX<CollectDataDO>()
                .eq(CollectDataDO::getTaskId, taskId)
                .lt(CollectDataDO::getCollectTime, expireTime));
    }

}