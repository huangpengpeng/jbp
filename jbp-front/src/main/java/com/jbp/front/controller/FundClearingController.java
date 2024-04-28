package com.jbp.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.FundClearingFlowGetRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/front/fund/clearing")
@Api(tags = "佣金发放记录")
public class FundClearingController {
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private UserService userService;

    @Resource
    private SystemConfigService systemConfigService;


    @GetMapping("/totalGet")
    @ApiOperation("佣金发放记录统计")
    public CommonResult<Map<String, Object>> totalGet() {
        User info = userService.getInfo();
        return CommonResult.success(fundClearingService.totalGet(info.getId()));

    }
    @GetMapping("/flowGet")
    @ApiOperation("头部数据")
    public CommonResult<CommonPage<FundClearing>> flowGet(FundClearingFlowGetRequest request, PageParamRequest pageParamRequest) {
        User info = userService.getInfo();
        return CommonResult.success(CommonPage.restPage(fundClearingService.flowGet(info.getId(), request.getHeaderStatus(), pageParamRequest)));
    }


    @GetMapping("/data")
    @ApiOperation("积分中心")
    public CommonResult<JSONObject>  data(String month) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(month)) {
            month = DateTimeUtils.format(new Date(), "yyyy-MM");
        }

        Integer userId = userService.getUserId();

        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));

        //总积分
        BigDecimal total =  (fundClearingService.getUserTotal(userId).multiply(wallet_pay_integral)).setScale(2,BigDecimal.ROUND_UP);

        //月份积分
        BigDecimal totalMonth =  (fundClearingService.getUserTotalMonth(userId,month).multiply(wallet_pay_integral)).setScale(2,BigDecimal.ROUND_UP);
        //已结积分
        BigDecimal totalUnusedMonth =   (fundClearingService.getUserTotalContMonth(userId,month).multiply(wallet_pay_integral)).setScale(2,BigDecimal.ROUND_UP);
        //今日获得积分
        BigDecimal totalDay =  (fundClearingService.getUserTotalDay(userId, DateTimeUtils.format(new Date(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN)).multiply(wallet_pay_integral)).setScale(2,BigDecimal.ROUND_UP);

        List<Map<String,Object>> list =  fundClearingService.getUserTotalMonthList(userId,month);

        list.forEach(e->{
           e.put("total",(new BigDecimal(e.get("total").toString()).multiply(wallet_pay_integral)).setScale(2,BigDecimal.ROUND_UP));
        });

        jsonObject.put("month", month);
        jsonObject.put("total", total);
        jsonObject.put("totalMonth", totalMonth);
        jsonObject.put("totalUnusedMonth", totalUnusedMonth);
        jsonObject.put("totalDay", totalDay);
        jsonObject.put("list", list);
        return CommonResult.success(jsonObject);

    }



}

