package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.WalletGivePlan;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletGivePlanRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.WalletGivePlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/admin/agent/user/wallet/give/plan")
@Api(tags = "用户钱包奖励计划")
public class WalletGivePlanController {

    @Autowired
    private WalletGivePlanService walletGivePlanService;


    @PreAuthorize("hasAuthority('agent:user:wallet:give:plan')")
    @ApiOperation("用户钱包奖励计划列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<WalletGivePlan>> page(WalletGivePlanRequest request, PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(walletGivePlanService.pageList(request,pageParamRequest)));
    }

}
