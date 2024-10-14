package com.jbp.admin.controller.pay;

import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.pay.PaySubMerchantMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/pay/sub/merchant")
@Api(tags = "支付进件子商户")
public class PaySubMerchantAct {

    @Resource
    private PaySubMerchantMng paySubMerchantMng;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PaySubMerchant>> page(@ModelAttribute PageParamRequest pageParamRequest, String merchantName, String merchantNo) {
        CommonPage<PaySubMerchant> result = CommonPage.restPage((paySubMerchantMng.page(pageParamRequest, merchantName, merchantNo)));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PaySubMerchant> detail(Long id) {
        return CommonResult.success(paySubMerchantMng.getById(id));
    }

    @ApiOperation(value = "保存或者更新")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PaySubMerchant> saveOrUpdate(PaySubMerchant info) {
        paySubMerchantMng.saveOrUpdate(info);
        return CommonResult.success(info);
    }
}
