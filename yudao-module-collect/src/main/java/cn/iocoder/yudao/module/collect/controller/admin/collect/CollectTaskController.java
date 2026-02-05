package cn.iocoder.yudao.module.collect.controller.admin.collect;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskCreateReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskPageReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskRespVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskUpdateReqVO;
import cn.iocoder.yudao.module.collect.convert.collect.CollectTaskConvert;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;
import cn.iocoder.yudao.module.collect.service.collect.CollectTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 数据采集任务")
@RestController
@RequestMapping("/collect/task")
@Validated
public class CollectTaskController {

    @Resource
    private CollectTaskService collectTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建数据采集任务")
    @PreAuthorize("@ss.hasPermission('collect:task:create')")
    public CommonResult<Long> createCollectTask(@Valid @RequestBody CollectTaskCreateReqVO createReqVO) {
        return success(collectTaskService.createCollectTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据采集任务")
    @PreAuthorize("@ss.hasPermission('collect:task:update')")
    public CommonResult<Boolean> updateCollectTask(@Valid @RequestBody CollectTaskUpdateReqVO updateReqVO) {
        collectTaskService.updateCollectTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据采集任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:task:delete')")
    public CommonResult<Boolean> deleteCollectTask(@RequestParam("id") Long id) {
        collectTaskService.deleteCollectTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据采集任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('collect:task:query')")
    public CommonResult<CollectTaskRespVO> getCollectTask(@RequestParam("id") Long id) {
        CollectTaskDO collectTask = collectTaskService.getCollectTask(id);
        return success(CollectTaskConvert.INSTANCE.convert(collectTask));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据采集任务分页")
    @PreAuthorize("@ss.hasPermission('collect:task:query')")
    public CommonResult<PageResult<CollectTaskRespVO>> getCollectTaskPage(@Valid CollectTaskPageReqVO pageVO) {
        PageResult<CollectTaskDO> pageResult = collectTaskService.getCollectTaskPage(pageVO);
        return success(CollectTaskConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取数据采集任务精简信息列表")
    @PreAuthorize("@ss.hasPermission('collect:task:query')")
    public CommonResult<List<CollectTaskRespVO>> getSimpleCollectTaskList() {
        List<CollectTaskDO> list = collectTaskService.getEnableCollectTaskList();
        return success(CollectTaskConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出数据采集任务 Excel")
    @PreAuthorize("@ss.hasPermission('collect:task:export')")
    @OperateLog(type = EXPORT)
    public void exportCollectTaskExcel(@Valid CollectTaskPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageResult.PAGE_SIZE_NONE);
        List<CollectTaskDO> list = collectTaskService.getCollectTaskPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "数据采集任务.xls", "数据", CollectTaskRespVO.class,
                CollectTaskConvert.INSTANCE.convertList(list));
    }

    @PutMapping("/execute")
    @Operation(summary = "立即执行数据采集任务")
    @Parameter(name = "id", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('collect:task:execute')")
    public CommonResult<Boolean> executeCollectTask(@RequestParam("id") Long id) {
        collectTaskService.executeCollectTask(id);
        return success(true);
    }

}
