package com.jbp.admin.controller.merchant;

import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.PlatformWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/merchant/wallet")
@Api(tags = "平台积分")
public class PlatformWalletController {
    @Resource
    private PlatformWalletService platformWalletService;

    @GetMapping("/page/list")
    @ApiOperation("平台积分列表")
    public CommonResult<CommonPage<PlatformWallet>> getList(@ModelAttribute @Validated PlatformWalletRequest request,
                                                            @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(platformWalletService.pageList(request, pageParamRequest)));
    }

    @PostMapping("/add")
    @ApiOperation("平台钱包添加")
    public CommonResult addPlatformWallet(@RequestBody PlatformWalletRequest platformWalletRequest) {
        platformWalletService.add(platformWalletRequest);
        return CommonResult.success();
    }

    @GetMapping("/transfer/user")
    public CommonResult transferToUser(PlatformWalletFlowRequest platformWalletFlowRequest) {
        platformWalletService.transferToUser(platformWalletFlowRequest.getType(),platformWalletFlowRequest.getAction(),
                platformWalletFlowRequest.getOperate(),platformWalletFlowRequest.getExternalNo(), platformWalletFlowRequest.getPostscript(),
                platformWalletFlowRequest.getUid(),platformWalletFlowRequest.getTransferIntegral());
        return CommonResult.success().setMessage("转账成功");
    }
}
