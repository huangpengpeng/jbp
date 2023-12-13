package com.jbp.admin.controller.platform;

import com.jbp.service.service.OnePassSmsService;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SmsApplyTempRequest;
import com.jbp.common.request.SmsModifySignRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.OnePassSmsTempsListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 一号通短信服务控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/one/pass/sms")
@Api(tags = "一号通短信服务控制器")
public class OnePassSmsController {

    @Autowired
    private OnePassSmsService onePassSmsService;

    @PreAuthorize("hasAuthority('platform:one:pass:sms:modify:sign')")
    @ApiOperation(value = "修改签名")
    @RequestMapping(value = "/modify/sign", method = RequestMethod.POST)
    public CommonResult<String> modifySign(@RequestBody @Validated SmsModifySignRequest request) {
        if (onePassSmsService.modifySign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:sms:temps')")
    @ApiOperation(value = "短信模板")
    @RequestMapping(value = "/temps", method = RequestMethod.GET)
    public CommonResult<OnePassSmsTempsListVo> temps(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(onePassSmsService.temps(pageParamRequest));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:sms:temp:apply')")
    @ApiOperation(value = "申请短信模板")
    @RequestMapping(value = "/temp/apply", method = RequestMethod.POST)
    public CommonResult<String> applyTempMessage(@RequestBody @Validated SmsApplyTempRequest request) {
        if (onePassSmsService.applyTempMessage(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

}
