package com.jbp.front.controller;

import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRecordSelfRequest;
import com.jbp.common.response.FundClearingRecordSelfResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;

@Slf4j
@RestController
@RequestMapping("api/front/fund/clearing/record")
@Api(tags = "运营中心")
public class FundClearingRecordController implements Serializable {

    @Resource
    private FundClearingRecordService fundClearingRecordService;

    @Resource
    private UserService userService;

    @GetMapping("/total")
    @ApiOperation("个人佣金发放记录汇总列表")
    public CommonResult<FundClearingRecordSelfResponse> getTotal() {
        User info = userService.getInfo();
        return CommonResult.success(fundClearingRecordService.selfTotal(info.getId()));
    }

    @GetMapping("/detail")
    @ApiOperation("个人佣金发放记录明细列表")
    public CommonResult<CommonPage<FundClearingRecord>> getList(FundClearingRecordSelfRequest request ,PageParamRequest pageParamRequest) {
        User info = userService.getInfo();
        return CommonResult.success(CommonPage.restPage(fundClearingRecordService.detail(info.getId(),request.getDay(), pageParamRequest)));
    }
}
