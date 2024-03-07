package com.jbp.admin.controller.agent;

import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrderProductProfitRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.OrderProductProfitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/order/product/profit")
@Api(tags = "订单收益")
public class OrderProductProfitController {
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @PreAuthorize("hasAuthority('agent:order:product:profit:page')")
    @GetMapping("/page")
    @ApiOperation("订单商品收益")
    public CommonResult<CommonPage<OrderProductProfit>> getList(OrderProductProfitRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(orderProductProfitService.pageList(request.getOrderNo(),request.getProfitName(),request.getStatus(),pageParamRequest)));
    }
}
