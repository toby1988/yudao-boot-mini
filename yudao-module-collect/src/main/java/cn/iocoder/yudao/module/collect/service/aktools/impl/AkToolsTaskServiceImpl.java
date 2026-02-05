package cn.iocoder.yudao.module.collect.service.aktools.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTaskDO;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.AkToolsTaskMapper;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.job.aktools.executor.AkToolsTaskExecutor;
import cn.iocoder.yudao.module.collect.job.aktools.factory.AkToolsTaskExecutorFactory;
import cn.iocoder.yudao.module.collect.job.aktools.handler.AkToolsDataCollectionJobHandler;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsBatchCollectionService;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.collect.enums.ErrorCodeConstants.*;

/**
 * AkTools任务服务实现类
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AkToolsTaskServiceImpl implements AkToolsTaskService {

    private final AkToolsTaskMapper taskMapper;
    private final AkToolsTaskExecutorFactory executorFactory;
    private final AkToolsBatchCollectionService batchCollectionService;
    private final AkToolsDataCollectionJobHandler jobHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(@Valid AkToolsTaskCreateReqVO createReqVO) {
        // 验证API枚举是否存在
        validateApiEnum(createReqVO.getApiEnum());
        
        // 验证Cron表达式
        if (!validateCronExpression(createReqVO.getCronExpression())) {
            throw exception(JOB_CONFIG_CRON_INVALID);
        }
        
        // 验证任务名称唯一性
        validateTaskNameUnique(null, createReqVO.getTaskName());
        
        // 转换为DO对象
        AkToolsTaskDO task = BeanUtils.toBean(createReqVO, AkToolsTaskDO.class);
        task.setCreator("system"); // TODO: 从SecurityContextHolder获取当前用户
        
        // 插入数据库
        taskMapper.insert(task);
        log.info("[createTask][创建任务成功] id: {}, taskName: {}", task.getId(), task.getTaskName());
        
        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(@Valid AkToolsTaskUpdateReqVO updateReqVO) {
        // 校验存在
        validateTaskExists(updateReqVO.getId());
        
        // 验证API枚举
        if (StrUtil.isNotEmpty(updateReqVO.getApiEnum())) {
            validateApiEnum(updateReqVO.getApiEnum());
        }
        
        // 验证Cron表达式
        if (StrUtil.isNotEmpty(updateReqVO.getCronExpression())) {
            if (!validateCronExpression(updateReqVO.getCronExpression())) {
                throw exception(JOB_CONFIG_CRON_INVALID);
            }
        }
        
        // 验证任务名称唯一性
        validateTaskNameUnique(updateReqVO.getId(), updateReqVO.getTaskName());
        
        // 更新数据库
        AkToolsTaskDO updateObj = BeanUtils.toBean(updateReqVO, AkToolsTaskDO.class);
        updateObj.setUpdater("system"); // TODO: 从SecurityContextHolder获取当前用户
        taskMapper.updateById(updateObj);
        log.info("[updateTask][更新任务成功] id: {}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        // 校验存在
        validateTaskExists(id);
        
        // 删除数据库记录
        taskMapper.deleteById(id);
        log.info("[deleteTask][删除任务成功] id: {}", id);
    }

    @Override
    public AkToolsTaskRespVO getTask(Long id) {
        AkToolsTaskDO task = taskMapper.selectById(id);
        if (task == null) {
            throw exception(JOB_CONFIG_NOT_EXISTS);
        }
        return BeanUtils.toBean(task, AkToolsTaskRespVO.class);
    }

    @Override
    public PageResult<AkToolsTaskRespVO> getTaskPage(AkToolsTaskPageReqVO pageReqVO) {
        PageResult<AkToolsTaskDO> pageResult = taskMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, AkToolsTaskRespVO.class);
    }

    @Override
    public List<AkToolsTaskDO> getAllEnabledTasks() {
        return taskMapper.selectAllEnabled();
    }

    @Override
    public List<AkToolsTaskDO> getEnabledTasksByApiEnum(String apiEnum) {
        return taskMapper.selectEnabledByApiEnum(apiEnum);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTask(Long id) {
        updateTaskStatus(id, 1);
        log.info("[enableTask][启用任务成功] id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTask(Long id) {
        updateTaskStatus(id, 0);
        log.info("[disableTask][禁用任务成功] id: {}", id);
    }

    @Override
    public String executeTaskNow(Long id) {
        // 校验存在
        AkToolsTaskDO task = taskMapper.selectById(id);
        if (task == null) {
            throw exception(JOB_CONFIG_NOT_EXISTS);
        }
        
        if (task.getStatus() != 1) {
            throw exception(JOB_CONFIG_DISABLED);
        }
        
        try {
            // 查找对应的API枚举
            AkApiEnum apiEnum = AkApiEnum.getByApiPath(task.getApiEnum());
            if (apiEnum == null) {
                throw exception(JOB_CONFIG_API_ENUM_NOT_FOUND);
            }
            
            // 执行任务
            AkToolsBatchCollectionService.TaskExecutionResult result = 
                batchCollectionService.executeTask(apiEnum, task.getTaskParam());
            
            String message = String.format("任务执行%s: %s, 参数: %s, 结果: %d条记录", 
                result.isSuccess() ? "成功" : "失败",
                apiEnum.getApiPath(), 
                task.getTaskParam(), 
                result.getRecordCount());
                
            log.info("[executeTaskNow][{}] {}", task.getTaskName(), message);
            return message;
            
        } catch (Exception e) {
            log.error("[executeTaskNow][任务执行失败] id: {}, error: {}", id, e.getMessage(), e);
            throw exception(JOB_CONFIG_EXECUTE_FAILED, e.getMessage());
        }
    }

    @Override
    public boolean validateCronExpression(String cronExpression) {
        if (StrUtil.isBlank(cronExpression)) {
            return false;
        }
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public String getNextExecuteTime(String cronExpression) {
        if (!validateCronExpression(cronExpression)) {
            return "无效的Cron表达式";
        }
        
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date nextTime = cron.getNextValidTimeAfter(new Date());
            if (nextTime != null) {
                return nextTime.toString();
            }
            return "无下次执行时间";
        } catch (Exception e) {
            return "计算下次执行时间失败";
        }
    }

    // ========== 私有方法 ==========

    private void validateTaskExists(Long id) {
        if (taskMapper.selectById(id) == null) {
            throw exception(JOB_CONFIG_NOT_EXISTS);
        }
    }

    private void validateApiEnum(String apiEnum) {
        if (!AkApiEnum.exists(apiEnum)) {
            throw exception(JOB_CONFIG_API_ENUM_NOT_FOUND);
        }
    }

    private void validateTaskNameUnique(Long id, String taskName) {
        AkToolsTaskDO existTask = taskMapper.selectByTaskName(taskName);
        if (existTask != null && !existTask.getId().equals(id)) {
            throw exception(JOB_CONFIG_NAME_DUPLICATE);
        }
    }

    private void updateTaskStatus(Long id, Integer status) {
        validateTaskExists(id);
        AkToolsTaskDO updateObj = new AkToolsTaskDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        updateObj.setUpdater("system"); // TODO: 从SecurityContextHolder获取当前用户
        taskMapper.updateById(updateObj);
    }

}