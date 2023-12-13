package com.jbp.admin.controller.platform;

import com.jbp.common.request.*;
import com.jbp.common.response.OnePassLoginResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.OnePassMealCodeVo;
import com.jbp.common.vo.OnePassMealListVo;
import com.jbp.common.vo.OnePassRecordListVo;
import com.jbp.common.vo.OnePassUserInfoVo;
import com.jbp.service.service.OnePassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 一号通控制器
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
@RequestMapping("api/admin/platform/one/pass")
@Api(tags = "一号通控制器")
public class OnePassController {

    @Autowired
    private OnePassService onePassService;

    @PreAuthorize("hasAuthority('platform:one:pass:send:code')")
    @ApiOperation(value = "获取用户验证码")
    @RequestMapping(value = "/sendUserCode", method = RequestMethod.POST)
    public CommonResult<Object> sendUserCode(@RequestBody @Validated OnePassUserCodeRequest request) {
        return CommonResult.success(onePassService.sendUserCode(request));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:register')")
    @ApiOperation(value = "账号注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<String> register(@Validated @RequestBody OnePassRegisterRequest registerRequest) {
        return CommonResult.success(onePassService.register(registerRequest));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:login')")
    @ApiOperation(value = "一号通用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<OnePassLoginResponse> account(@Validated @RequestBody OnePassLoginRequest request) {
        return CommonResult.success(onePassService.login(request));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:is:login')")
    @ApiOperation(value = "是否已经登录")
    @RequestMapping(value = "/isLogin", method = RequestMethod.GET)
    public CommonResult<OnePassLoginResponse> isLogin() {
        return CommonResult.success(onePassService.isLogin());
    }

    @PreAuthorize("hasAuthority('platform:one:pass:info')")
    @ApiOperation(value = "一号通用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<OnePassUserInfoVo> getInfo() {
        return CommonResult.success(onePassService.info());
    }

    @PreAuthorize("hasAuthority('platform:one:pass:logout')")
    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public CommonResult<String> logOut() {
        if (onePassService.logOut()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:update:password')")
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    public CommonResult<String> updatePassword(@Validated @RequestBody OnePassUpdateRequest request) {
        if (onePassService.updatePassword(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:update:phone:validator')")
    @ApiOperation(value = "修改手机号——验证账号密码")
    @RequestMapping(value = "/update/phone/validator", method = RequestMethod.POST)
    public CommonResult<String> updatePhone(@Validated @RequestBody OnePassLoginRequest request) {
        if (onePassService.beforeUpdatePhoneValidator(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:update:phone')")
    @ApiOperation(value = "修改手机号")
    @RequestMapping(value = "/update/phone", method = RequestMethod.POST)
    public CommonResult<String> updatePhone(@Validated @RequestBody OnePassUpdateRequest request) {
        if (onePassService.updatePhone(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:meal:list')")
    @ApiOperation(value = "套餐列表")
    @RequestMapping(value = "/meal/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="type", value="套餐类型：sms,短信；expr_query,物流查询；expr_dump,电子面单；copy,产品复制")
    public CommonResult<OnePassMealListVo> mealList(@Validated @RequestParam String type) {
        return CommonResult.success(onePassService.mealList(type));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:meal:code')")
    @ApiOperation(value = "套餐购买")
    @RequestMapping(value = "/meal/code", method = RequestMethod.POST)
    public CommonResult<OnePassMealCodeVo> mealCode(@RequestBody @Validated OnePassMealCodeRequest request) {
        return CommonResult.success(onePassService.mealCode(request));
    }

    @PreAuthorize("hasAuthority('platform:one:pass:service:open')")
    @ApiOperation(value = "服务开通")
    @RequestMapping(value = "/service/open", method = RequestMethod.POST)
    public CommonResult<String> serviceOpen(@RequestBody @Validated OnePassServiceOpenRequest request) {
        if (onePassService.serviceOpen(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:one:pass:user:record')")
    @ApiOperation(value = "用量记录")
    @RequestMapping(value = "/user/record", method = RequestMethod.GET)
    public CommonResult<OnePassRecordListVo> record(@Validated OnePassUserRecordRequest request) {
        return CommonResult.success(onePassService.userRecord(request));
    }
}
