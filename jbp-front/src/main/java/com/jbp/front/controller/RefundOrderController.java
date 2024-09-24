package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.request.agent.OrderRefundSuccessFrontRequest;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.common.response.RefundOrderResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.front.service.FrontOrderService;

import com.jbp.service.service.RefundOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端订单操作
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
@RequestMapping("api/front/refund")
@Api(tags = "售后订单控制器")
public class RefundOrderController {

    @Autowired
    private FrontOrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    @ApiOperation(value = "售后申请列表(可申请售后列表)")
    @RequestMapping(value = "/after/sale/apply/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<OrderDetail>> afterSaleApplyList(@ModelAttribute CommonSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.getAfterSaleApplyList(request)));
    }

    @ApiOperation(value = "订单退款申请")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public CommonResult<Boolean> refundApply(@RequestBody @Validated OrderRefundApplyRequest request) {
        if(orderService.refundApply(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "订单退款理由（平台提供）")
    @RequestMapping(value = "/reason", method = RequestMethod.GET)
    public CommonResult<List<String>> refundReason() {
        return CommonResult.success(orderService.getRefundReason());
    }

    @ApiOperation(value = "退款订单详情")
    @RequestMapping(value = "/detail/{refundOrderNo}", method = RequestMethod.GET)
    public CommonResult<RefundOrderInfoResponse> refundOrderDetail(@PathVariable String refundOrderNo) {
        return CommonResult.success(orderService.refundOrderDetail(refundOrderNo));
    }

    @ApiOperation(value = "退款订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RefundOrderResponse>> refundOrderList(@ModelAttribute @Validated OrderAfterSalesSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(orderService.getRefundOrderList(request)));
    }

    @ApiOperation(value = "退款单退回商品")
    @RequestMapping(value = "/returning/goods", method = RequestMethod.POST)
    public CommonResult<Boolean> returningGoods(@RequestBody @Validated OrderRefundReturningGoodsRequest request) {
        if(orderService.returningGoods(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "撤销退款单")
    @RequestMapping(value = "/revoke/{refundOrderNo}", method = RequestMethod.POST)
    public CommonResult<Boolean> revoke(@PathVariable String refundOrderNo) {
        if(orderService.revoke(refundOrderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "退款单审核成功")
    @RequestMapping(value = "/success", method = RequestMethod.POST)
    public CommonResult<String> audit(@RequestBody @Validated OrderRefundSuccessFrontRequest request) {
        RefundOrder refundOrder = refundOrderService.getOne(new QueryWrapper<RefundOrder>().lambda().eq(RefundOrder::getOrderNo, request.getOrderNo()).orderByDesc(RefundOrder::getId).last("limit 1"));
        if(refundOrder == null) {
            return CommonResult.failed("退款失败！");
        }
        OrderRefundAuditRequest orderRefundAuditRequest = new OrderRefundAuditRequest();
        orderRefundAuditRequest.setRefundOrderNo(refundOrder.getRefundOrderNo());
        orderRefundAuditRequest.setAuditType("success");
        if (refundOrderService.audit(orderRefundAuditRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
