package com.jbp.front.controller;

import com.jbp.common.request.PreOrderRequest;
import com.jbp.common.response.OrderNoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.OrderScanPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/front/order/scan/pay")
@Api(tags = "订单")
public class OrderScanPayController {

    @Resource
    private OrderScanPayService orderScanPayService;

    @ApiOperation(value = "扫码支付下单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<OrderNoResponse> create(@RequestBody @Validated PreOrderRequest request) {
        return CommonResult.success();
    }
}
