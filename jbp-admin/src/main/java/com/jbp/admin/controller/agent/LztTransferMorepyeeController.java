package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
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
@RequestMapping("api/admin/agent/lzt/transfer/morepyee")
@Api(tags = "来账通代发接口")
public class LztTransferMorepyeeController {

    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztTransferMorepyeeService lztTransferMorepyeeService;

    @PreAuthorize("hasAuthority('agent:lzt:transfer:morepyee:create')")
    @ApiOperation(value = "来账通内部代发")
    @GetMapping(value = "/create")
    public CommonResult<LztTransferMorepyee> apply(HttpServletRequest request, String payerId, String payeeId, String payCode,
                                                   String pwd, BigDecimal amt, String randomKey) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct acct = lztAcctService.getByUserId(payerId);
        if (acct == null || acct.getMerId() != merId) {
            throw new CrmebException("付款用户不存在");
        }
        LztAcct acct2 = lztAcctService.getByUserId(payeeId);
        if (acct2 == null) {
            throw new CrmebException("收款款用户不存在");
        }
        String ip = CrmebUtil.getClientIp(request);
        LztTransferMorepyee result = lztTransferMorepyeeService.transferMorepyee(merId, payerId, payCode, amt, "服务费", pwd, randomKey, payeeId, ip, "服务费");
        if(StringUtils.isNotEmpty(acct.getPhone())){
            String phone = acct.getPhone();
            result.setRegMsg("短信已发送至: " + phone.substring(0, 3) + "****" + phone.substring(7, phone.length()) + " 请注意查收");
        }else{
            result.setRegMsg("短信已发送请注意查收");
        }

        return CommonResult.success(result);
    }


    @ApiOperation(value = "来账通内部代发刷新")
    @GetMapping(value = "/refresh")
    public CommonResult refresh(String accpTxno) {
        lztTransferMorepyeeService.refresh(accpTxno);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:lzt:transfer:morepyee:page')")
    @ApiOperation(value = "分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztTransferMorepyee>> page(String payerId, String payeeId, String txnSeqno,
                                                            String accpTxno, String status,
                                                            PageParamRequest pageParamRequest,
                                                            @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date startTime,
                                                            @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date endTime) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztTransferMorepyee> page = lztTransferMorepyeeService.pageList(merId, payerId, payeeId, txnSeqno,
                accpTxno, status, startTime, endTime, pageParamRequest);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "来账通转账详情")
    @GetMapping(value = "/detail")
    public CommonResult<LztTransferMorepyee> detail(Long id) {
        return CommonResult.success(lztTransferMorepyeeService.detail(id));
    }






}
