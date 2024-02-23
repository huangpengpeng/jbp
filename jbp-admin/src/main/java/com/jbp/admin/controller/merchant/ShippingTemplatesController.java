package com.jbp.admin.controller.merchant;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.express.ShippingTemplates;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ShippingTemplatesRequest;
import com.jbp.common.request.ShippingTemplatesSearchRequest;
import com.jbp.common.response.ShippingTemplatesInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ShippingTemplatesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 物流-模板控制器
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
@RequestMapping("api/admin/merchant/shipping/templates")
@Api(tags = "商户端 -- 运费模板")
public class ShippingTemplatesController {

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    @PreAuthorize("hasAuthority('merchant:shipping:templates:list')")
    @ApiOperation(value = "运费模板分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShippingTemplates>> getList(@Validated ShippingTemplatesSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<ShippingTemplates> shippingTemplatesCommonPage = CommonPage.restPage(shippingTemplatesService.getList(request, pageParamRequest));
        return CommonResult.success(shippingTemplatesCommonPage);
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增运费模板")
    @PreAuthorize("hasAuthority('merchant:shipping:templates:save')")
    @ApiOperation(value = "新增运费模板")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ShippingTemplatesRequest request) {
        if (shippingTemplatesService.create(request)) {
            return CommonResult.success("新增运费模板成功");
        }
        return CommonResult.failed("新增运费模板失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除运费模板")
    @PreAuthorize("hasAuthority('merchant:shipping:templates:delete')")
    @ApiOperation(value = "删除运费模板")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (shippingTemplatesService.remove(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "运费模板修改")
    @PreAuthorize("hasAuthority('merchant:shipping:templates:update')")
    @ApiOperation(value = "运费模板修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ShippingTemplatesRequest request) {
        if (shippingTemplatesService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "运费模板详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ShippingTemplatesInfoResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(shippingTemplatesService.getInfo(id));
    }
}



