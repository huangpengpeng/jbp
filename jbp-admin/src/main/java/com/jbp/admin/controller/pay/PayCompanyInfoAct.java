
package com.jbp.admin.controller.pay;


import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.pay.PayCompanyInfoMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("api/admin/pay/company/info")
@Api(tags = "支付收款公司信息")
public class PayCompanyInfoAct {

    @Autowired
    private PayCompanyInfoMng payCompanyInfoMng;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PayCompanyInfo>> page(@ModelAttribute PageParamRequest pageParamRequest, String name, String status) {
        CommonPage<PayCompanyInfo> result = CommonPage.restPage((payCompanyInfoMng.page(pageParamRequest, name, status)));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PayCompanyInfo> detail(Long id) {
        return CommonResult.success(payCompanyInfoMng.getById(id));
    }

    @ApiOperation(value = "保存或者更新")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<PayCompanyInfo> saveOrUpdate(PayCompanyInfo info) {
        payCompanyInfoMng.saveOrUpdate(info);
        return CommonResult.success(info);
    }

}



