package cn.iocoder.yudao.module.collect.controller.admin.aktools;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.*;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.*;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsCollectResult;
import cn.iocoder.yudao.module.collect.service.aktools.AkToolsDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

/**
 * AkTools 股票数据管理 Controller
 *
 * @author ToBy.Qoder
 */
@Tag(name = "管理后台 - AkTools 股票数据")
@RestController
@RequestMapping("/collect/aktools")
@Validated
@RequiredArgsConstructor
public class AkToolsDataController {

    private final AkToolsDataService akToolsDataService;

    // ========== 股票实时行情接口 ==========

    @PostMapping("/stock-spot/collect")
    @Operation(summary = "采集股票实时行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<Integer> collectStockSpotData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        Integer result = akToolsDataService.collectStockSpotData(tradeDate);
        return success(result);
    }

    @GetMapping("/stock-spot/get")
    @Operation(summary = "获取股票实时行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<AkToolsStockSpotRespVO> getStockSpotData(
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsStockSpotDO data = akToolsDataService.getStockSpotData(symbol, tradeDate);
        return success(BeanUtils.toBean(data, AkToolsStockSpotRespVO.class));
    }

    @GetMapping("/stock-spot/list")
    @Operation(summary = "获取某日所有股票行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<List<AkToolsStockSpotRespVO>> listStockSpotDataByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        List<AkToolsStockSpotDO> dataList = akToolsDataService.listStockSpotDataByDate(tradeDate);
        return success(BeanUtils.toBean(dataList, AkToolsStockSpotRespVO.class));
    }

    // ========== 股票估值接口 ==========

    @PostMapping("/stock-valuation/collect")
    @Operation(summary = "采集股票估值数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<Integer> collectStockValuationData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        Integer result = akToolsDataService.collectStockValuationData(tradeDate);
        return success(result);
    }

    @GetMapping("/stock-valuation/get")
    @Operation(summary = "获取股票估值数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<AkToolsStockValuationRespVO> getStockValuationData(
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsStockValuationDO data = akToolsDataService.getStockValuationData(symbol, tradeDate);
        return success(BeanUtils.toBean(data, AkToolsStockValuationRespVO.class));
    }

    @GetMapping("/stock-valuation/list")
    @Operation(summary = "获取某日所有股票估值数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<List<AkToolsStockValuationRespVO>> listStockValuationDataByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        List<AkToolsStockValuationDO> dataList = akToolsDataService.listStockValuationDataByDate(tradeDate);
        return success(BeanUtils.toBean(dataList, AkToolsStockValuationRespVO.class));
    }

    // ========== 资金流向接口 ==========

    @PostMapping("/fund-flow/collect")
    @Operation(summary = "采集股票资金流向数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<Integer> collectStockFundFlowData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        Integer result = akToolsDataService.collectStockFundFlowData(tradeDate);
        return success(result);
    }

    @GetMapping("/fund-flow/get")
    @Operation(summary = "获取股票资金流向数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<AkToolsStockFundFlowRespVO> getStockFundFlowData(
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsStockFundFlowDO data = akToolsDataService.getStockFundFlowData(symbol, tradeDate);
        return success(BeanUtils.toBean(data, AkToolsStockFundFlowRespVO.class));
    }

    @GetMapping("/fund-flow/list")
    @Operation(summary = "获取某日所有股票资金流向数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<List<AkToolsStockFundFlowRespVO>> listStockFundFlowDataByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        List<AkToolsStockFundFlowDO> dataList = akToolsDataService.listStockFundFlowDataByDate(tradeDate);
        return success(BeanUtils.toBean(dataList, AkToolsStockFundFlowRespVO.class));
    }

    // ========== 北向资金接口 ==========

    @PostMapping("/north-fund/collect")
    @Operation(summary = "采集北向资金数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<Integer> collectNorthFundData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        Integer result = akToolsDataService.collectNorthFundData(tradeDate);
        return success(result);
    }

    @GetMapping("/north-fund/get")
    @Operation(summary = "获取北向资金数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<AkToolsNorthFundRespVO> getNorthFundData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsNorthFundDO data = akToolsDataService.getNorthFundData(tradeDate);
        return success(BeanUtils.toBean(data, AkToolsNorthFundRespVO.class));
    }

    @GetMapping("/north-fund/list")
    @Operation(summary = "获取日期范围内的北向资金数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<List<AkToolsNorthFundRespVO>> listNorthFundDataByRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<AkToolsNorthFundDO> dataList = akToolsDataService.listNorthFundDataByRange(startDate, endDate);
        return success(BeanUtils.toBean(dataList, AkToolsNorthFundRespVO.class));
    }

    // ========== 指数行情接口 ==========

    @PostMapping("/index-spot/collect")
    @Operation(summary = "采集指数行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<Integer> collectIndexSpotData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        Integer result = akToolsDataService.collectIndexSpotData(tradeDate);
        return success(result);
    }

    @GetMapping("/index-spot/get")
    @Operation(summary = "获取指数行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<AkToolsIndexSpotRespVO> getIndexSpotData(
            @RequestParam String indexCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsIndexSpotDO data = akToolsDataService.getIndexSpotData(indexCode, tradeDate);
        return success(BeanUtils.toBean(data, AkToolsIndexSpotRespVO.class));
    }

    @GetMapping("/index-spot/list")
    @Operation(summary = "获取某日所有指数行情数据")
    @PreAuthorize("@ss.hasPermission('collect:aktools:query')")
    public CommonResult<List<AkToolsIndexSpotRespVO>> listIndexSpotDataByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        List<AkToolsIndexSpotDO> dataList = akToolsDataService.listIndexSpotDataByDate(tradeDate);
        return success(BeanUtils.toBean(dataList, AkToolsIndexSpotRespVO.class));
    }

    // ========== 批量采集接口 ==========

    @PostMapping("/collect-all")
    @Operation(summary = "执行每日数据采集任务")
    @PreAuthorize("@ss.hasPermission('collect:aktools:collect')")
    public CommonResult<AkToolsCollectResult> collectAllDataDaily(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradeDate) {
        AkToolsCollectResult result = akToolsDataService.collectAllDataDaily(tradeDate);
        return success(result);
    }

}
