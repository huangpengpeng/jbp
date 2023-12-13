package com.jbp.admin.controller.merchant;

import com.jbp.admin.service.FinanceService;
import com.jbp.common.model.bill.MerchantDailyStatement;
import com.jbp.common.model.bill.MerchantMonthStatement;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.FundsFlowRequest;
import com.jbp.common.request.MerchantClosingApplyRequest;
import com.jbp.common.request.MerchantClosingSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.FundsFlowResponse;
import com.jbp.common.response.MerchantClosingBaseInfoResponse;
import com.jbp.common.response.MerchantClosingDetailResponse;
import com.jbp.common.response.MerchantClosingPageResponse;
import com.jbp.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户端财务控制器
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
@RequestMapping("api/admin/merchant/finance")
@Api(tags = "商户端财务控制器")
public class MerchantFinanceController {

    @Autowired
    private FinanceService financeService;

    @PreAuthorize("hasAuthority('merchant:finance:funds:flow')")
    @ApiOperation(value = "资金流水分页列表")
    @RequestMapping(value = "/funds/flow", method = RequestMethod.GET)
    public CommonResult<CommonPage<FundsFlowResponse>> getFundsFlow(@Validated FundsFlowRequest request,
                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantFundsFlow(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:base:info')")
    @ApiOperation(value = "获取结算申请基础信息")
    @RequestMapping(value = "/closing/base/info", method = RequestMethod.GET)
    public CommonResult<MerchantClosingBaseInfoResponse> getClosingBaseInfo() {
        return CommonResult.success(financeService.getClosingBaseInfo());
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:apply')")
    @ApiOperation(value = "结算申请")
    @RequestMapping(value = "/closing/apply", method = RequestMethod.POST)
    public CommonResult<String> closingApply(@RequestBody @Validated MerchantClosingApplyRequest request) {
        if (financeService.merchantClosingApply(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:page:list')")
    @ApiOperation(value = "结算记录分页列表")
    @RequestMapping(value = "/closing/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantClosingPageResponse>> getMerchantClosingPageList(@Validated MerchantClosingSearchRequest request,
                                                                                            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantClosingRecordPageList(request, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:closing:detail')")
    @ApiOperation(value = "结算记录详情")
    @RequestMapping(value = "/closing/record/detail/{closingNo}", method = RequestMethod.GET)
    public CommonResult<MerchantClosingDetailResponse> getMerchantClosingDetail(@PathVariable(value = "closingNo") String closingNo) {
        return CommonResult.success(financeService.getMerchantClosingDetailByMerchant(closingNo));
    }

    @PreAuthorize("hasAuthority('merchant:finance:daily:statement:page:list')")
    @ApiOperation(value = "日帐单管理分页列表")
    @RequestMapping(value = "/daily/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantDailyStatement>> getDailyStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                  @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantDailyStatementList(dateLimit, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('merchant:finance:month:statement:page:list')")
    @ApiOperation(value = "月帐单管理分页列表")
    @RequestMapping(value = "/month/statement/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantMonthStatement>> getMonthStatementList(@RequestParam(value = "dateLimit", required = false, defaultValue = "") String dateLimit,
                                                                                  @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(financeService.getMerchantMonthStatementList(dateLimit, pageParamRequest)));
    }
}



