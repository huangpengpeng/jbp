package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.FundClearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/fund/clearing")
@Api(tags = "佣金发放记录")
public class FundClearingController {
    @Resource
    private FundClearingService fundClearingService;

    @GetMapping("/page")
    @ApiOperation("佣金发放记录列表")
    public CommonResult<CommonPage<FundClearing>> getList(FundClearingRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(fundClearingService.pageList(request.getUniqueNo(), request.getExternalNo(), request.getStartClearingTime(), request.getEndClearingTime(), request.getStartCreateTime(), request.getEndCreateTime(), request.getStatus(), pageParamRequest)));
    }

    @GetMapping("/status/list")
    @ApiOperation("结算状态列表")
    public CommonResult<Object> statusList() {
        return CommonResult.success(FundClearing.Constants.values());
    }
}
