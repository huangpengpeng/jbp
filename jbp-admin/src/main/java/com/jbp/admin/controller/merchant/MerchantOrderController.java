package com.jbp.admin.controller.merchant;

import cn.hutool.core.collection.CollUtil;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.express.Express;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.MerchantOrderPageResponse;
import com.jbp.common.response.OrderAdminDetailResponse;
import com.jbp.common.response.OrderCountItemResponse;
import com.jbp.common.response.OrderInvoiceResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.LogisticsResultVo;
import com.jbp.common.vo.OrderShippingExcelVo;
import com.jbp.service.service.ExpressService;
import com.jbp.service.service.OrderService;

import com.jbp.service.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商户侧订单控制器
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
@RequestMapping("api/admin/merchant/order")
@Api(tags = "商户侧订单控制器") //配合swagger使用
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ExpressService expressService;

    @Resource
    private PayService payService;
    @PreAuthorize("hasAuthority('merchant:order:page:list')")
    @ApiOperation(value = "商户端订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantOrderPageResponse>> getList(@Validated OrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(orderService.getMerchantAdminPage(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:order:status:num')")
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(orderService.getMerchantOrderStatusNum(dateLimit));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "商户删除订单")
    @PreAuthorize("hasAuthority('merchant:order:delete')")
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete/{orderNo}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(name = "orderNo") String orderNo) {
        orderNo = orderService.getOrderNo(orderNo);
        if (orderService.merchantDeleteByOrderNo(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户备注订单")
    @PreAuthorize("hasAuthority('merchant:order:mark')")
    @ApiOperation(value = "商户备注订单")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestBody @Validated OrderRemarkRequest request) {
        request.setOrderNo(orderService.getOrderNo(request.getOrderNo()));
        if (orderService.merchantMark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:order:info')")
    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/info/{orderNo}", method = RequestMethod.GET)
    public CommonResult<OrderAdminDetailResponse> info(@PathVariable(value = "orderNo") String orderNo) {
        orderNo = orderService.getOrderNo(orderNo);
        return CommonResult.success(orderService.adminDetail(orderNo));
    }

    @PreAuthorize("hasAuthority('merchant:order:detail:list')")
    @ApiOperation(value = "订单细节详情列表（发货使用）")
    @RequestMapping(value = "/{orderNo}/detail/list", method = RequestMethod.GET)
    public CommonResult<List<OrderDetail>> getDetailList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.getDetailList(orderService.getOrderNo(orderNo)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "订单发货")
    @PreAuthorize("hasAuthority('merchant:order:send')")
    @ApiOperation(value = "订单发货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated OrderSendRequest request) {
        request.setOrderNo(orderService.getOrderNo(request.getOrderNo()));
        if (orderService.send(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "小票打印")

    @ApiOperation(value = "小票打印")
    @RequestMapping(value = "/printreceipt/{orderno}", method = RequestMethod.GET)
    public CommonResult<Boolean> printReceipt(@PathVariable(value = "orderno") String orderno) {
        Order order = orderService.getByPlatOrderNo(orderno).get(0);
        orderno = order.getOrderNo();
        orderService.printReceipt(orderno);
        return CommonResult.success();
    }


    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        Order order = orderService.getByPlatOrderNo(orderNo).get(0);
        orderNo = order.getOrderNo();
        return CommonResult.success(orderService.getInvoiceListByMerchant(orderNo));
    }


    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(orderService.getLogisticsInfoByMerchant(invoiceId));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "核销码核销订单")
    @PreAuthorize("hasAuthority('merchant:order:verification')")
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/verification", method = RequestMethod.POST)
    public CommonResult<Object> verificationOrder(@RequestBody @Validated OrderVerificationRequest request) {
        return CommonResult.success(orderService.verificationOrderByCode(request.getVerifyCode()));
    }
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商户端确认支付")
    @PreAuthorize("hasAuthority('merchant:order:confirm:pay')")
    @ApiOperation(value = "确认支付")
    @GetMapping(value = "/confirm/pay")
    public CommonResult confirmPay(String orderNo) {
        payService.confirmPay(orderService.getByOrderNo(orderNo));
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:export:order:import')")
    @ApiOperation(value = "导入发货订单Excel")
    @PostMapping(value = "/order/importSend")
    public CommonResult importSend(@RequestBody @Validated List<OrderShippingExcelVo> request){
        if(CollUtil.isEmpty(request)){
            return CommonResult.failed("导入发货数据不能为空");
        }
        for (OrderShippingExcelVo orderShippingExcelVo : request) {
            orderShippingExcelVo.setOrderNo(orderService.getOrderNo(orderShippingExcelVo.getOrderNo()));
        }
        Set<Integer> orderDetailIdSet = request.stream().map(OrderShippingExcelVo::getOrderDetailId).collect(Collectors.toSet());
        if (orderDetailIdSet.size() != request.size()) {
            return CommonResult.failed("导入发货数据重复");
        }
        // 按订单归档统一
        Map<String, List<OrderShippingExcelVo>> shippingListMap = FunctionUtil.valueMap(request, OrderShippingExcelVo::getOrderNo);
        Set<String> orderNoSet = shippingListMap.keySet();
        for (OrderShippingExcelVo vo : request) {
            if("虚拟发货".equals(vo.getDeliveryTypeName()) && !"0000".equals(vo.getExpressNumber())){
                return CommonResult.failed("虚拟发货单号请固定填写:0000");
            }
        }

        Map<String, Express> expressMap = Maps.newConcurrentMap();
        List<OrderSendRequest> sendRequestList = Lists.newArrayList();
        for (String s : orderNoSet) {
            // 单号分组
            List<OrderShippingExcelVo> shippingVos = shippingListMap.get(s);
            // 物流单号分组
            Map<String, List<OrderShippingExcelVo>> shippingNoListMap = FunctionUtil.valueMap(shippingVos, OrderShippingExcelVo::getExpressNumber);
            shippingNoListMap.forEach((k, v) -> {
                List<SplitOrderSendDetailRequest> detailList = Lists.newArrayList();
                for (OrderShippingExcelVo shippingVo : v) {
                    SplitOrderSendDetailRequest splitDetail = new SplitOrderSendDetailRequest(shippingVo.getOrderDetailId(), shippingVo.getNum());
                    detailList.add(splitDetail);
                }
                OrderShippingExcelVo vo = v.get(0);
                OrderSendRequest orderSend = new OrderSendRequest();
                orderSend.setOrderNo(s);
                orderSend.setDeliveryType(vo.getDeliveryTypeName().equals("快递") ? "express" : "fictitious");
                if (StringUtils.equals("express", orderSend.getDeliveryType())) {
                    Express express = expressMap.get(vo.getExpressName());
                    if (express == null) {
                        express = expressService.getByName(vo.getExpressName());
                        expressMap.put(vo.getExpressName(), express);
                    }
                    if (express == null) {
                        throw new CrmebException("快递名称不存在");
                    }
                    orderSend.setExpressCode(express.getCode());
                }
                orderSend.setExpressNumber(k);
                orderSend.setExpressRecordType(1);
                orderSend.setIsSplit(true);
                orderSend.setDetailList(detailList);
                sendRequestList.add(orderSend);
            });
        }
        orderService.batchSend(sendRequestList);
        return CommonResult.success();
    }
}



