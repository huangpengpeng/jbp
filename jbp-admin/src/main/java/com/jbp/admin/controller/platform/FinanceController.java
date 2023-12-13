package com.jbp.admin.controller.platform;

import com.jbp.admin.service.FinanceService;
import com.jbp.common.model.bill.PlatformDailyStatement;
import com.jbp.common.model.bill.PlatformMonthStatement;
import com.jbp.common.model.user.UserClosing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.FundsFlowResponse;
import com.jbp.common.response.MerchantClosingInfoResponse;
import com.jbp.common.response.MerchantClosingPlatformPageResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.MerchantClosingConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 平台端财务控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/finance")
@Api(tags = "平台端财务控制器")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @PreAuthorize("hasAuthority('platform:finance:user:closing:page:list')")
    @ApiOperation(value = "用户结算分页列表")
    @RequestMapping(value = "/user/closing/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserClosing>> getUserClosingPageList(@ModelAttribute UserClosingSearchRequest request,
                                                                        @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getUserClosingPageList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:finance:user:closing:audit')")
    @ApiOperation(value = "用户结算申请审核")
    @RequestMapping(value = "/user/closing/audit", method = RequestMethod.POST)
    public CommonResult<String> userClosingAudit(@RequestBody @Validated ClosingAuditRequest request) {
        if (financeService.userClosingAudit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:user:closing:proof')")
    @ApiOperation(value = "用户结算到账凭证")
    @RequestMapping(value = "/user/closing/proof", method = RequestMethod.POST)
    public CommonResult<String> userClosingProof(@RequestBody @Validated ClosingProofRequest request) {
        if (financeService.userClosingProof(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:user:closing:remark')")
    @ApiOperation(value = "用户结算备注")
    @RequestMapping(value = "/user/closing/remark", method = RequestMethod.POST)
    public CommonResult<String> userClosingRemark(@RequestBody @Validated ClosingRemarkRequest request) {
        if (financeService.userClosingRemark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:config')")
    @ApiOperation(value = "获取商户结算设置")
    @RequestMapping(value = "/merchant/closing/config", method = RequestMethod.GET)
    public CommonResult<MerchantClosingConfigVo> getMerchantClosingConfig() {
        return CommonResult.success(financeService.getMerchantClosingConfig());
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:config:edit')")
    @ApiOperation(value = "编辑商户结算设置")
    @RequestMapping(value = "/merchant/closing/config/edit", method = RequestMethod.POST)
    public CommonResult<String> merchantClosingConfigEdit(@RequestBody @Validated MerchantClosingConfigVo request) {
        if (financeService.merchantClosingConfigEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:page:list')")
    @ApiOperation(value = "商户结算分页列表")
    @RequestMapping(value = "/merchant/closing/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantClosingPlatformPageResponse>> getMerchantClosingPageList(
            @ModelAttribute MerchantClosingSearchRequest request, @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantClosingPageList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:detail')")
    @ApiOperation(value = "商户结算记录详情")
    @RequestMapping(value = "/merchant/closing/detail/{closingNo}", method = RequestMethod.GET)
    public CommonResult<MerchantClosingInfoResponse> getMerchantClosingDetail(@PathVariable String closingNo) {
        return CommonResult.success(financeService.getMerchantClosingDetailByPlatform(closingNo));
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:audit')")
    @ApiOperation(value = "商户结算申请审核")
    @RequestMapping(value = "/merchant/closing/audit", method = RequestMethod.POST)
    public CommonResult<String> merchantClosingAudit(@RequestBody @Validated ClosingAuditRequest request) {
        if (financeService.merchantClosingAudit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:proof')")
    @ApiOperation(value = "商户结算到账凭证")
    @RequestMapping(value = "/merchant/closing/proof", method = RequestMethod.POST)
    public CommonResult<String> merchantClosingProof(@RequestBody @Validated ClosingProofRequest request) {
        if (financeService.merchantClosingProof(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:merchant:closing:remark')")
    @ApiOperation(value = "商户结算备注")
    @RequestMapping(value = "/merchant/closing/remark", method = RequestMethod.POST)
    public CommonResult<String> merchantClosingRemark(@RequestBody @Validated ClosingRemarkRequest request) {
        if (financeService.merchantClosingRemark(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:finance:funds:flow')")
    @ApiOperation(value = "资金流水分页列表")
    @RequestMapping(value = "/funds/flow", method = RequestMethod.GET)
    public CommonResult<CommonPage<FundsFlowResponse>> getFundsFlow(@ModelAttribute FundsFlowRequest request,
                                                                    @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getFundsFlow(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:finance:daily:statement:page:list')")
    @ApiOperation(value = "日帐单管理分页列表")
    @RequestMapping(value = "/daily/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformDailyStatement>> getDailyStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                  @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getPlatformDailyStatementList(dateLimit, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('platform:finance:month:statement:page:list')")
    @ApiOperation(value = "月帐单管理分页列表")
    @RequestMapping(value = "/month/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PlatformMonthStatement>> getMonthStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                  @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getPlatformMonthStatementList(dateLimit, pageParamRequest)));
    }
}



