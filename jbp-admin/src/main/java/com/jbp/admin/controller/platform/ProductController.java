package com.jbp.admin.controller.platform;

import cn.hutool.core.util.StrUtil;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.enums.MethodType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * 平台端商品控制器
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
@RequestMapping("api/admin/platform/product")
@Api(tags = "平台端商品控制器") //配合swagger使用
public class ProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('platform:product:page:list')")
    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformProductListResponse>> getList(@Validated ProductSearchRequest request,
                                                                         @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getPlatformPageList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:product:list:ids')")
    @ApiOperation(value = "根据商品id集合查询商品列表") //配合swagger使用
    @RequestMapping(value = "/listbyids/{ids}", method = RequestMethod.GET)
    public CommonResult<List<PlatformProductListResponse>> getListByIds(@PathVariable(value = "ids") List<String> ids) {
        return CommonResult.success(productService.getPlatformListForIdsByLimit(ids));
    }

    @PreAuthorize("hasAuthority('platform:product:tabs:headers')")
    @ApiOperation(value = "商品表头数量")
    @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
    public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader() {
        return CommonResult.success(productService.getPlatformTabsHeader());
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品审核")
    @PreAuthorize("hasAuthority('platform:product:audit')")
    @ApiOperation(value = "商品审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated ProductAuditRequest request) {
        if (productService.audit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "强制下架商品")
    @PreAuthorize("hasAuthority('platform:product:force:down')")
    @ApiOperation(value = "强制下架商品")
    @RequestMapping(value = "/force/down", method = RequestMethod.POST)
    public CommonResult<String> forceDown(@RequestBody @Validated ProductForceDownRequest request) {
        if (productService.forceDown(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "强制下架商品")
    @PreAuthorize("hasAuthority('platform:product:force:down')")
    @ApiOperation(value = "强制上架架商品")
    @RequestMapping(value = "/force/up", method = RequestMethod.POST)
    public CommonResult<String> forceUp(@RequestBody @Validated ProductForceDownRequest request) {
        if (productService.forceUp(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "平台端商品编辑")
    @PreAuthorize("hasAuthority('platform:product:update')")
    @ApiOperation(value = "商品编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> platUpdate(@RequestBody @Validated ProductPlatUpdateRequest request) {
        if (productService.platUpdate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(productService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('platform:product:activity:search:page')")
    @ApiOperation(value = "商品搜索分页列表（活动）")
    @RequestMapping(value = "/activity/search/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductActivityResponse>> getActivitySearchPage(
            @Validated ProductActivitySearchRequest request, @Validated PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getActivitySearchPage(request, pageRequest)));
    }
    @PreAuthorize("hasAuthority('platform:product:copy')")
    @ApiOperation(value = "复制")
    @GetMapping("/copy/{productId}")
    public CommonResult copy(@PathVariable("productId") Integer productId) {
        productService.copy(productId);
        return CommonResult.success();
    }
}



