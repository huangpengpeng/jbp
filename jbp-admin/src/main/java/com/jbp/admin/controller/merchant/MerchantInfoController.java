package com.jbp.admin.controller.merchant;


import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.response.MerchantBaseInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.MerchantConfigInfoVo;
import com.jbp.common.vo.MerchantSettlementInfoVo;
import com.jbp.service.service.MerchantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户端商户信息控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant")
@Api(tags = "商户端商户信息控制器")
@Validated
public class MerchantInfoController {

    @Autowired
    private MerchantService merchantService;

    @PreAuthorize("hasAuthority('merchant:base:info')")
    @ApiOperation(value = "商户端商户基础信息")
    @RequestMapping(value = "/base/info", method = RequestMethod.GET)
    public CommonResult<MerchantBaseInfoResponse> getBaseInfo() {
        return CommonResult.success(merchantService.getBaseInfo());
    }

    @PreAuthorize("hasAuthority('merchant:config:info')")
    @ApiOperation(value = "商户端商户配置信息")
    @RequestMapping(value = "/config/info", method = RequestMethod.GET)
    public CommonResult<MerchantConfigInfoVo> getConfigInfo() {
        return CommonResult.success(merchantService.getConfigInfo());
    }

    @PreAuthorize("hasAuthority('merchant:settlement:info')")
    @ApiOperation(value = "商户端商户结算信息")
    @RequestMapping(value = "/settlement/info", method = RequestMethod.GET)
    public CommonResult<MerchantSettlementInfoVo> getSettlementInfo() {
        return CommonResult.success(merchantService.getSettlementInfo());
    }

    @PreAuthorize("hasAuthority('merchant:config:info:edit')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户端商户配置信息编辑")
    @ApiOperation(value = "商户端商户配置信息编辑")
    @RequestMapping(value = "/config/info/edit", method = RequestMethod.POST)
    public CommonResult<String> configInfoEdit(@RequestBody @Validated MerchantConfigInfoVo request) {
        if (merchantService.configInfoEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:settlement:info:edit')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户端商户结算信息编辑")
    @ApiOperation(value = "商户端商户结算信息编辑")
    @RequestMapping(value = "/settlement/info/edit", method = RequestMethod.POST)
    public CommonResult<String> settlementInfoEdit(@RequestBody @Validated MerchantSettlementInfoVo request) {
        if (merchantService.settlementInfoEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:switch:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户端商户开关")
    @ApiOperation(value = "商户端商户开关")
    @RequestMapping(value = "/switch/update", method = RequestMethod.POST)
    public CommonResult<String> updateSwitch() {
        if (merchantService.updateSwitch()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



