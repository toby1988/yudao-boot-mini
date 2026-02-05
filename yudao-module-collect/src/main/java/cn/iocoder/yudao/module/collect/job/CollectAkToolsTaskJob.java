package cn.iocoder.yudao.module.collect.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsTaskDO;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.AkToolsTaskMapper;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsBatchCollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AkTools任务处理器
 * 支持从数据库配置中读取任务参数和执行逻辑
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectAkToolsTaskJob implements JobHandler {

    private final AkToolsTaskMapper taskMapper;
    private final AkToolsBatchCollectionService batchCollectionService;
    private final ObjectMapper objectMapper;

    @Override
    public String execute(String param) throws Exception {
        log.info("[execute][开始执行AkTools任务] param: {}", param);
        
        try {
            // 解析参数，格式: CONFIG_ID
            if (param == null || param.trim().isEmpty()) {
                throw new IllegalArgumentException("任务参数不能为空，应为配置ID");
            }
            
            Long configId = Long.parseLong(param.trim());
            
            // 根据配置ID获取任务配置
            AkToolsTaskDO task = taskMapper.selectById(configId);
            if (task == null) {
                throw new IllegalArgumentException("未找到对应的任务配置: " + configId);
            }
            
            if (task.getStatus() != 1) {
                throw new IllegalArgumentException("任务配置已禁用: " + configId);
            }
            
            // 查找对应的API枚举
            AkApiEnum apiEnum = AkApiEnum.getByApiPath(task.getApiEnum());
            if (apiEnum == null) {
                throw new IllegalArgumentException("未找到对应的API枚举: " + task.getApiEnum());
            }
            
            // 解析任务参数
            Object taskParam = parseTaskParam(task.getTaskParam());
            
            // 执行任务
            AkToolsBatchCollectionService.TaskExecutionResult result = 
                batchCollectionService.executeTask(apiEnum, taskParam);
            
            String message = String.format("任务执行%s: %s, 配置ID: %d, 参数: %s, 结果: %d条记录", 
                result.isSuccess() ? "成功" : "失败",
                apiEnum.getApiPath(), 
                configId,
                task.getTaskParam(), 
                result.getRecordCount());
                
            log.info("[execute][{}] {}", task.getTaskName(), message);
            return message;
            
        } catch (Exception e) {
            log.error("[execute][任务执行失败] param: {}, error: {}", param, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 解析任务参数
     *
     * @param taskParamJson JSON格式的参数字符串
     * @return 解析后的参数对象
     */
    private Object parseTaskParam(String taskParamJson) {
        if (taskParamJson == null || taskParamJson.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 尝试解析为Map
            return objectMapper.readValue(taskParamJson, Map.class);
        } catch (Exception e) {
            log.warn("[parseTaskParam][参数解析为Map失败，使用原始字符串] param: {}, error: {}", 
                taskParamJson, e.getMessage());
            return taskParamJson;
        }
    }

}