package com.jbp.admin.controller.pay;

import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.pay.PayUserSubMerchantMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/pay/user/sub/merchant")
@Api(tags = "收款用户子商户进件绑定关系")
public class PayUserSubMerchantAct {

    @Resource
    private PayUserSubMerchantMng payUserSubMerchantMng;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PayUserSubMerchant>> page(@ModelAttribute PageParamRequest pageParamRequest, String payUserAccountName, String payUserAccountNo) {
        CommonPage<PayUserSubMerchant> result = CommonPage.restPage((payUserSubMerchantMng.page(pageParamRequest, payUserAccountName, payUserAccountNo)));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PayUserSubMerchant> detail(Long id) {
        return CommonResult.success(payUserSubMerchantMng.getById(id));
    }

    @ApiOperation(value = "保存或者更新")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PayUserSubMerchant> saveOrUpdate(PayUserSubMerchant info) {
        payUserSubMerchantMng.saveOrUpdate(info);
        return CommonResult.success(info);
    }
}
