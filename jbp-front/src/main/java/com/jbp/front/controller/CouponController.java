package com.jbp.front.controller;

import com.github.pagehelper.PageInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.CouponFrontSearchRequest;
import com.jbp.common.request.OrderUseCouponRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.CouponFrontResponse;
import com.jbp.common.response.CouponUserOrderResponse;
import com.jbp.common.response.UserCouponResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.CouponService;
import com.jbp.service.service.CouponUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 优惠券表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/coupon")
@Api(tags = "优惠券控制器")
public class CouponController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;


    @ApiOperation(value = "优惠券分页列表")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    public CommonResult<PageInfo<CouponFrontResponse>> getList(@ModelAttribute @Validated CouponFrontSearchRequest request,
                                                               @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(couponService.getH5List(request, pageParamRequest));
    }

    @ApiOperation(value = "当前订单可用优惠券")
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public CommonResult<List<CouponUserOrderResponse>> getCouponsListByPreOrderNo(@ModelAttribute @Validated OrderUseCouponRequest request) {
        return CommonResult.success(couponUserService.getListByPreOrderNo(request));
    }

    @ApiOperation(value = "我的优惠券")
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型，usable-可用，unusable-不可用", required = true),
            @ApiImplicitParam(name="page", value="页码", required = true),
            @ApiImplicitParam(name="limit", value="每页数量", required = true)
    })
    public CommonResult<CommonPage<UserCouponResponse>> getList(@RequestParam(value = "type") String type,
                                                                @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(couponUserService.getMyCouponList(type, pageParamRequest)));
    }

    @ApiOperation(value = "领券")
    @RequestMapping(value = "/receive/{id}", method = RequestMethod.POST)
    public CommonResult<String> receive(@PathVariable(value = "id") Integer id) {
        if (couponUserService.receiveCoupon(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



