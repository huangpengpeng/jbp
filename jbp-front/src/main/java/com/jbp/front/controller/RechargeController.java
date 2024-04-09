package com.jbp.front.controller;

import com.jbp.common.request.UserRechargeRequest;
import com.jbp.common.response.OrderPayResultResponse;
import com.jbp.common.response.RechargePackageResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.IPUtil;
import com.jbp.service.service.RechargeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 充值控制器
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
@RequestMapping("api/front/recharge")
@Api(tags = "充值控制器")
public class RechargeController {

    @Autowired
    private RechargeOrderService rechargeOrderService;

    @ApiOperation(value = "获取用户充值套餐")
    @RequestMapping(value = "/get/user/package", method = RequestMethod.GET)
    public CommonResult<RechargePackageResponse> getRechargePackage() {
        return CommonResult.success(rechargeOrderService.getRechargePackage());
    }

    @ApiOperation(value = "生成用户充值订单")
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> userRechargeOrderCreate(@RequestBody @Validated UserRechargeRequest request, HttpServletRequest httpRequest) {
       request.setIp(IPUtil.getIpAddress(httpRequest));
        return CommonResult.success(rechargeOrderService.userRechargeOrderCreate(request));
    }


//    @ApiOperation(value = "佣金转入余额")
//    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
//    public CommonResult<Boolean> transferIn(@RequestParam(name = "price") BigDecimal price) {
//        return CommonResult.success(rechargeOrderService.transferIn(price));
//    }
//
//    @ApiOperation(value = "用户账单记录")
//    @RequestMapping(value = "/bill/record", method = RequestMethod.GET)
//    @ApiImplicitParam(name = "type", value = "记录类型：all-全部，expenditure-支出，income-收入", required = true)
//    public CommonResult<CommonPage<UserRechargeBillRecordResponse>> billRecord(@RequestParam(name = "type") String type, @ModelAttribute PageParamRequest pageRequest) {
//        return CommonResult.success(rechargeOrderService.nowMoneyBillRecord(type, pageRequest));
//    }
}



