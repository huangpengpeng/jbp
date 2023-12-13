package com.jbp.front.controller;


import com.github.pagehelper.PageInfo;
import com.jbp.front.service.FrontProductService;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.result.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
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
@RequestMapping("api/front/product")
@Api(tags = "商品控制器")
public class ProductController {

    @Autowired
    private FrontProductService productService;

    @ApiOperation(value = "商品分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> getList(@ModelAttribute @Validated ProductFrontSearchRequest request,
                                                                  @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getList(request, pageParamRequest)));
    }

    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<ProductDetailResponse> getDetail(@Validated @PathVariable Integer id,
                                                         @RequestParam(value = "type", required = false) String type) {
        return CommonResult.success(productService.getDetail(id,type));
    }

    @ApiOperation(value = "商品评论列表")
    @RequestMapping(value = "/reply/list/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "评价等级|0=全部,1=好评,2=中评,3=差评", allowableValues = "range[0,1,2,3]")
    public CommonResult<CommonPage<ProductReplyResponse>> getReplyList(@PathVariable Integer id,
                                                                       @RequestParam(value = "type") Integer type, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getReplyList(id, type, pageParamRequest)));
    }

    @ApiOperation(value = "商品评论数量")
    @RequestMapping(value = "/reply/config/{id}", method = RequestMethod.GET)
    public CommonResult<ProductReplayCountResponse> getReplyCount(@PathVariable Integer id) {
        return CommonResult.success(productService.getReplyCount(id));
    }

    @ApiOperation(value = "商品详情评论")
    @RequestMapping(value = "/reply/detail/{id}", method = RequestMethod.GET)
    public CommonResult<ProductDetailReplyResponse> getProductReply(@PathVariable Integer id) {
        return CommonResult.success(productService.getProductReply(id));
    }

//    /**
//     * 商品规格详情
//     */
//    @ApiOperation(value = "商品规格详情")
//    @RequestMapping(value = "/sku/detail/{id}", method = RequestMethod.GET)
//    public CommonResult<ProductDetailResponse> getSkuDetail(@PathVariable Integer id) {
//        return CommonResult.success(productService.getSkuDetail(id));
//    }
//
//    /**
//     * 商品排行榜
//     */
//    @ApiOperation(value = "商品排行榜")
//    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
//    public CommonResult<List<StoreProduct>> getLeaderboard() {
//        return CommonResult.success(productService.getLeaderboard());
//    }

    @ApiOperation(value = "商户商品列表")
    @RequestMapping(value = "/merchant/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductCommonResponse>> getMerchantProList(@ModelAttribute @Validated MerchantProductSearchRequest request,
                                                                              @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getMerchantProList(request, pageParamRequest)));
    }

    @ApiOperation(value = "优惠券商品列表")
    @RequestMapping(value = "/coupon/pro/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "商品名称"),
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true)
    })
    public CommonResult<PageInfo<ProductFrontResponse>> getCouponProList(@ModelAttribute @Validated CouponProductSearchRequest request,
                                                                         @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(productService.getCouponProList(request, pageParamRequest));
    }
}



