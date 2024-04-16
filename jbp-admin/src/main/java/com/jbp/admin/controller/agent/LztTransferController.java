package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.CardListInfo;
import com.jbp.common.lianlian.result.QueryCnapsCodeResult;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferService;
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
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lzt/transfer/out/")
@Api(tags = "来账通外部代发接口")
public class LztTransferController {

    @Resource
    private MerchantService merchantService;
    @Resource
    private LztService lztService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztTransferService lztTransferService;

    @PreAuthorize("hasAuthority('agent:lzt:transfer:out:create')")
    @ApiOperation(value = "来账通外部代付")
    @GetMapping(value = "/create")
    public CommonResult<LztTransfer> apply(HttpServletRequest request, String payerId, String payCode, String payeeType,
                                           String pwd, BigDecimal amt, String randomKey, String cnapsCode, String bankAcctNo,
                                           String bankCode, String bankAcctName) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct acct = lztAcctService.getByUserId(payerId);
        if (acct == null || acct.getMerId() != merId) {
            throw new CrmebException("付款用户不存在");
        }
        String ip = CrmebUtil.getClientIp(request);
        LztTransfer lztTransfer = lztTransferService.create(payerId, payCode, amt, payeeType, bankAcctNo,
                bankCode, bankAcctName, cnapsCode, "服务费", pwd, randomKey, "服务费", ip);
        if(StringUtils.isNotEmpty(acct.getPhone())){
            String phone = acct.getPhone();
            lztTransfer.setRegMsg("短信已发送至: " + phone.substring(0, 3) + "****" + phone.substring(7, phone.length()) + " 请注意查收");
        }else{
            lztTransfer.setRegMsg("短信已发送请注意查收");
        }
        return CommonResult.success(lztTransfer);
    }

    @PreAuthorize("hasAuthority('agent:lzt:transfer:out:page')")
    @ApiOperation(value = "分页")
    @GetMapping(value = "/page")
    public CommonResult<PageInfo<LztTransfer>> page(String payerId, String txnSeqno, String status, String bankAcctNo, String bankAcctName,
                                                    PageParamRequest pageParamRequest,
                                                    @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date startTime,
                                                    @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN) Date endTime) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztTransfer> page = lztTransferService.pageList(merId, payerId, txnSeqno,
                bankAcctNo, bankAcctName, status, startTime, endTime, pageParamRequest);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "外部代付详情")
    @GetMapping(value = "/detail")
    public CommonResult<LztTransfer> detail(Long id) {
        return CommonResult.success(lztTransferService.detail(id));
    }

    @PreAuthorize("hasAuthority('agent:lzt:transfer:out:affrim')")
    @ApiOperation(value = "外部代付确认")
    @GetMapping(value = "/affrim")
    public CommonResult<LztTransfer> affrim(Long id, String checkReturn, String checkReason) {
        return CommonResult.success(lztTransferService.check(id, checkReturn, checkReason));
    }

    @ApiOperation(value = "查询联行卡号")
    @GetMapping(value = "/cnapsCodeList")
    public CommonResult<List<CardListInfo>> cnapsCodeList(String bankCode, String braBankName, String cityCode) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        MerchantPayInfo payInfo = merchantService.getById(merId).getPayInfo();
        QueryCnapsCodeResult queryCnapsCodeResult = lztService.queryCnapsCode(payInfo.getOidPartner(),
                payInfo.getPriKey(), bankCode, braBankName, cityCode);
        List<CardListInfo> cardList = queryCnapsCodeResult == null ? Lists.newArrayList() : queryCnapsCodeResult.getCard_list();
        return CommonResult.success(cardList);
    }

}
