package cn.iocoder.yudao.module.collect.service.collect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskCreateReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskPageReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskUpdateReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据采集任务 Service 接口
 *
 * @author ToBy.Qoder
 */
public interface CollectTaskService {

    /**
     * 创建数据采集任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCollectTask(@Valid CollectTaskCreateReqVO createReqVO);

    /**
     * 更新数据采集任务
     *
     * @param updateReqVO 更新信息
     */
    void updateCollectTask(@Valid CollectTaskUpdateReqVO updateReqVO);

    /**
     * 删除数据采集任务
     *
     * @param id 编号
     */
    void deleteCollectTask(Long id);

    /**
     * 获得数据采集任务
     *
     * @param id 编号
     * @return 数据采集任务
     */
    CollectTaskDO getCollectTask(Long id);

    /**
     * 获得数据采集任务列表
     *
     * @param ids 编号
     * @return 数据采集任务列表
     */
    List<CollectTaskDO> getCollectTaskList(List<Long> ids);

    /**
     * 获得数据采集任务分页
     *
     * @param pageReqVO 分页查询
     * @return 数据采集任务分页
     */
    PageResult<CollectTaskDO> getCollectTaskPage(CollectTaskPageReqVO pageReqVO);

    /**
     * 获得启用的数据采集任务列表
     *
     * @return 数据采集任务列表
     */
    List<CollectTaskDO> getEnableCollectTaskList();

    /**
     * 执行数据采集任务
     *
     * @param id 任务ID
     */
    void executeCollectTask(Long id);

    /**
     * 更新任务统计信息
     *
     * @param id 任务ID
     * @param isSuccess 是否成功
     * @param duration 执行耗时
     */
    void updateTaskStatistics(Long id, boolean isSuccess, Integer duration);

}
