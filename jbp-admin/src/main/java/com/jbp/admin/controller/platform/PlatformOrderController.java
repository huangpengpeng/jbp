package com.jbp.admin.controller.platform;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.OrderCountItemResponse;
import com.jbp.common.response.OrderInvoiceResponse;
import com.jbp.common.response.PlatformOrderAdminDetailResponse;
import com.jbp.common.response.PlatformOrderPageResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.LogisticsResultVo;
import com.jbp.service.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单表 前端控制器
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
@RequestMapping("api/admin/platform/order")
@Api(tags = "平台端订单控制器") //配合swagger使用
public class PlatformOrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('platform:order:page:list')")
    @ApiOperation(value = "平台端订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformOrderPageResponse>> getList(@Validated OrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(orderService.getPlatformAdminPage(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:order:status:num')")
    @ApiOperation(value = "平台端获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(orderService.getPlatformOrderStatusNum(dateLimit));
    }

    @PreAuthorize("hasAuthority('platform:order:info')")
    @ApiOperation(value = "平台端订单详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PlatformOrderAdminDetailResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.platformInfo(orderNo));
    }

    @PreAuthorize("hasAuthority('platform:order:invoice:list')")
    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(orderService.getInvoiceList(orderNo));
    }

    @PreAuthorize("hasAuthority('platform:order:logistics:info')")
    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(orderService.getLogisticsInfo(invoiceId));
    }

    @PreAuthorize("hasAuthority('platform:order:confirm:pay')")
    @ApiOperation(value = "订单确认付款")
    @RequestMapping(value = "/confirm/pay/{orderNo}", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> confirmPay(@PathVariable(value = "orderNo") String orderNo) {




        return CommonResult.success();
    }
}



