package cn.iocoder.yudao.module.collect.controller.admin.aktools;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsBatchCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * AkTools数据采集任务管理控制器
 * 提供手动触发和管理接口
 *
 * @author ToBy.Qoder
 */
@Tag(name = "管理后台 - AkTools数据采集任务")
@RestController
@RequestMapping("/admin/aktools/job")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AkToolsJobAdminController {

    private final AkToolsBatchCollectionService batchCollectionService;

    @PostMapping("/daily")
    @Operation(summary = "执行日维度批量数据采集")
    public CommonResult<AkToolsBatchCollectionService.BatchCollectionResult> collectDailyData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        log.info("[collectDailyData][开始执行日维度批量采集] tradeDate: {}", tradeDate);
        AkToolsBatchCollectionService.BatchCollectionResult result = 
            batchCollectionService.collectAllDailyData(tradeDate);
        return success(result);
    }

    @PostMapping("/monthly")
    @Operation(summary = "执行月维度批量数据采集")
    public CommonResult<AkToolsBatchCollectionService.BatchCollectionResult> collectMonthlyData(
            @RequestParam String month) {
        log.info("[collectMonthlyData][开始执行月维度批量采集] month: {}", month);
        AkToolsBatchCollectionService.BatchCollectionResult result = 
            batchCollectionService.collectAllMonthlyData(month);
        return success(result);
    }

    @PostMapping("/single")
    @Operation(summary = "执行单个API数据采集任务")
    public CommonResult<AkToolsBatchCollectionService.TaskExecutionResult> executeSingleTask(
            @RequestParam String apiPath,
            @RequestParam(required = false) String param) {
        log.info("[executeSingleTask][开始执行单个任务] apiPath: {}, param: {}", apiPath, param);
        
        // 查找API枚举
        AkApiEnum apiEnum = findApiEnumByPath(apiPath);
        if (apiEnum == null) {
            return CommonResult.error(400, "未找到对应的API枚举: " + apiPath);
        }
        
        AkToolsBatchCollectionService.TaskExecutionResult result = 
            batchCollectionService.executeTask(apiEnum, param);
        return success(result);
    }

    @GetMapping("/apis")
    @Operation(summary = "获取所有可用的API枚举")
    public CommonResult<List<AkApiEnum>> getAvailableApis() {
        List<AkApiEnum> apis = batchCollectionService.getAvailableApis();
        return success(apis);
    }

    /**
     * 根据API路径查找对应的枚举
     */
    private AkApiEnum findApiEnumByPath(String apiPath) {
        for (AkApiEnum apiEnum : AkApiEnum.values()) {
            if (apiEnum.getApiPath().equals(apiPath)) {
                return apiEnum;
            }
        }
        return null;
    }

}