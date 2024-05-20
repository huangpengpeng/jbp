package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.OrdersFundSummary;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrdersFundSummaryRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.OrdersFundSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/orders/fund/summary")
@Api(tags = "订单资金汇总")
public class OrdersFundSummaryController {
    @Resource
    private OrdersFundSummaryService ordersFundSummaryService;
    @PreAuthorize("hasAuthority('agent:orders:fund:summary:page')")
    @GetMapping("/page")
    @ApiOperation("订单资金汇总列表")
    public CommonResult<CommonPage<OrdersFundSummary>> getList(OrdersFundSummaryRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(ordersFundSummaryService.pageList(request.getOrdersSn(),request.getTeamId(),pageParamRequest)));
    }
}
