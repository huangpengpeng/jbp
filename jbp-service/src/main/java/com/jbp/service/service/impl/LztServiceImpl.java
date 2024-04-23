package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.client.LLianPayClient;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.agent.LztWithdrawalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztServiceImpl implements LztService {

    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;

    @Override
    public OpenacctApplyResult createUser(String oidPartner, String priKey, String txnSeqno, String userId,
                                          String userType, String notifyUrl, String returnUrl, String flagChnl, String businessScope) {
        LianLianPayInfoResult payInfo = lianLianPayService.get();
        OpenacctApplyParams params = new OpenacctApplyParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        params.setTxn_seqno(txnSeqno);
        params.setTxn_time(timestamp);
        params.setFlag_chnl(flagChnl);
        params.setReturn_url(returnUrl);
        params.setNotify_url(payInfo.getHost() + notifyUrl);
        params.setUser_type(userType);
        params.setCust_trade_serial_type("OpenNormalUser");
        OpenacctApplyAccountInfo accountInfo = new OpenacctApplyAccountInfo();
        accountInfo.setAccount_type("INNERUSER".equals(userType) ? "PERSONAL_PAYMENT_ACCOUNT" : "ENTERPRISE_PAYMENT_ACCOUNT");
        if ("INNERUSER".equals(userType)) {
            accountInfo.setAccount_need_level("V3");
        }
        params.setAccountInfo(accountInfo);
        // 行业类目
        OpenacctApplyBusinessInfo businessInfo = new OpenacctApplyBusinessInfo();
        businessInfo.setBusiness_scope(businessScope);
        params.setBusinessInfo(businessInfo);

        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/openacct-apply";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, payInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("请求开户异常");
        }
        try {
            OpenacctApplyResult result = JSON.parseObject(s, OpenacctApplyResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("请求开户异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("请求开户异常:" + s);
        }
    }

    @Override
    public UserInfoResult queryUserInfo(String oidPartner, String priKey, String userId) {
        LianLianPayInfoResult payInfo = lianLianPayService.get();
        UserInfoParams params = new UserInfoParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-userinfo";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, payInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("查询用户信息异常");
        }
        UserInfoResult result = JSON.parseObject(s, UserInfoResult.class);
        return result;
    }

    @Override
    public AcctInfoResult queryAcct(String oidPartner, String priKey, String userId, String userType) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        AcctInfoParams params = new AcctInfoParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        params.setUser_type(userType);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-acctinfo";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("查询连连资金账户异常:" + userId);
        }
        AcctInfoResult result = JSON.parseObject(s, AcctInfoResult.class);
        return result;
    }

    @Override
    public LztOpenacctApplyResult createBankUser(String oidPartner, String priKey, String userId, String txnSeqno, String shopId,
                                                 String shopName, String province, String city,
                                                 String area, String address, String notifyUrl, String openBank) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        LztOpenacctApplyParams params = new LztOpenacctApplyParams(userId, txnSeqno, notifyUrl, "USER", shopId, shopName, province, city, area, address);
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_type("USER");
        params.setAcct_type("USEROWN");
        params.setTxn_time(timestamp);
        params.setNotify_url(lianLianInfo.getHost() + params.getNotify_url());
        params.setOpen_bank(openBank);

        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/lzt-openacct-apply";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("请求银行开户异常");
        }
        try {
            LztOpenacctApplyResult result = JSON.parseObject(s, LztOpenacctApplyResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("请求银行开户异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("请求银行开户异常:" + s);
        }
    }

    @Override
    public LztQueryAcctInfoResult queryBankAcct(String oidPartner, String priKey, String userId) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        LztQueryAcctInfoParams params = new LztQueryAcctInfoParams(timestamp, oidPartner, userId, "USER", "USEROWN");
        String url = "https://accpquery.lianlianpay.com/v1/lzt/query-acctinfo";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("查询银行账户资金异常");
        }
        LztQueryAcctInfoResult result = JSON.parseObject(s, LztQueryAcctInfoResult.class);
        return result;
    }

    @Override
    public LztFundTransferResult fundTransfer(String oidPartner, String priKey, String txnSeqno, String userId, String bankAccountNo, String amt, String notifyUrl) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        LztFundTransferParams params = new LztFundTransferParams(txnSeqno, userId, bankAccountNo, amt, lianLianInfo.getHost() + notifyUrl);
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setTxn_time(timestamp);
        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/lzt-fund-transfer";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("划拨资金异常");
        }
        try {
            LztFundTransferResult result = JSON.parseObject(s, LztFundTransferResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("划拨资金异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("划拨资金异常:" + s);
        }
    }

    @Override
    public LztQueryFundTransferResult queryFundTransfer(String oidPartner, String priKey, String userId, String txnSeqno) {
        LztQueryFundTransferParams params = new LztQueryFundTransferParams();
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        params.setTxn_seqno(txnSeqno);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-lzt-fund-transfer";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("划拨资金查询异常:" + txnSeqno);
        }
        try {
            LztQueryFundTransferResult result = JSON.parseObject(s, LztQueryFundTransferResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("划拨资金查询异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("划拨资金查询异常:" + s);
        }
    }

    @Override
    public TransferMorepyeeResult transferMorepyee(String oidPartner, String priKey, String payerId, String orderNo,
                                                   Double amt, String txnPurpose, String pwd, String randomKey,
                                                   String payeeId, String ip, String notify_url, String phone, Date registerTime, String frmsWareCategory) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        TransferMorepyeeParams params = new TransferMorepyeeParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setFunds_flag("N");
        params.setDirectionalpay_flag("N");
        params.setContinuously_flag("N");
        params.setNotify_url(lianLianInfo.getHost() + notify_url);

        // 商户订单信息
        TransferMorepyeeOrderInfo orderInfo = new TransferMorepyeeOrderInfo();
        orderInfo.setTxn_seqno(orderNo);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amt);
        orderInfo.setTxn_purpose(txnPurpose);
        params.setOrderInfo(orderInfo);

        // 付款方信息
        TransferMorepyeePayerInfo payerInfo = new TransferMorepyeePayerInfo();
        payerInfo.setPayer_type("USER");
        payerInfo.setPayer_id(payerId);
        payerInfo.setPassword(pwd);
        payerInfo.setRandom_key(randomKey);
        params.setPayerInfo(payerInfo);

        // 收款方信息
        TransferMorepyeePayeeInfo payeeInfo = new TransferMorepyeePayeeInfo();
        payeeInfo.setPayee_type("USER");
        payeeInfo.setPayee_id(payeeId);
        payeeInfo.setPayee_amount(String.valueOf(amt));
        params.setPayeeInfo(Arrays.asList(payeeInfo));
        // 风控参数
        String registerTimeStr = DateTimeUtils.format(registerTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo(frmsWareCategory, payeeId, phone, registerTimeStr, txnPurpose);
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("13");
        riskItemInfo.setUser_auth_flag("1");

        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));
        String url = "https://accpapi.lianlianpay.com/v1/txn/transfer-morepyee";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("内部转账异常");
        }
        try {
            TransferMorepyeeResult result = JSON.parseObject(s, TransferMorepyeeResult.class);
            if (result == null || !("8888".equals(result.getRet_code()) || "0000".equals(result.getRet_code()) || "8889".equals(result.getRet_code()))) {
                throw new CrmebException("内部转账异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("内部转账异常:" + e.getMessage());
        }
    }

    @Override
    public QueryPaymentResult queryTransferMorepyee(String oidPartner, String priKey, String txnSeqno) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        QueryPaymentParams params = new QueryPaymentParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(oidPartner);
        params.setTxn_seqno(txnSeqno);
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("内部转账查询异常");
        }
        return JSON.parseObject(s, QueryPaymentResult.class);
    }

    @Override
    public WithdrawalResult withdrawal(String oidPartner, String priKey, String payeeNo, String drawNo,
                                       BigDecimal amt, BigDecimal fee, String postscript, String password, String random_key, String ip,
                                       String notifyUrl, String linked_acctno, String phone, Date registerTime, String frmsWareCategory) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        WithDrawalParams params = new WithDrawalParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setCheck_flag("Y");
        params.setNotify_url(lianLianInfo.getHost() + notifyUrl);
        if (StringUtils.isNotEmpty(linked_acctno)) {
            params.setLinked_acctno(linked_acctno);
        }
        // 设置商户订单信息
        WithDrawalOrderInfo orderInfo = new WithDrawalOrderInfo();
        orderInfo.setTxn_seqno(drawNo);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amt.doubleValue());
        orderInfo.setFee_amount(fee.doubleValue());
        if(ArithmeticUtils.gte(amt, BigDecimal.valueOf(49999))){
            orderInfo.setPostscript("提现");
        }
        params.setOrderInfo(orderInfo);

        // 设置付款方信息
        WithDrawalPayerInfo payerInfo = new WithDrawalPayerInfo();
        payerInfo.setPayer_type("USER");
        payerInfo.setPayer_id(payeeNo);
        payerInfo.setPassword(password);
        payerInfo.setRandom_key(random_key);
        params.setPayerInfo(payerInfo);

        String registerTimeStr = DateTimeUtils.format(registerTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo(frmsWareCategory, payeeNo, phone, registerTimeStr, "提现");
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("13");
        riskItemInfo.setUser_auth_flag("1");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));

        String url = "https://accpapi.lianlianpay.com/v1/txn/withdrawal";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("提现异常");
        }
        try {
            WithdrawalResult result = JSON.parseObject(s, WithdrawalResult.class);
            if (result == null || !("8889".equals(result.getRet_code()) || "0000".equals(result.getRet_code()) || "8888".equals(result.getRet_code()))) {
                throw new CrmebException("提现异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("提现异常:" + e.getMessage());
        }
    }


    @Override
    public WithdrawalCheckResult withdrawalCheck(String oidPartner, String priKey, String txn_seqno,
                                                 String total_amount, String check_result, String check_reason, String fee) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        WithdrawalCheckParams params = new WithdrawalCheckParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        WithDrawalOrderInfo orderInfo = new WithDrawalOrderInfo();
        orderInfo.setTxn_seqno(txn_seqno);
        orderInfo.setTotal_amount(Double.valueOf(total_amount));
        orderInfo.setFee_amount(Double.valueOf(fee));
        params.setOrderInfo(orderInfo);

        WithDrawalCheckInfo checkInfo = new WithDrawalCheckInfo();
        checkInfo.setCheck_result(check_result);
        checkInfo.setCheck_reason(check_reason);
        params.setCheckInfo(checkInfo);


        String url = "https://accpapi.lianlianpay.com/v1/txn/withdrawal-check";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("提现复合异常:" + txn_seqno);
        }
        try {
            WithdrawalCheckResult result = JSON.parseObject(s, WithdrawalCheckResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("提现复合异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("WithdrawalCheckResult:" + e.getMessage());
        }
    }

    @Override
    public QueryWithdrawalResult queryWithdrawal(String oidPartner, String priKey, String txnSeqno) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        QueryWithdrawalParams params = new QueryWithdrawalParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setTxn_seqno(txnSeqno);

        String url = "https://accpapi.lianlianpay.com/v1/txn/query-withdrawal";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("提现查询异常:" + txnSeqno);
        }
        return JSON.parseObject(s, QueryWithdrawalResult.class);

    }

    @Override
    public ApplyPasswordElementResult getPasswordToken(String oidPartner, String priKey, String userId, String payCode, String pyee_name, BigDecimal amount, String scan) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        ApplyPasswordElementParams params = new ApplyPasswordElementParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        params.setTxn_seqno(payCode);
        params.setPyee_name(pyee_name);
        if (amount != null) {
            params.setAmount(amount.doubleValue());
        }
        params.setEncrypt_algorithm("SM2");
        params.setPassword_scene(scan);
        params.setFlag_chnl("PCH5");
        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/apply-password-element";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("获取密码控件失败" + pyee_name);
        }
        try {
            ApplyPasswordElementResult result = JSON.parseObject(s, ApplyPasswordElementResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("获取密码控件失败：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("获取密码控件失败:" + e.getMessage());
        }
    }

    /**
     * 获取资金流水
     */
    @Override
    public AcctSerialResult queryAcctSerial(String oidPartner, String priKey, String userId, String userType,
                                            String dateStart, String endStart, String flagDc, String pageNo) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();

        AcctSerialParams params = new AcctSerialParams(timestamp, oidPartner, userId, userType, "USEROWN_AVAILABLE",
                dateStart, endStart, flagDc, pageNo, "10", "DESC");
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-acctserial";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            return new AcctSerialResult();
        }
        return JSON.parseObject(s, AcctSerialResult.class);
    }

    @Override
    public ValidationSmsResult validationSms(String oidPartner, String priKey, String payer_id, String txn_seqno,
                                             String total_amount, String token, String verify_code) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        ValidationSmsParams params = new ValidationSmsParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setPayer_type("USER");
        params.setPayer_id(payer_id);
        params.setTxn_seqno(txn_seqno);
        params.setTotal_amount(total_amount);
        params.setToken(token);
        params.setVerify_code(verify_code);
        String url = "https://accpapi.lianlianpay.com/v1/txn/validation-sms";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("短信二次验证失败" + payer_id);
        }
        try {
            ValidationSmsResult result = JSON.parseObject(s, ValidationSmsResult.class);
            if (result == null || !("8889".equals(result.getRet_code()) || "0000".equals(result.getRet_code()) || "8888".equals(result.getRet_code()))) {
                throw new CrmebException("短信二次验证未成功：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("短信二次验证未成功:" + e.getMessage());
        }
    }


    @Override
    public QueryLinkedAcctResult queryLinkedAcct(String oidPartner, String priKey, String payer_id) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        QueryLinkedAcctParams params = new QueryLinkedAcctParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(oidPartner);
        params.setUser_id(payer_id);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-linkedacct";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("查询绑卡账户异常:" + payer_id);
        }
        QueryLinkedAcctResult result = JSON.parseObject(s, QueryLinkedAcctResult.class);
        return result;
    }

    @Override
    public ReceiptProduceResult receiptProduce(String oidPartner, String priKey, String txn_seqno, String amt, String Trade_bill_type, String memo, String trade_txn_seqno) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        ReceiptProduceParams params = new ReceiptProduceParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setTxn_time(timestamp);
        params.setTxn_seqno(txn_seqno);
        params.setTrade_txn_seqno(trade_txn_seqno);
        params.setTotal_amount(amt);
        params.setTrade_bill_type(Trade_bill_type);
        params.setMemo(memo);

        String url = "https://accpapi.lianlianpay.com/v1/offlinetxn/receipt-produce";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("申请回执异常:" + txn_seqno);
        }
        try {
            ReceiptProduceResult result = JSON.parseObject(s, ReceiptProduceResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("申请回执异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("申请回执异常:" + e.getMessage());
        }
    }

    @Override
    public ReceiptDownloadResult receiptDownload(String oidPartner, String priKey, String receipt_accp_txno, String token) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        ReceiptDownloadParams params = new ReceiptDownloadParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setReceipt_accp_txno(receipt_accp_txno);
        params.setToken(token);

        String url = "https://accpapi.lianlianpay.com/v1/offlinetxn/receipt-download";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("申请下载异常:" + receipt_accp_txno);
        }
        ReceiptDownloadResult result = JSON.parseObject(s, ReceiptDownloadResult.class);
        return result;
    }

    @Override
    public FindPasswordApplyResult findPasswordApply(String oidPartner, String priKey, String user_id, String linked_acctno, String ip) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        FindPasswordApplyParams params = new FindPasswordApplyParams(timestamp, oidPartner, user_id);
        if (StringUtils.isNotEmpty(linked_acctno)) {
            params.setLinked_acctno(linked_acctno);
        }
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/find-password-apply";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("找回密码请求错误:" + user_id);
        }
        try {
            FindPasswordApplyResult result = JSON.parseObject(s, FindPasswordApplyResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("找回密码请求异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("找回密码请求异常:" + e.getMessage());
        }
    }

    @Override
    public FindPasswordVerifyResult findPasswordVerify(String oidPartner, String priKey, String user_id, String token,
                                                       String verify_code, String random_key, String password) {

        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        FindPasswordVerifyParams params = new FindPasswordVerifyParams(timestamp, oidPartner, user_id);
        params.setToken(token);
        params.setVerify_code(verify_code);
        params.setRandom_key(random_key);
        params.setPassword(password);

        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/find-password-verify";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("找回密码验证请求错误:" + user_id);
        }
        try {
            FindPasswordVerifyResult result = JSON.parseObject(s, FindPasswordVerifyResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("找回密码验证请求异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("找回密码验证请求异常:" + e.getMessage());
        }
    }


    @Override
    public QueryCnapsCodeResult queryCnapsCode(String oidPartner, String priKey, String bank_code, String brabank_name, String city_code) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        QueryCnapsCodeParams params = new QueryCnapsCodeParams(timestamp, oidPartner);
        params.setBank_code(bank_code);
        params.setBrabank_name(brabank_name);
        params.setCity_code(city_code);

        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-cnapscode";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("未找到支持的联行号" + bank_code);
        }
        QueryCnapsCodeResult result = JSON.parseObject(s, QueryCnapsCodeResult.class);
        return result;
    }

    @Override
    public LztTransferResult transfer(String oidPartner, String priKey, String payerId, String txnPurpose, String txn_seqno,
                                      String amt, String feeAmt, String pwd, String random_key, String payee_type,
                                      String bank_acctno, String bank_code, String bank_acctname,
                                      String cnaps_code, String postscript, String ip, String phone, Date registerTime, String frmsWareCategory) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        TransferParams params = new TransferParams(timestamp, oidPartner);
        // 风控参数
        String registerTimeStr = DateTimeUtils.format(registerTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo(frmsWareCategory, payerId, phone, registerTimeStr, txnPurpose);
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("13");
        riskItemInfo.setUser_auth_flag("1");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));
        if(ArithmeticUtils.lessEquals(new BigDecimal(amt), BigDecimal.valueOf(49999))){
            postscript = "";
        }
        TransferOrderInfo orderInfo = new TransferOrderInfo(txn_seqno, timestamp, Double.valueOf(amt), txnPurpose, postscript);
        orderInfo.setFee_amount(Double.valueOf(feeAmt));
        params.setOrderInfo(orderInfo);
        TransferPayerInfo payerInfo = new TransferPayerInfo("USER", payerId,
                "USEROWN", pwd, random_key);
        params.setPayerInfo(payerInfo);
        TransferPayeeInfo payeeInfo = new TransferPayeeInfo(payee_type, bank_acctno, bank_code, bank_acctname, cnaps_code);
        params.setPayeeInfo(payeeInfo);
        String url = "https://accpapi.lianlianpay.com/v1/txn/transfer";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("代付失败" + payerId);
        }
        try {
            LztTransferResult result = JSON.parseObject(s, LztTransferResult.class);
            if (result == null || !("8889".equals(result.getRet_code()) || "0000".equals(result.getRet_code()) ||
                    "8888".equals(result.getRet_code()))) {
                throw new CrmebException("代付失败：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("代付失败:" + e.getMessage());
        }
    }

    @Override
    public ChangeRegPhoneApplyResult changeRegPhoneApply(String oidPartner, String priKey, String user_id,
                                                         String reg_phone, String reg_phone_new,
                                                         String password, String random_key, Date registerTime, String ip, String frmsWareCategory) {

        ChangeRegPhoneApplyParams params = new ChangeRegPhoneApplyParams();
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(user_id);
         String txn_seqno = StringUtils.N_TO_10("CRP_");
        params.setTxn_seqno(StringUtils.N_TO_10("CRP_"));
        params.setTxn_time(timestamp);
        params.setNotify_url(lianLianInfo.getHost()+"/api/publicly/payment/callback/lianlian/lzt/" + txn_seqno);
        String registerTimeStr = DateTimeUtils.format(registerTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo(frmsWareCategory, user_id, reg_phone, registerTimeStr, "修改手机号");
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("13");
        riskItemInfo.setUser_auth_flag("1");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));

        params.setReg_phone(reg_phone);
        params.setReg_phone_new(reg_phone_new);
        params.setPassword(password);
        params.setRandom_key(random_key);

        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/change-regphone-apply";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("申请手机号修改失败" + user_id);
        }
        ChangeRegPhoneApplyResult result = JSON.parseObject(s, ChangeRegPhoneApplyResult.class);
        return result;
    }

    @Override
    public ChangeRegPhoneVerifyResult changeRegPhoneVerify(String oidPartner, String priKey, String user_id, String token,
                                                           String txn_seqno, String verify_code_new) {
        ChangeRegPhoneVerifyParams params = new ChangeRegPhoneVerifyParams();
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(user_id);
        params.setTxn_seqno(txn_seqno);
        params.setToken(token);
        params.setVerify_code_new(verify_code_new);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/change-regphone-verify";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("申请手机号验证失败" + user_id);
        }
        ChangeRegPhoneVerifyResult result = JSON.parseObject(s, ChangeRegPhoneVerifyResult.class);
        return result;
    }

    @Override
    public AcctSerialDetailResult acctSerialDetail(String oidPartner, String priKey, String user_id, String user_type, String jno_acct) {
        AcctSerialDetailParams params = new AcctSerialDetailParams();
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(user_id);
        params.setUser_type(user_type);
        params.setJno_acct(jno_acct);

        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-acctserialdetail";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("获取资金流水详情" + user_id);
        }
        AcctSerialDetailResult result = JSON.parseObject(s, AcctSerialDetailResult.class);
        return result;
    }
}
