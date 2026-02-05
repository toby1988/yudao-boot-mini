package cn.iocoder.yudao.module.collect.dal.mysql.collect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskPageReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据采集任务 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CollectTaskMapper extends BaseMapperX<CollectTaskDO> {

    default PageResult<CollectTaskDO> selectPage(CollectTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CollectTaskDO>()
                .likeIfPresent(CollectTaskDO::getName, reqVO.getName())
                .eqIfPresent(CollectTaskDO::getCode, reqVO.getCode())
                .eqIfPresent(CollectTaskDO::getType, reqVO.getType())
                .eqIfPresent(CollectTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CollectTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CollectTaskDO::getId));
    }

    default List<CollectTaskDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<CollectTaskDO>()
                .eq(CollectTaskDO::getStatus, status)
                .orderByAsc(CollectTaskDO::getId));
    }

    default CollectTaskDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<CollectTaskDO>()
                .eq(CollectTaskDO::getCode, code));
    }

}