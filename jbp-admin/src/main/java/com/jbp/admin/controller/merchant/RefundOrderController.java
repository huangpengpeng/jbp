package com.jbp.admin.controller.merchant;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.MerchantRefundOrderPageResponse;
import com.jbp.common.response.RefundOrderAdminDetailResponse;
import com.jbp.common.response.RefundOrderCountItemResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.RefundOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户侧退款订单控制器
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
@RequestMapping("api/admin/merchant/refund/order")
@Api(tags = "商户侧退款订单控制器") //配合swagger使用
public class RefundOrderController {

    @Autowired
    private RefundOrderService refundOrderService;

    @PreAuthorize("hasAuthority('merchant:refund:order:page:list')")
    @ApiOperation(value = "商户端退款订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantRefundOrderPageResponse>> getList(@Validated RefundOrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(refundOrderService.getMerchantAdminPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:refund:order:detail')")
    @ApiOperation(value = "商户端退款订单详情") //配合swagger使用
    @RequestMapping(value = "/detail/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<RefundOrderAdminDetailResponse> getDetail(@PathVariable(value = "refundOrderNo") String refundOrderNo) {
        return CommonResult.success(refundOrderService.getMerchantDetail(refundOrderNo));
    }

    @PreAuthorize("hasAuthority('merchant:refund:order:status:num')")
    @ApiOperation(value = "商户端获取退款订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<RefundOrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(refundOrderService.getMerchantOrderStatusNum(dateLimit));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户备注退款订单")
    @PreAuthorize("hasAuthority('merchant:refund:order:mark')")
    @ApiOperation(value = "商户备注退款订单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated RefundOrderRemarkRequest request) {
        if (refundOrderService.mark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

//    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "订单退款")
//    @PreAuthorize("hasAuthority('merchant:refund:order:refund')")
//    @ApiOperation(value = "订单退款")
//    @RequestMapping(value = "/refund", method = RequestMethod.GET)
//    public CommonResult<String> refund(@ModelAttribute @Validated OrderRefundAuditRequest request) {
//        if (refundOrderService.refund(request)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }
//
//    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "订单拒绝退款")
//    @PreAuthorize("hasAuthority('merchant:refund:order:refund:refuse')")
//    @ApiOperation(value = "拒绝退款")
//    @RequestMapping(value = "/refund/refuse", method = RequestMethod.GET)
//    public CommonResult<String> refundRefuse(@ModelAttribute @Validated OrderRefundAuditRequest request) {
//        if (refundOrderService.refundRefuse(request)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单审核")
    @PreAuthorize("hasAuthority('merchant:refund:order:audit')")
    @ApiOperation(value = "退款单审核")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated OrderRefundAuditRequest request) {
        if (refundOrderService.audit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单收到退货")
    @PreAuthorize("hasAuthority('merchant:refund:order:receiving')")
    @ApiOperation(value = "退款单收到退货")
    @RequestMapping(value = "/receiving/{refundOrderNo}", method = RequestMethod.POST)
    public CommonResult<String> receiving(@PathVariable(value = "refundOrderNo") String refundOrderNo) {
        if (refundOrderService.receiving(refundOrderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "退款单拒绝收货")
    @PreAuthorize("hasAuthority('merchant:refund:order:receiving:reject')")
    @ApiOperation(value = "退款单拒绝收货")
    @RequestMapping(value = "/receiving/reject", method = RequestMethod.POST)
    public CommonResult<String> receivingReject(@RequestBody @Validated RejectReceivingRequest request) {
        if (refundOrderService.receivingReject(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



