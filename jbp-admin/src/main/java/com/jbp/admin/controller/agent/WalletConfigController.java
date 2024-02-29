package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletConfigEditRequest;
import com.jbp.common.request.agent.WalletConfigRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WalletConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/wallet/config")
@Api(tags = "积分配置")
public class WalletConfigController {
    @Resource
    private WalletConfigService walletConfigService;

    @GetMapping("/list")
    @ApiOperation("列表")
    public CommonResult<List<WalletConfig>> list() {
        return CommonResult.success(walletConfigService.list());
    }

    @GetMapping("/deduction/list")
    @ApiOperation("获取可抵扣可支付商品列表")
    public CommonResult<List<WalletConfig>> deductionList() {
        return CommonResult.success(walletConfigService.getCanDeductionList());
    }

    @PreAuthorize("hasAuthority('agent:wallet:config:page')")
    @GetMapping("/page")
    @ApiOperation("分页列表")
    public CommonResult<CommonPage<WalletConfig>> pageList(WalletConfigRequest request, PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(walletConfigService.pageList(request.getName(), request.getStatus(),
                request.getCanWithdraw(), request.getRecharge(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:wallet:config:update')")
    @PostMapping("/update")
    @ApiOperation("修改")
    public CommonResult update(@RequestBody WalletConfigEditRequest request) {
        walletConfigService.update(request.getId(), request.getName(), request.getStatus(), request.getCanDeduction(), request.getCanPay(), request.getCanWithdraw(), request.getRecharge(), request.getCanTransfer(), request.getChangeScale(), request.getChangeType());
        return CommonResult.success();
    }
}
