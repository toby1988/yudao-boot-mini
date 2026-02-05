package cn.iocoder.yudao.module.collect.controller.admin.aktools;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.task.*;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AkTools任务管理")
@RestController
@RequestMapping("/admin/aktools/task")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AkToolsTaskController {

    private final AkToolsTaskService taskService;

    @PostMapping("/create")
    @Operation(summary = "创建任务配置")
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:create')")
    public CommonResult<Long> createTask(@Valid @RequestBody AkToolsTaskCreateReqVO createReqVO) {
        log.info("[createTask][创建任务] req: {}", createReqVO);
        Long id = taskService.createTask(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新任务")
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:update')")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody AkToolsTaskUpdateReqVO updateReqVO) {
        log.info("[updateTask][更新任务] req: {}", updateReqVO);
        taskService.updateTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        log.info("[deleteTask][删除任务] id: {}", id);
        taskService.deleteTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:query')")
    public CommonResult<AkToolsTaskRespVO> getTask(@RequestParam("id") Long id) {
        AkToolsTaskRespVO task = taskService.getTask(id);
        return success(task);
    }

    @GetMapping("/page")
    @Operation(summary = "获得任务分页")
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:query')")
    public CommonResult<PageResult<AkToolsTaskRespVO>> getTaskPage(
            @Valid AkToolsTaskPageReqVO pageVO) {
        PageResult<AkToolsTaskRespVO> pageResult = taskService.getTaskPage(pageVO);
        return success(pageResult);
    }

    @PutMapping("/enable")
    @Operation(summary = "启用任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:update')")
    public CommonResult<Boolean> enableTask(@RequestParam("id") Long id) {
        log.info("[enableTask][启用任务] id: {}", id);
        taskService.enableTask(id);
        return success(true);
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:update')")
    public CommonResult<Boolean> disableTask(@RequestParam("id") Long id) {
        log.info("[disableTask][禁用任务] id: {}", id);
        taskService.disableTask(id);
        return success(true);
    }

    @PostMapping("/execute")
    @Operation(summary = "立即执行任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:aktools-task:execute')")
    public CommonResult<String> executeTaskNow(@RequestParam("id") Long id) {
        log.info("[executeTaskNow][立即执行任务] id: {}", id);
        String result = taskService.executeTaskNow(id);
        return success(result);
    }

    @GetMapping("/validate-cron")
    @Operation(summary = "验证Cron表达式")
    @Parameter(name = "cronExpression", description = "Cron表达式", required = true)
    public CommonResult<Boolean> validateCronExpression(@RequestParam("cronExpression") String cronExpression) {
        boolean valid = taskService.validateCronExpression(cronExpression);
        return success(valid);
    }

    @GetMapping("/next-execute-time")
    @Operation(summary = "获取下次执行时间")
    @Parameter(name = "cronExpression", description = "Cron表达式", required = true)
    public CommonResult<String> getNextExecuteTime(@RequestParam("cronExpression") String cronExpression) {
        String nextTime = taskService.getNextExecuteTime(cronExpression);
        return success(nextTime);
    }

    @GetMapping("/available-apis")
    @Operation(summary = "获取所有可用的API枚举")
    public CommonResult<String[]> getAvailableApis() {
        String[] apiEnums = {
            "STOCK_SPOT", "STOCK_VALUATION", "STOCK_FUND_FLOW", "NORTH_FUND",
            "INDEX_SPOT", "FINANCIAL_REPORT", "SHAREHOLDER_STATS", "MARKET_ACTIVITY",
            "MACRO_CPI", "MACRO_MONEY_SUPPLY", "TRADE_CALENDAR"
        };
        return success(apiEnums);
    }

}