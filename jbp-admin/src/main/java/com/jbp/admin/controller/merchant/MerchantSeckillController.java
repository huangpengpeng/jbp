package com.jbp.admin.controller.merchant;

import com.jbp.admin.service.SeckillService;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.SeckillActivityDetailResponse;
import com.jbp.common.response.SeckillActivityPageResponse;
import com.jbp.common.response.SeckillProductPageResponse;
import com.jbp.common.result.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户端秒杀控制器
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
@RequestMapping("api/admin/merchant/seckill")
@Api(tags = "商户端 - 秒杀管理")
public class MerchantSeckillController {

    @Autowired
    private SeckillService seckillService;

    @PreAuthorize("hasAuthority('merchant:seckill:activity:page')")
    @ApiOperation(value = "秒杀活动分页列表")
    @RequestMapping(value = "/activity/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SeckillActivityPageResponse>> activityPage(@Validated SeckillActivitySearchRequest request,
                                                                              @Validated PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(seckillService.activityPage(request, pageRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:seckill:activity:detail')")
    @ApiOperation(value = "秒杀活动详情")
    @RequestMapping(value = "/activity/detail/{id}", method = RequestMethod.GET)
    public CommonResult<SeckillActivityDetailResponse> activityDetail(@PathVariable("id") Integer id) {
        return CommonResult.success(seckillService.activityDetail(id));
    }

    @PreAuthorize("hasAuthority('merchant:seckill:product:list')")
    @ApiOperation(value = "秒杀商品列表")
    @RequestMapping(value = "/product/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SeckillProductPageResponse>> getSeckillProductPage(@Validated SeckillProductSearchRequest request,
                                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(seckillService.getSeckillProductPage(request, pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品撤回审核")
    @PreAuthorize("hasAuthority('merchant:seckill:product:withdraw')")
    @ApiOperation(value = "秒杀商品撤回审核")
    @RequestMapping(value = "/product/withdraw/{id}", method = RequestMethod.POST)
    public CommonResult<String> withdrawProductAudit(@PathVariable("id") Integer id) {
        if (seckillService.withdrawProductAudit(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品设置活动价")
    @PreAuthorize("hasAuthority('merchant:seckill:product:price')")
    @ApiOperation(value = "秒杀商品设置活动价")
    @RequestMapping(value = "/product/set/price", method = RequestMethod.POST)
    public CommonResult<String> setProductPrice(@RequestBody @Validated SeckillProductPriceRequest request) {
        if (seckillService.setProductPrice(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品上架")
    @PreAuthorize("hasAuthority('merchant:seckill:product:up')")
    @ApiOperation(value = "秒杀商品上架")
    @RequestMapping(value = "/product/up", method = RequestMethod.POST)
    public CommonResult<String> upProduct(@RequestBody @Validated SeckillProductBatchRequest request) {
        if (seckillService.upProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "秒杀商品下架")
    @PreAuthorize("hasAuthority('merchant:seckill:product:down')")
    @ApiOperation(value = "秒杀商品下架")
    @RequestMapping(value = "/product/down", method = RequestMethod.POST)
    public CommonResult<String> downProduct(@RequestBody @Validated SeckillProductBatchRequest request) {
        if (seckillService.downProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "秒杀商品删除")
    @PreAuthorize("hasAuthority('merchant:seckill:product:delete')")
    @ApiOperation(value = "秒杀商品删除")
    @RequestMapping(value = "/product/delete", method = RequestMethod.POST)
    public CommonResult<String> deleteProduct(@RequestBody @Validated SeckillProductBatchRequest request) {
        if (seckillService.merchantDeleteProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商户秒杀商品添加")
    @PreAuthorize("hasAuthority('merchant:seckill:product:add')")
    @ApiOperation(value = "秒杀商品添加")
    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public CommonResult<String> addProduct(@RequestBody @Validated SeckillProductAddRequest request) {
        if (seckillService.merchantAddProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



