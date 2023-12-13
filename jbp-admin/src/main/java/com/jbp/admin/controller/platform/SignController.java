package com.jbp.admin.controller.platform;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SignConfigRequest;
import com.jbp.common.response.SignConfigResponse;
import com.jbp.common.response.UserSignRecordResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SignService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 签到控制器
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
@RequestMapping("api/admin/platform/sign")
@Api(tags = "签到控制器")
public class SignController {

    @Autowired
    private SignService signService;

    @PreAuthorize("hasAuthority('platform:sign:get:config')")
    @ApiOperation(value = "获取签到配置")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public CommonResult<SignConfigResponse> getConfig() {
        return CommonResult.success(signService.getConfig());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增连续签到配置")
    @PreAuthorize("hasAuthority('platform:sign:add:config')")
    @ApiOperation(value = "新增连续签到配置")
    @RequestMapping(value = "/add/config", method = RequestMethod.POST)
    public CommonResult<String> addConfig(@RequestBody @Validated SignConfigRequest request) {
        if (signService.addConfig(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除连续签到配置")
    @PreAuthorize("hasAuthority('platform:sign:delete:config')")
    @ApiOperation(value = "删除连续签到配置")
    @RequestMapping(value = "/delete/config/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (signService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑基础签到配置")
    @PreAuthorize("hasAuthority('platform:sign:edit:base:config')")
    @ApiOperation(value = "编辑基础签到配置")
    @RequestMapping(value = "/edit/base/config", method = RequestMethod.POST)
    public CommonResult<String> editBaseConfig(@RequestBody @Validated SignConfigRequest request) {
        if (signService.editBaseConfig(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑连续签到配置")
    @PreAuthorize("hasAuthority('platform:sign:edit:award:config')")
    @ApiOperation(value = "编辑连续签到配置")
    @RequestMapping(value = "/edit/award/config", method = RequestMethod.POST)
    public CommonResult<String> editAwardConfig(@RequestBody @Validated SignConfigRequest request) {
        if (signService.editAwardConfig(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:sign:user:record:list')")
    @ApiOperation(value = "用户签到记录分页列表") //配合swagger使用
    @RequestMapping(value = "/user/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignRecordResponse>> getSignRecordList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(signService.getSignRecordList(pageParamRequest)));
    }
}



