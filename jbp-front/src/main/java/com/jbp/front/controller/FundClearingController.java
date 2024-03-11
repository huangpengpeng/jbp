package com.jbp.front.controller;

import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.FundClearingFlowGetRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/front/fund/clearing")
@Api(tags = "佣金发放记录")
public class FundClearingController {
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private UserService userService;

    @GetMapping("/totalGet")
    @ApiOperation("佣金发放记录统计")
    public CommonResult<Map<String, Object>> totalGet() {
        User info = userService.getInfo();
        return CommonResult.success(fundClearingService.totalGet(info.getId()));

    }
    @GetMapping("/flowGet")
    @ApiOperation("头部数据")
    public CommonResult<CommonPage<FundClearing>> flowGet(FundClearingFlowGetRequest request, PageParamRequest pageParamRequest) {
        User info = userService.getInfo();
        return CommonResult.success(CommonPage.restPage(fundClearingService.flowGet(info.getId(), request.getHeaderStatus(), pageParamRequest)));
    }}

