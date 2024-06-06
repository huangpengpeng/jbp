package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztFundTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("api/admin/agent/lzt/fund")
@Api(tags = "来账通划拨接口")
public class LztFundTransferController {

    @Resource
    private LztFundTransferService lztFundTransferService;
    @Resource
    private LztAcctService lztAcctService;
    @PreAuthorize("hasAuthority('agent:lzt:fund:page')")
    @ApiOperation(value = "分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztFundTransfer>> page(String userId, String username, String bankAccountNo, String txnSeqno,
                                                        String accpTxno, PageParamRequest pageParamRequest,
                                                        @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date startTime,
                                                        @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date endTime) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztFundTransfer> page = lztFundTransferService.pageList(merId, userId, username,bankAccountNo, txnSeqno,
                accpTxno, startTime, endTime, pageParamRequest);
        return CommonResult.success(page);
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "来账通资金划拨")
    @PreAuthorize("hasAuthority('agent:lzt:acct:transfer')")
    @ApiOperation(value = "来账通资金划拨")
    @GetMapping(value = "/transfer")
    public CommonResult<LztFundTransfer> transfer(String userId, String bankAccountNo, BigDecimal amt) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct acct = lztAcctService.getByUserId(userId);
        if (acct == null || acct.getMerId() != merId) {
            throw new CrmebException("收款款账户不存在");
        }
        if (StringUtils.isEmpty(bankAccountNo)) {
            throw new CrmebException("付款款账户不存在");
        }
        LztFundTransfer lztFundTransfer = lztFundTransferService.fundTransfer(merId, userId, bankAccountNo, amt, "付款");
        return CommonResult.success(lztFundTransfer);
    }
}
