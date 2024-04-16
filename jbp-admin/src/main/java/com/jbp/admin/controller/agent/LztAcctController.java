package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.LztInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.*;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztAcctService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
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
@RequestMapping("api/admin/agent/lzt/acct")
@Api(tags = "来账通-账户")
public class LztAcctController {

    @Resource
    private LztAcctApplyService lztAcctApplyService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztService lztService;
    @Resource
    private MerchantService merchantService;

    @PreAuthorize("hasAuthority('agent:lzt:acct:info')")
    @GetMapping("/info")
    @ApiOperation("来账通首页")
    public CommonResult<LztInfoResponse> info() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(lztAcctService.lztInfo(systemAdmin.getMerId()));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:page')")
    @ApiOperation(value = "来账通账户分页")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<LztAcct>> page(String userId, String username, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        PageInfo<LztAcct> page = lztAcctService.pageList(merId, userId, username, pageParamRequest);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation(value = "来账通账户详情[余额信息]")
    @GetMapping(value = "/details")
    public CommonResult<LztAcct> details(String userId) {
        return CommonResult.success(lztAcctService.details(userId));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:bank:apply')")
    @ApiOperation(value = "银行虚拟户申请")
    @GetMapping(value = "/bank/apply")
    public CommonResult<LztAcctApply> apply(Integer merId, String userId, String shopId, String shopName, String province,
                                            String city, String area, String address, String openBank) {
        LztAcctApply apply = lztAcctApplyService.apply(merId, userId, shopId, shopName, province, city, area, address, openBank);
        return CommonResult.success(apply);
    }

    @ApiOperation(value = "来账通账户密码控件")
    @GetMapping(value = "/pwd")
    public CommonResult<ApplyPasswordElementResult> pwd(String userId, BigDecimal amt, String scan) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        if (StringUtils.isEmpty(scan)) {
            throw new CrmebException("场景不能为空");
        }
        if(!(scan.equals("换绑卡") || scan.equals("忘记密码"))) {
            if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
                throw new CrmebException("请先输入金额");
            }
        }

        String payCode = "";
        switch (scan) {


            case "转账":
                scan = "pay_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通内部代发.getPrefix());
                break;
            case "代付":
                scan = "pay_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通外部代发.getPrefix());
                break;
            case "提现":
                scan = "cashout_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通提现.getPrefix());
                break;
            case "忘记密码":
                scan = "setting_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.设置密码.getPrefix());
                break;
            case "换绑卡":
                scan = "bind_card_password";
                payCode = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.换绑卡.getPrefix());
                break;


        }
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        ApplyPasswordElementResult result = lztService.getPasswordToken(payInfo.getOidPartner(), payInfo.getPriKey(),
                userId, payCode, lztAcct.getUsername(), amt, scan);
        result.setPasswordScene(scan);
        result.setPayCode(payCode);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "来账通账户代发资金用途")
    @GetMapping(value = "/purposeList")
    public CommonResult<List<String>> purposeList() {
        return CommonResult.success(Lists.newArrayList("服务费", "信息费", "修理费", "佣金支付", "贷款", "其他"));
    }

    @ApiOperation(value = "来账通用户类型")
    @GetMapping(value = "/userTypeList")
    public CommonResult<List<String>> userTypeList() {
        List<String> list = Lists.newArrayList();
        for (LianLianPayConfig.UserType value : LianLianPayConfig.UserType.values()) {
            if (!value.name().equals("商户")) {
                list.add(value.name());
            }
        }
        return CommonResult.success(list);
    }

    @ApiOperation(value = "来账通账户下拉选")
    @GetMapping(value = "/list")
    public CommonResult<List<LztAcct>> list() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        QueryWrapper<LztAcct> query = new QueryWrapper<>();
        query.lambda().eq(merId > 0, LztAcct::getMerId, merId);
        return CommonResult.success(lztAcctService.list(query));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:serialPage')")
    @SneakyThrows
    @ApiOperation(value = "账户资金明细 flagDc-> DEBIT：出账 CREDIT：入账 时间格式 yyyyMMddHHmmss")
    @GetMapping(value = "/serialPage")
    public CommonResult<CommonPage<AcctBalList>> serialPage(String userId, String dateStart, String endStart, String flagDc, Integer pageNo) {
        if (StringUtils.isEmpty(userId)) {
            throw new CrmebException("请选择账号查询");
        }
        if (StringUtils.isAnyEmpty(dateStart, endStart)) {
            throw new CrmebException("请选择查询时间");
        }
        if (pageNo == null || pageNo < 1) {
            throw new CrmebException("页码必传");
        }
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        if (merchant == null || merchant.getPayInfo() == null) {
            throw new CrmebException("商户未配置完整请联系管理员");
        }
        MerchantPayInfo payInfo = merchant.getPayInfo();
        CommonPage<AcctBalList> page = new CommonPage();
        AcctSerialResult result = lztService.queryAcctSerial(payInfo.getOidPartner(), payInfo.getPriKey(), userId,
                LianLianPayConfig.UserType.getCode(lztAcct.getUserType()), dateStart, endStart, flagDc, pageNo.toString());
        List<AcctBalList> acctbalList = result.getAcctbal_list();
        if (CollectionUtils.isNotEmpty(acctbalList)) {
            for (AcctBalList acctBalList : acctbalList) {
                acctBalList.setUserId(lztAcct.getUserId());
                acctBalList.setUserNo(lztAcct.getUserNo());
                acctBalList.setUsername(lztAcct.getUsername());
                acctBalList.setUserType(lztAcct.getUserType());
                acctBalList.setTxn_type(LianLianPayConfig.SerialTxnType.getName(acctBalList.getTxn_type()));
                acctBalList.setFlag_dc("CREDIT".equals(acctBalList.getFlag_dc()) ? "入账" : "出账");
                acctBalList.setTxn_time(DateTimeUtils.format(DateTimeUtils.parseDate(acctBalList.getTxn_time(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            }
        }
        page.setPage(result.getPage_no());
        page.setLimit(result.getAcctbal_list().size());
        page.setTotalPage(result.getTotal_page());
        page.setTotal(result.getTotal_num().longValue());
        page.setList(acctbalList);
        return CommonResult.success(page);
    }

    @SneakyThrows
    @ApiOperation(value = "短信二次校验")
    @GetMapping(value = "/validationSms")
    public CommonResult validationSms(String userId, String payCode, String amt, String token, String code) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        lztService.validationSms(payInfo.getOidPartner(), payInfo.getPriKey(), userId, payCode, amt, token, code);
        return CommonResult.success();
    }


    @SneakyThrows
    @ApiOperation(value = "忘记密码发送验证码")
    @GetMapping(value = "/findPwdSendCode")
    public CommonResult<FindPasswordApplyResult> findPwdSendCode(String userId, HttpServletRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        String ip = CrmebUtil.getClientIp(request);
        String linked_acctno = "";
        if (LianLianPayConfig.UserType.个人用户.name().equals(lztAcct.getUserType())) {
            QueryLinkedAcctResult queryLinkedAcctResult = lztService.queryLinkedAcct(payInfo.getOidPartner(), payInfo.getPriKey(), userId);
            if (queryLinkedAcctResult != null && CollectionUtils.isNotEmpty(queryLinkedAcctResult.getLinked_acctlist())) {
                linked_acctno = queryLinkedAcctResult.getLinked_acctlist().get(0).getLinked_acctno();
            }
        }
        FindPasswordApplyResult passwordApply = lztService.findPasswordApply(payInfo.getOidPartner(),
                payInfo.getPriKey(), userId, linked_acctno, ip);
        if (passwordApply != null && "0000".equals(passwordApply.getRet_code())) {
            passwordApply.setRegMsg("已发送至: " + passwordApply.getReg_phone() + " 请注意查收");
        }
        return CommonResult.success(passwordApply);
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:findPwd')")
    @SneakyThrows
    @ApiOperation(value = "忘记密码")
    @GetMapping(value = "/findPwd")
    public CommonResult<FindPasswordVerifyResult> findPwd(String userId, String token, String verifyCode, String randomKey, String password) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        FindPasswordVerifyResult passwordVerify = lztService.findPasswordVerify(payInfo.getOidPartner(), payInfo.getPriKey(), userId, token, verifyCode, randomKey, password);
        return CommonResult.success(passwordVerify);
    }


    @SneakyThrows
    @ApiOperation(value = "修改手机号发送验证码")
    @GetMapping(value = "/changePhoneSendCode")
    public CommonResult<ChangeRegPhoneApplyResult> changePhoneSendCode(String userId, String regPhone, String newPhone,
                                                                       String pwd, String randomKey, HttpServletRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        String ip = CrmebUtil.getClientIp(request);
        ChangeRegPhoneApplyResult result = lztService.changeRegPhoneApply(payInfo.getOidPartner(),
                payInfo.getPriKey(), userId, regPhone, newPhone, pwd, randomKey, merchant.getCreateTime(), ip, merchant.getFrmsWareCategory());

        if (result != null && "0000".equals(result.getRet_code())) {
            result.setRegMsg("已发送至: " + newPhone + " 请注意查收");
        }
        lztAcct.setChangePhone(newPhone);
        lztAcctService.updateById(lztAcct);
        return CommonResult.success(result);
    }


    @PreAuthorize("hasAuthority('agent:lzt:acct:changePhone')")
    @ApiOperation(value = "修改手机号确认")
    @GetMapping(value = "/changePhone")
    public CommonResult changePhone(String userId, String token, String txn_seqno, String code) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        if (lztAcct == null || lztAcct.getMerId() != merId) {
            throw new CrmebException("账户不存在");
        }
        Merchant merchant = merchantService.getById(merId);
        MerchantPayInfo payInfo = merchant.getPayInfo();
        ChangeRegPhoneVerifyResult result = lztService.changeRegPhoneVerify(payInfo.getOidPartner(),
                payInfo.getPriKey(), userId, token, txn_seqno, code);
        if ("0000".equals(result.getRet_code())) {
            lztAcct.setPhone(lztAcct.getChangePhone());
            lztAcctService.updateById(lztAcct);
            return CommonResult.success();
        }
        return CommonResult.failed(result.getRet_msg());
    }

}
