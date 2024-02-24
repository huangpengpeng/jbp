package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletWithdrawPageRequest;
import com.jbp.common.request.agent.WalletWithdrawRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.WalletWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/wallet/withdraw")
@Api(tags = "钱包提现")
public class WalletWithdrawController {
    @Resource
    private WalletWithdrawService walletWithdrawService;
    @PreAuthorize("hasAuthority('agent:wallet:withdraw:page')")
    @GetMapping("/page")
    @ApiOperation("钱包提现列表")
    public CommonResult<CommonPage<WalletWithdraw>> getList(WalletWithdrawPageRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(walletWithdrawService.pageList(request.getAccount(), request.getWalletName(), request.getStatus(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:wallet:withdraw:send')")
    @PostMapping("/send")
    @ApiOperation("钱包提现批量出款")
    public CommonResult send(List<WalletWithdrawRequest> requests) {
        walletWithdrawService.send(requests);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:wallet:withdraw:cancel')")
    @PostMapping("/cancel")
    @ApiOperation("钱包提现批量取消")
    public CommonResult cancel(List<WalletWithdrawRequest> requests) {
        walletWithdrawService.cancel(requests);
        return CommonResult.success();
    }
}
