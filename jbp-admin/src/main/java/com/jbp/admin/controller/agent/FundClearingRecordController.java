package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.*;
import com.jbp.common.response.FundClearingRecordResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.FundClearingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/fund/clearing/record")
@Api(tags = "运营中心")
public class FundClearingRecordController {

    @Resource
    private FundClearingRecordService fundClearingRecordService;

    @PreAuthorize("hasAuthority('agent:fund:clearing:record:page')")
    @GetMapping("/page")
    @ApiOperation("佣金发放记录列表")
    public CommonResult<CommonPage<FundClearingRecord>> getList(FundClearingRecordRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(fundClearingRecordService.pageList(request, pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "更新已取消")
    @PreAuthorize("hasAuthority('agent:fund:clearing:record:update:cancel')")
    @PostMapping("/update/cancel")
    @ApiOperation("批量更新已取消")
    public CommonResult updateCancel(@RequestBody @Validated FundClearingRecordUpdateRequest request) {
        fundClearingRecordService.updateCancel(request.getIds());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:record:total')")
    @GetMapping("/total")
    @ApiOperation("佣金发放记录汇总列表")
    public CommonResult<CommonPage<FundClearingRecordResponse>> getTotal(FundClearingRecordTotalRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(fundClearingRecordService.total(request, pageParamRequest)));
    }
}
