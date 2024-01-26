package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public CommonResult<List<WalletConfig>> deductionlist() {
        LambdaQueryWrapper<WalletConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletConfig::getCanDeduction,true);
        return CommonResult.success(walletConfigService.list(lambdaQueryWrapper));
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
        // 1.canPay  只能存在一条记录为true  如果更新为true 要检查数据库是否存在有true 并且不是当前 的记录 有就不允许改  canDeduction false
        // 2.设置canPay  不允许设置  canDeduction  true
        // 3.设置 canDeduction true 不允许设置  canPay  true

        // 4.canWithdraw == true  有且仅有一条
        walletConfigService.update(request.getId(), request.getName(), request.getStatus(), request.getCanWithdraw(),
                request.getRecharge(), request.getChangeType(), request.getChangeScale());
        walletConfigService.update(request.getId(),request.getName(),request.getStatus(),request.getCanDeduction(),request.getCanPay(),request.getCanWithdraw(),request.getRecharge(),request.getCanTransfer(),request.getChangeScale());
        return CommonResult.success();
    }
}
