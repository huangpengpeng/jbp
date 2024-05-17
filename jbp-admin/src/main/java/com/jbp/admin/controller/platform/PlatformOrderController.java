package com.jbp.admin.controller.platform;

import cn.hutool.core.util.StrUtil;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformOrderAddressEditRequest;
import com.jbp.common.response.*;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LogisticsResultVo;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private PayService payService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('platform:order:page:list')")
    @ApiOperation(value = "平台端订单分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformOrderPageResponse>> getList(@Validated OrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        if (StrUtil.isNotEmpty(request.getUaccount())) {
            request.setUid(-1);
            User user = userService.getByAccount(request.getUaccount());
            if (user != null) {
                request.setUid(user.getId());
            }
        }
        if (StrUtil.isNotEmpty(request.getPayAccount())) {
            request.setPayUid(-1);
            User user = userService.getByAccount(request.getPayAccount());
            if (user != null) {
                request.setPayUid(user.getId());
            }
        }
        if (StrUtil.isNotEmpty(request.getPayPhone())) {
            List<User> userList = userService.getByPhone(request.getPayPhone());
            if (!CollectionUtils.isEmpty(userList)) {
                List<Integer> collect = userList.stream().map(User::getId).collect(Collectors.toList());
                request.setUidList(collect);
            }
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        request.setSupplyName(systemAdmin.getSupplyName());
        return CommonResult.success(CommonPage.restPage(orderService.getPlatformAdminPage(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:order:status:num')")
    @ApiOperation(value = "平台端获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<OrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(orderService.getPlatformOrderStatusNum(dateLimit, systemAdmin.getSupplyName()));
    }

    @ApiOperation(value = "平台端订单详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PlatformOrderAdminDetailResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        orderNo = orderService.getOrderNo(orderNo);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(orderService.platformInfo(orderNo, systemAdmin.getSupplyName()));
    }

    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public CommonResult<List<OrderInvoiceResponse>> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        orderNo = orderService.getOrderNo(orderNo);
        return CommonResult.success(orderService.getInvoiceList(orderNo));
    }

    @ApiOperation(value = "订单物流详情")
    @RequestMapping(value = "/get/{invoiceId}/logistics/info", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return CommonResult.success(orderService.getLogisticsInfo(invoiceId));
    }
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "平台端订单确认付款")
    @PreAuthorize("hasAuthority('platform:order:confirm:pay')")
    @ApiOperation(value = "订单确认付款")
    @RequestMapping(value = "/confirm/pay/{orderNo}", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> confirmPay(@PathVariable(value = "orderNo") String orderNo) {
        payService.confirmPay(orderService.getByOrderNo(orderNo));
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:order:address:edit')")
    @ApiOperation(value = "修改订单收货地址")
    @RequestMapping(value = "/address/edit", method = RequestMethod.POST)
    public CommonResult editAddress(@RequestBody @Validated PlatformOrderAddressEditRequest request){
        orderService.editAddress(request);
        return CommonResult.success();

    }

}



