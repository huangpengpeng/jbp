package com.jbp.admin.controller.pay;

import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.pay.PayChannelMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/pay/channel")
@Api(tags = "支付收款公司信息")
public class PayChannelAct {

    @Autowired
    private PayChannelMng payChannelMng;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PayChannel>> page(@ModelAttribute PageParamRequest pageParamRequest, String name) {
        CommonPage<PayChannel> result = CommonPage.restPage((payChannelMng.page(pageParamRequest, name)));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "保存或者更新")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PayChannel> saveOrUpdate(PayChannel info) {
        payChannelMng.saveOrUpdate(info);
        return CommonResult.success(info);
    }
}
