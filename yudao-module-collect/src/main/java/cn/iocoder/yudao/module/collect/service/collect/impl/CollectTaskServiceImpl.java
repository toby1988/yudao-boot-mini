package cn.iocoder.yudao.module.collect.service.collect.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskCreateReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskPageReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskUpdateReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;
import cn.iocoder.yudao.module.collect.dal.mysql.collect.CollectTaskMapper;
import cn.iocoder.yudao.module.collect.enums.CollectTypeEnum;
import cn.iocoder.yudao.module.collect.service.collect.CollectTaskService;
import cn.iocoder.yudao.module.collect.util.CollectUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.collect.enums.ErrorCodeConstants.*;

/**
 * 数据采集任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class CollectTaskServiceImpl implements CollectTaskService {

    @Resource
    private CollectTaskMapper collectTaskMapper;

    @Override
    public Long createCollectTask(CollectTaskCreateReqVO createReqVO) {
        // 校验任务编码唯一性
        validateTaskCodeUnique(null, createReqVO.getCode());
        // 校验采集类型
        validateCollectType(createReqVO.getType());
        // 校验Cron表达式
        validateCronExpression(createReqVO.getCronExpression());

        // 插入
        CollectTaskDO collectTask = BeanUtils.toBean(createReqVO, CollectTaskDO.class);
        collectTask.setStatus(cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE.getStatus());
        collectTaskMapper.insert(collectTask);
        return collectTask.getId();
    }

    @Override
    public void updateCollectTask(CollectTaskUpdateReqVO updateReqVO) {
        // 校验存在
        validateCollectTaskExists(updateReqVO.getId());
        // 校验任务编码唯一性
        validateTaskCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验采集类型
        validateCollectType(updateReqVO.getType());
        // 校验Cron表达式
        validateCronExpression(updateReqVO.getCronExpression());

        // 更新
        CollectTaskDO updateObj = BeanUtils.toBean(updateReqVO, CollectTaskDO.class);
        collectTaskMapper.updateById(updateObj);
    }

    @Override
    public void deleteCollectTask(Long id) {
        // 校验存在
        validateCollectTaskExists(id);
        // 删除
        collectTaskMapper.deleteById(id);
    }

    private void validateCollectTaskExists(Long id) {
        if (collectTaskMapper.selectById(id) == null) {
            throw exception(TASK_NOT_EXISTS);
        }
    }

    private void validateTaskCodeUnique(Long id, String code) {
        CollectTaskDO task = collectTaskMapper.selectByCode(code);
        if (task == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(TASK_CODE_DUPLICATE);
        }
        if (!task.getId().equals(id)) {
            throw exception(TASK_CODE_DUPLICATE);
        }
    }

    private void validateCollectType(Integer type) {
        if (CollectTypeEnum.valueOfType(type) == null) {
            throw exception(TASK_TYPE_INVALID);
        }
    }

    private void validateCronExpression(String cronExpression) {
        if (!CollectUtils.isValidCronExpression(cronExpression)) {
            throw exception(TASK_CRON_INVALID);
        }
    }

    @Override
    public CollectTaskDO getCollectTask(Long id) {
        return collectTaskMapper.selectById(id);
    }

    @Override
    public List<CollectTaskDO> getCollectTaskList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return List.of();
        }
        return collectTaskMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<CollectTaskDO> getCollectTaskPage(CollectTaskPageReqVO pageReqVO) {
        return collectTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CollectTaskDO> getEnableCollectTaskList() {
        return collectTaskMapper.selectListByStatus(
                cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeCollectTask(Long id) {
        CollectTaskDO task = getCollectTask(id);
        if (task == null) {
            throw exception(TASK_NOT_EXISTS);
        }
        if (!cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE.getStatus()
                .equals(task.getStatus())) {
            throw exception(TASK_NOT_ENABLED);
        }

        long startTime = System.currentTimeMillis();
        boolean success = false;
        try {
            // 根据不同的采集类型执行相应的采集逻辑
            switch (CollectTypeEnum.valueOfType(task.getType())) {
                case HTTP_INTERFACE:
                    executeHttpInterfaceTask(task);
                    break;
                case DATABASE_QUERY:
                    executeDatabaseQueryTask(task);
                    break;
                case FILE_READ:
                    executeFileReadTask(task);
                    break;
                case SYSTEM_METRICS:
                    executeSystemMetricsTask(task);
                    break;
                default:
                    throw new ServiceException("不支持的采集类型: " + task.getType());
            }
            success = true;
        } finally {
            // 更新任务统计信息
            Integer duration = (int) (System.currentTimeMillis() - startTime);
            updateTaskStatistics(id, success, duration);
        }
    }

    /**
     * 执行HTTP接口采集任务
     */
    private void executeHttpInterfaceTask(CollectTaskDO task) {
        // TODO: 实现HTTP接口采集逻辑
        log.info("[executeHttpInterfaceTask][任务ID: {}] 执行HTTP接口采集", task.getId());
        // 这里应该根据 sourceConfig 中的配置调用HTTP接口
    }

    /**
     * 执行数据库查询采集任务
     */
    private void executeDatabaseQueryTask(CollectTaskDO task) {
        // TODO: 实现数据库查询采集逻辑
        log.info("[executeDatabaseQueryTask][任务ID: {}] 执行数据库查询采集", task.getId());
        // 这里应该根据 sourceConfig 中的配置执行SQL查询
    }

    /**
     * 执行文件读取采集任务
     */
    private void executeFileReadTask(CollectTaskDO task) {
        // TODO: 实现文件读取采集逻辑
        log.info("[executeFileReadTask][任务ID: {}] 执行文件读取采集", task.getId());
        // 这里应该根据 sourceConfig 中的配置读取文件
    }

    /**
     * 执行系统指标采集任务
     */
    private void executeSystemMetricsTask(CollectTaskDO task) {
        // TODO: 实现系统指标采集逻辑
        log.info("[executeSystemMetricsTask][任务ID: {}] 执行系统指标采集", task.getId());
        // 这里应该采集系统CPU、内存、磁盘等指标
    }

    @Override
    public void updateTaskStatistics(Long id, boolean isSuccess, Integer duration) {
        CollectTaskDO task = getCollectTask(id);
        if (task == null) {
            return;
        }

        CollectTaskDO updateObj = new CollectTaskDO();
        updateObj.setId(id);
        updateObj.setLastExecuteTime(LocalDateTime.now());
        updateObj.setExecuteCount(task.getExecuteCount() + 1);
        if (isSuccess) {
            updateObj.setSuccessCount(task.getSuccessCount() + 1);
        } else {
            updateObj.setFailCount(task.getFailCount() + 1);
        }
        collectTaskMapper.updateById(updateObj);
    }

}