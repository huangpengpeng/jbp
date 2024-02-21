package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.OrdersRefundMsg;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrdersRefundMsgRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/orders/refund/msg")
@Api(tags = "订单退款消息")
public class OrdersRefundMsgController {
    @Resource
    private OrdersRefundMsgService ordersRefundMsgService;
    @PreAuthorize("hasAuthority('agent:orders:refund:msg:page')")
    @GetMapping("/page")
    @ApiOperation("订单退款信息列表")
    public CommonResult<CommonPage<OrdersRefundMsg>> getList(OrdersRefundMsgRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(ordersRefundMsgService.pageList(request.getOrdersSn(), request.getRefundSn(), request.getIfRead(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:orders:refund:msg:read')")
    @PostMapping("/read")
    @ApiOperation("批量已读")
    public CommonResult read(@RequestBody List<Long> ids) {
        ordersRefundMsgService.read(ids);
        return CommonResult.success();
    }

}
