package cn.iocoder.yudao.module.collect.dal.mysql.aktools;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.jobconfig.AkToolsJobConfigPageReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AkTools任务 Mapper
 *
 * @author ToBy.Qoder
 */
@Mapper
public interface AkToolsTaskMapper extends BaseMapperX<AkToolsTaskDO> {

    /**
     * 分页查询任务配置
     *
     * @param reqVO 查询条件
     * @return 任务配置分页结果
     */
    default PageResult<AkToolsTaskDO> selectPage(AkToolsJobConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AkToolsTaskDO>()
                .likeIfPresent(AkToolsTaskDO::getTaskName, reqVO.getJobName())
                .eqIfPresent(AkToolsTaskDO::getApiEnum, reqVO.getApiEnum())
                .eqIfPresent(AkToolsTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AkToolsTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AkToolsTaskDO::getId));
    }

    /**
     * 根据任务名称查询任务
     *
     * @param taskName 任务名称
     * @return 任务
     */
    default AkToolsTaskDO selectByTaskName(String taskName) {
        return selectOne(AkToolsTaskDO::getTaskName, taskName);
    }

    /**
     * 根据API枚举查询启用的配置列表
     *
     * @param apiEnum API枚举标识
     * @return 启用的配置列表
     */
    default List<AkToolsTaskDO> selectEnabledByApiEnum(String apiEnum) {
        return selectList(new LambdaQueryWrapperX<AkToolsTaskDO>()
                .eq(AkToolsTaskDO::getApiEnum, apiEnum)
                .eq(AkToolsTaskDO::getStatus, 1));
    }

    /**
     * 查询所有启用的配置
     *
     * @return 启用的配置列表
     */
    default List<AkToolsTaskDO> selectAllEnabled() {
        return selectList(AkToolsTaskDO::getStatus, 1);
    }

    /**
     * 根据状态查询配置列表
     *
     * @param status 状态
     * @return 配置列表
     */
    default List<AkToolsTaskDO> selectByStatus(Integer status) {
        return selectList(AkToolsTaskDO::getStatus, status);
    }

}