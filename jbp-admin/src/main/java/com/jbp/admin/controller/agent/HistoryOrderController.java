package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.HistoryOrderEditRequest;
import com.jbp.common.request.HistoryOrderRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.InvitationScoreRequest;
import com.jbp.common.response.HistoryOrderResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/history/order")
@Api(tags = "历史订单列表")
public class HistoryOrderController {

    @PreAuthorize("hasAuthority('agent:history:order:page')")
    @GetMapping("/page")
    @ApiOperation("列表")
    public CommonResult<CommonPage<HistoryOrderResponse>> page(HistoryOrderRequest request, PageParamRequest pageParamRequest) {
//        CommonPage.restPage(null)
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:history:order:page2')")
    @GetMapping("/page")
    @ApiOperation("雪康列表")
    public CommonResult<CommonPage<HistoryOrderResponse>> page2(HistoryOrderRequest request, PageParamRequest pageParamRequest) {
//        CommonPage.restPage(null)
        return CommonResult.success();
    }


    @PostMapping("/edit")
    @ApiOperation("编辑订单")
    public CommonResult<Boolean> edit(@RequestBody HistoryOrderEditRequest  request) {
//        CommonPage.restPage(null)

//        SELECT o.userId as uid, u.username as nickname, u.numberCode as account,
//                o.orderSn as orderNo, o.status , o.payPrice, o.freightPrice, o.goodsPrice,
//                o.receiveName, o.mobile as receiveMobile, o.address,
//                o.shipName, o.shipSn, o.createTime, o.payTime, o.shipTime, o.id as orderId
//        FROM orders AS o
//        LEFT JOIN USER AS u ON u.`id` = o.userId
//        WHERE 1 =1 ORDER BY o.id DESC;
//
//
//        select * from ordergoods where orderId=461
        return CommonResult.success();
    }

}
