package com.jbp.admin.controller.platform;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.CouponAdminDetailResponse;
import com.jbp.common.response.CouponPageResponse;
import com.jbp.common.response.CouponUserResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.CouponService;
import com.jbp.service.service.CouponUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端优惠券控制器
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
@RequestMapping("api/admin/platform/coupon")
@Api(tags = "平台端优惠券管理")
public class PlatformCouponController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;

    @PreAuthorize("hasAuthority('platform:coupon:page:list')")
    @ApiOperation(value = "优惠券分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CouponPageResponse>> getList(@Validated CouponSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(couponService.getPlatformPageList(request)));
    }

    @PreAuthorize("hasAuthority('platform:coupon:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加平台优惠券")
    @ApiOperation(value = "添加优惠券")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated CouponAddRequest request) {
        if (!couponService.platformAdd(request)) {
            return CommonResult.failed("添加优惠券失败");
        }
        return CommonResult.success("添加优惠券成功");
    }

    @PreAuthorize("hasAuthority('platform:coupon:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除平台优惠券")
    @ApiOperation(value = "删除优惠券")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated CouponDeleteRequest request) {
        if (!couponService.platformDelete(request)) {
            return CommonResult.failed("删除优惠券失败");
        }
        return CommonResult.success("删除优惠券成功");
    }

    @PreAuthorize("hasAuthority('platform:coupon:switch')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "切换优惠券开关")
    @ApiOperation(value = "优惠券开关")
    @RequestMapping(value = "/switch/{id}", method = RequestMethod.POST)
    public CommonResult<String> couponSwitch(@PathVariable(value = "id") Integer id) {
        if (!couponService.switchById(id)) {
            return CommonResult.failed("变更优惠券开关失败");
        }
        return CommonResult.success("变更优惠券开关成功");
    }

    @PreAuthorize("hasAuthority('platform:coupon:detail')")
    @ApiOperation(value = "优惠券详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<CouponAdminDetailResponse> getDetail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(couponService.getPlatformDetail(id));
    }

    @PreAuthorize("hasAuthority('platform:coupon:batch:send')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "平台批量发送优惠券")
    @ApiOperation(value = "批量发送优惠券")
    @RequestMapping(value = "/batch/send", method = RequestMethod.POST)
    public CommonResult<String> batchSend(@RequestBody @Validated CouponBatchSendRequest request) {
        if (!couponService.platformBatchSend(request)) {
            return CommonResult.failed("发送优惠券失败");
        }
        return CommonResult.success("发送优惠券成功");
    }

    @PreAuthorize("hasAuthority('platform:coupon:can:send:list')")
    @ApiOperation(value = "可发送优惠券列表")
    @RequestMapping(value = "/can/send/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Coupon>> canSendList(@Validated CommonSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(couponService.getPlatformCanSendList(request)));
    }

    @PreAuthorize("hasAuthority('platform:coupon:user:page:list')")
    @ApiOperation(value = "优惠券领取记录分页列表")
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CouponUserResponse>> getList(@Validated CouponUserSearchRequest request,
                                                                @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(couponUserService.getPlatformList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:coupon:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "优惠券编辑")
    @ApiOperation(value = "优惠券编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody CouponUpdateRequest request) {
        if (!couponService.edit(request)) {
            return CommonResult.failed("优惠券编辑失败");
        }
        return CommonResult.success("优惠券编辑成功");
    }
}
