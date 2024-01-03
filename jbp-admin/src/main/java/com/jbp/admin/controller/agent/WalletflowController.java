package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletformEditRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/wallet")
@Api(tags = "用户积分")
public class WalletflowController {
    @Resource
    WalletService walletService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:walletflow:page')")
    @ApiOperation("用户积分列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<Wallet>> getList(PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(walletService.pageList(pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:walletflow:increase')")
    @ApiOperation("增加积分")
    @PostMapping("/increase")
    public CommonResult increase(@RequestBody @Validated WalletformEditRequest request) {
        User user = userService.getByAccount(request.getAccount());
        walletService.increase(user.getId(),request.getType(), request.getAmt(), WalletFlow.OperateEnum.调账.name(),
                request.getExternalNo(), request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:walletflow:reduce')")
    @ApiOperation("减少积分")
    @PostMapping("/reduce")
    public CommonResult reduce(@RequestBody @Validated WalletformEditRequest request) {
        User user = userService.getByAccount(request.getAccount());
        walletService.reduce(user.getId(),request.getType(),request.getAmt(),WalletFlow.OperateEnum.调账.name(),
                 request.getExternalNo(),request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:walletflow:transfer')")
    @ApiOperation("转平台")
    @PostMapping("/transfer")
    public CommonResult transfer(@RequestBody @Validated WalletformEditRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (request == null) {
            return CommonResult.failed("账户信息错误");
        }
        walletService.transferToPlatform(user.getId(), request.getType(), request.getAmt(),
                WalletFlow.OperateEnum.调账.name(), request.getExternalNo(), request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }

}
