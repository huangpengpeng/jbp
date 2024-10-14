package com.jbp.admin.controller.pay;

import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.pay.PayUserAccount;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.pay.PayUserAccountMng;
import com.jbp.service.service.pay.PayUserSubMerchantMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/pay/user/account")
@Api(tags = "收款用户")
public class PayUserAccountAct {

    @Resource
    private PayUserAccountMng payUserAccountMng;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PayUserAccount>> page(@ModelAttribute PageParamRequest pageParamRequest, String accountName, String accountNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        CommonPage<PayUserAccount> result = CommonPage.restPage((payUserAccountMng.page(pageParamRequest, accountName, accountNo, systemAdmin.getMerId())));
        return CommonResult.success(result);
    }


    /**
     * 查询余额
     */

    /**
     * 操作提现
     */
}
