package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.PlatformWalletEditRequest;
import com.jbp.common.request.agent.PlatformWalletTransferRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.PlatformWalletService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/wallet")
@Api(tags = "平台积分")
public class PlatformWalletController {
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:platformWallet:page')")
    @ApiOperation("平台积分列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<PlatformWallet>> getList(PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(platformWalletService.pageList(pageParamRequest)));
    }

      @PreAuthorize("hasAuthority('agent:platformWallet:increase')")
    @ApiOperation("增加积分")
    @PostMapping("/increase")
    public CommonResult increase(@RequestBody @Validated PlatformWalletEditRequest request) {
        platformWalletService.increase(request.getType(), request.getAmt(), PlatformWalletFlow.OperateEnum.调账.name(),
                request.getExternalNo(), request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:platformWallet:reduce')")
    @ApiOperation("减少积分")
    @PostMapping("/reduce")
    public CommonResult reduce(@RequestBody @Validated PlatformWalletEditRequest request) {
        platformWalletService.reduce(request.getType(), request.getAmt(), PlatformWalletFlow.OperateEnum.调账.name(),
                request.getExternalNo(), request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:platformWallet:transfer')")
    @ApiOperation("转用户")
    @PostMapping("/transfer")
    public CommonResult transfer(@RequestBody @Validated PlatformWalletTransferRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (request == null) {
            return CommonResult.failed("账户信息错误");
        }
        platformWalletService.transferToUser(user.getId(), request.getType(), request.getAmt(),
                PlatformWalletFlow.OperateEnum.调账.name(), request.getExternalNo(), request.getPostscript());
        // todo 操作记录
        return CommonResult.success();
    }
}
