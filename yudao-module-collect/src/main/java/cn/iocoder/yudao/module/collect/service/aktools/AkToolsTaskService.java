package cn.iocoder.yudao.module.collect.service.aktools;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTaskDO;

import javax.validation.Valid;
import java.util.List;

/**
 * AkTools任务服务接口
 *
 * @author ToBy.Qoder
 */
public interface AkToolsTaskService {

    /**
     * 创建任务配置
     *
     * @param createReqVO 创建请求VO
     * @return 任务配置ID
     */
    Long createTask(@Valid AkToolsJobConfigCreateReqVO createReqVO);

    /**
     * 更新任务配置
     *
     * @param updateReqVO 更新请求VO
     */
    void updateTask(@Valid AkToolsJobConfigUpdateReqVO updateReqVO);

    /**
     * 删除任务配置
     *
     * @param id 任务配置ID
     */
    void deleteTask(Long id);

    /**
     * 获取任务配置详情
     *
     * @param id 任务配置ID
     * @return 任务配置详情
     */
    AkToolsJobConfigRespVO getTask(Long id);

    /**
     * 分页查询任务配置
     *
     * @param pageReqVO 分页查询条件
     * @return 任务配置分页结果
     */
    PageResult<AkToolsJobConfigRespVO> getTaskPage(AkToolsJobConfigPageReqVO pageReqVO);

    /**
     * 获取所有启用的任务配置
     *
     * @return 启用的任务配置列表
     */
    List<AkToolsTaskDO> getAllEnabledTasks();

    /**
     * 根据API枚举获取启用的配置列表
     *
     * @param apiEnum API枚举标识
     * @return 启用的配置列表
     */
    List<AkToolsTaskDO> getEnabledTasksByApiEnum(String apiEnum);

    /**
     * 启用任务配置
     *
     * @param id 任务配置ID
     */
    void enableTask(Long id);

    /**
     * 禁用任务配置
     *
     * @param id 任务配置ID
     */
    void disableTask(Long id);

    /**
     * 立即执行任务
     *
     * @param id 任务配置ID
     * @return 执行结果
     */
    String executeTaskNow(Long id);

    /**
     * 验证Cron表达式是否有效
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    boolean validateCronExpression(String cronExpression);

    /**
     * 获取下一个执行时间
     *
     * @param cronExpression Cron表达式
     * @return 下次执行时间
     */
    String getNextExecuteTime(String cronExpression);

}