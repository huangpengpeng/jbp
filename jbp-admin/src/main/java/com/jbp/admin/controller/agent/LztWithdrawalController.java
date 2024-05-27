package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.SmsService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztWithdrawalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("api/admin/agent/lzt/withdrawal")
@Api(tags = "来账通提现接口")
public class LztWithdrawalController {


    @Resource
    private SmsService smsService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;

    @PreAuthorize("hasAuthority('agent:lzt:withdrawal:create')")
    @ApiOperation(value = "来账通提现")
    @GetMapping(value = "/create")
    public CommonResult<LztWithdrawal> apply(HttpServletRequest request, String payeeId, String payCode,
                                             String pwd, BigDecimal amt, String randomKey, String  phoneCode) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct acct = lztAcctService.getByUserId(payeeId);
        if (acct == null || acct.getMerId() != merId) {
            throw new CrmebException("付款用户不存在");
        }
        if("易宝".equals(acct.getPayChannelType())){
            smsService.checkValidateCode(systemAdmin.getPhone(), phoneCode);
        }
        String ip = CrmebUtil.getClientIp(request);
        LztWithdrawal result = lztWithdrawalService.withdrawal(merId, payeeId, payCode, amt, "提现", pwd, randomKey, ip);
        if(StringUtils.isNotEmpty(acct.getPhone())){
            String phone = acct.getPhone();
            result.setRegMsg("短信已发送至: " + phone.substring(0, 3) + "****" + phone.substring(7, phone.length()) + " 请注意查收");
        }else{
            result.setRegMsg("短信已发送请注意查收");
        }
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('agent:lzt:withdrawal:affrim')")
    @ApiOperation(value = "来账通提现确认")
    @GetMapping(value = "/affrim")
    public CommonResult<LztWithdrawal> affrim(Long id, String checkReturn, String checkReason) {
        LztWithdrawal result = lztWithdrawalService.check(id, checkReturn, checkReason);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "来账通提现刷新")
    @GetMapping(value = "/refresh")
    public CommonResult refresh(String accpTxno) {
        lztWithdrawalService.refresh(accpTxno);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:lzt:withdrawal:page')")
    @ApiOperation(value = "分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztWithdrawal>> page(String payeeId, String txnSeqno, String accpTxno, String status, PageParamRequest pageParamRequest,
                                                      @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date startTime,
                                                      @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date endTime) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztWithdrawal> page = lztWithdrawalService.pageList(merId, payeeId, txnSeqno, accpTxno, status, startTime, endTime, pageParamRequest);
        return CommonResult.success(page);
    }


    @ApiOperation(value = "来账通提现详情")
    @GetMapping(value = "/detail")
    public CommonResult<LztWithdrawal> detail(Long id) {
        return CommonResult.success(lztWithdrawalService.detail(id));
    }


}
