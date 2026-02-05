package cn.iocoder.yudao.module.collect.job;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;
import cn.iocoder.yudao.module.collect.service.collect.CollectTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据采集定时任务
 *
 * @author ToBy.Qoder
 */
@Component
@Slf4j
public class CollectDataTaskJob implements JobHandler {

    @Resource
    private CollectTaskService collectTaskService;

    @Override
    @TenantIgnore
    public String execute(String param) throws Exception {
        log.info("[execute][开始执行数据采集任务]");
        
        // 获取所有启用的任务
        List<CollectTaskDO> taskList = collectTaskService.getEnableCollectTaskList();
        if (CollUtil.isEmpty(taskList)) {
            return "没有需要执行的采集任务";
        }

        int successCount = 0;
        int failCount = 0;
        
        // 逐个执行任务
        for (CollectTaskDO task : taskList) {
            try {
                collectTaskService.executeCollectTask(task.getId());
                successCount++;
                log.info("[execute][任务执行成功] 任务ID: {}, 任务名称: {}", task.getId(), task.getName());
            } catch (Exception e) {
                failCount++;
                log.error("[execute][任务执行失败] 任务ID: {}, 任务名称: {}", task.getId(), task.getName(), e);
            }
        }
        
        return String.format("数据采集任务执行完成，成功: %d 个，失败: %d 个", successCount, failCount);
    }

}
