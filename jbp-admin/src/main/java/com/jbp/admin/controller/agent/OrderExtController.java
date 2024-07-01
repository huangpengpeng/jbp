package com.jbp.admin.controller.agent;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrderExtProductListRequest;
import com.jbp.common.response.OrderExtProductResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.OrderExtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/order/ext")
@Api(tags = "订单扩展")
public class OrderExtController {
    @Resource
    private OrderExtService orderExtService;

    @PreAuthorize("hasAuthority('agent:order:ext:product:page')")
    @GetMapping("/product/page")
    @ApiOperation("订单商品扩展信息")
    public CommonResult<CommonPage<OrderExtProductResponse>> getProductList(OrderExtProductListRequest request,PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(orderExtService.getProductPage(request,pageParamRequest)));

    }
}
