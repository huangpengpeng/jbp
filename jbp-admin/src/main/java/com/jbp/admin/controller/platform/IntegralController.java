package com.jbp.admin.controller.platform;

import com.jbp.admin.service.IntegralService;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IntegralConfigResponse;
import com.jbp.common.response.IntegralRecordPageResponse;
import com.jbp.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 积分控制器
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
@RequestMapping("api/admin/platform/integral")
@Api(tags = "积分控制器")
public class IntegralController {

    @Autowired
    private IntegralService integralService;

    @PreAuthorize("hasAuthority('platform:integral:get:config')")
    @ApiOperation(value = "获取积分配置")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public CommonResult<IntegralConfigResponse> getConfig() {
        return CommonResult.success(integralService.getConfig());
    }

    @PreAuthorize("hasAuthority('platform:integral:update:config')")
    @ApiOperation(value = "编辑积分配置")
    @RequestMapping(value = "/set/config", method = RequestMethod.POST)
    public CommonResult<String> updateConfig(@RequestBody IntegralConfigResponse request) {
        if (integralService.updateConfig(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:integral:page:list')")
    @ApiOperation(value = "积分分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<IntegralRecordPageResponse>> findRecordPageList(@ModelAttribute @Validated IntegralPageSearchRequest request,
                                                                                   @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(integralService.findRecordPageList(request, pageParamRequest)));
    }
}
