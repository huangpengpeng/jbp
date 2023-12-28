package com.jbp.front.controller;

import com.jbp.common.model.b2b.PlatformWallet;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.PlatformWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/front/platformWallet")
@Api(tags = "平台积分")
public class PlatformWalletController {
    @Resource
    private PlatformWalletService platformWalletService;
    @GetMapping("/list")
    @ApiOperation("平台积分列表")
    public CommonResult<CommonPage<PlatformWallet>> getList(@ModelAttribute @Validated PlatformWallet request,
                                                            @ModelAttribute PageParamRequest pageParamRequest) {
       return null;
    }
}
