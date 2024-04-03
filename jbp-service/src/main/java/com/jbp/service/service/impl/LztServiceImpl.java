package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.client.LLianPayClient;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.LztService;
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
        params.setReturn_url(payInfo.getHost() + returnUrl);
        params.setNotify_url(payInfo.getHost() + notifyUrl);
        params.setUser_type(userType);
        params.setCust_trade_serial_type("OpenNormalUser");
        OpenacctApplyAccountInfo accountInfo = new OpenacctApplyAccountInfo();
        accountInfo.setAccount_type("INNERUSER".equals(userType) ? "PERSONAL_PAYMENT_ACCOUNT" : "ENTERPRISE_PAYMENT_ACCOUNT");
        if("INNERUSER".equals(userType)){
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
        try {
            UserInfoResult result = JSON.parseObject(s, UserInfoResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("查询用户信息异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("查询用户信息异常:" + s);
        }
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
    public LztOpenacctApplyResult createBankUser(String oidPartner, String priKey, String userId, String txnSeqno, String shopId, String shopName, String province, String city, String area, String address, String notifyUrl) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        LztOpenacctApplyParams params = new LztOpenacctApplyParams(userId, txnSeqno, notifyUrl, "USER", shopId, shopName, province, city, area, address);
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_type("USER");
        params.setAcct_type("USEROWN");
        params.setTxn_time(timestamp);
        params.setNotify_url(lianLianInfo.getHost() + params.getNotify_url());

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
        try {
            LztQueryAcctInfoResult result = JSON.parseObject(s, LztQueryAcctInfoResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("查询银行账户资金异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("查询银行账户资金异常:" + s);
        }
    }

    @Override
    public LztFundTransferResult fundTransfer(String oidPartner, String priKey, String txnSeqno, String userId, String bankAccountNo, String amt, String notifyUrl) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        LztFundTransferParams params = new LztFundTransferParams(txnSeqno, userId, bankAccountNo, amt, lianLianInfo.getHost()+notifyUrl);
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
    public LztQueryFundTransferResult queryFundTransfer(String oidPartner, String priKey, String userId, String accpTxno) {
        LztQueryFundTransferParams params = new LztQueryFundTransferParams();
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setUser_id(userId);
        params.setAccp_txno(accpTxno);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-lzt-fund-transfer";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("划拨资金查询异常:" + accpTxno);
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
    public TransferMorepyeeResult transferMorepyee(String oidPartner, String priKey,String payerId, String orderNo, Double amt, String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String notify_url) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        TransferMorepyeeParams params = new TransferMorepyeeParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setFunds_flag("N");
        params.setDirectionalpay_flag("N");
        params.setContinuously_flag("N");
        params.setNotify_url(lianLianInfo.getHost()+notify_url);

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
        // 测试风控参数
        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("2007", payeeId, "", registerTime, txnPurpose);
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
            if (result == null || !"8888".equals(result.getRet_code())) {
                throw new CrmebException("内部转账异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("内部转账异常:" + e.getMessage());
        }
    }

    @Override
    public QueryPaymentResult queryTransferMorepyee(String oidPartner, String priKey,String accpTxno) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        QueryPaymentParams params = new QueryPaymentParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(oidPartner);
        params.setAccp_txno(accpTxno);
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("内部转账查询异常");
        }
        try {
            QueryPaymentResult result = JSON.parseObject(s, QueryPaymentResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("内部转账查询异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("内部转账异常:" + e.getMessage());
        }
    }

    @Override
    public WithdrawalResult withdrawal(String oidPartner, String priKey,String payeeNo, String drawNo,
                                       BigDecimal amt, String postscript, String password, String random_key, String ip, String notifyUrl) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        WithDrawalParams params = new WithDrawalParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setNotify_url(lianLianInfo.getHost()+notifyUrl);

        // 设置商户订单信息
        WithDrawalOrderInfo orderInfo = new WithDrawalOrderInfo();
        orderInfo.setTxn_seqno(drawNo);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amt.doubleValue());
        orderInfo.setPostscript(postscript);
        params.setOrderInfo(orderInfo);

        // 设置付款方信息
        WithDrawalPayerInfo payerInfo = new WithDrawalPayerInfo();
        payerInfo.setPayer_type("USER");
        payerInfo.setPayer_id(payeeNo);
        payerInfo.setPassword(password);
        payerInfo.setRandom_key(random_key);
        params.setPayerInfo(payerInfo);

        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("2007", payeeNo, "", registerTime, "提现");
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("13");
        riskItemInfo.setUser_auth_flag("1");

        String url = "https://accpapi.lianlianpay.com/v1/txn/withdrawal";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("提现异常");
        }
        try {
            WithdrawalResult result = JSON.parseObject(s, WithdrawalResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("提现异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("提现异常:" + e.getMessage());
        }
    }

    @Override
    public QueryWithdrawalResult queryWithdrawal(String oidPartner, String priKey,String accpTxno) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        QueryWithdrawalParams params = new QueryWithdrawalParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(oidPartner);
        params.setAccp_txno(accpTxno);

        String url = "https://accpapi.lianlianpay.com/v1/txn/query-withdrawal";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("提现查询异常:" + accpTxno);
        }
        try {
            QueryWithdrawalResult result = JSON.parseObject(s, QueryWithdrawalResult.class);
            if (result == null || "0000".equals(result.getRet_code())) {
                throw new CrmebException("提现查询异常：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("提现查询异常:" + e.getMessage());
        }
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
        params.setAmount(amount.doubleValue());
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
                                            String dateStart, String endStart, String flagDc,  String pageNo) {
        LianLianPayInfoResult lianLianInfo = lianLianPayService.get();
        String timestamp = LLianPayDateUtils.getTimestamp();
        AcctSerialParams params = new AcctSerialParams(timestamp, oidPartner, userId, userType, "USEROWN_AVAILABLE",
                dateStart, endStart, flagDc, pageNo, "10", "DESC");
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-acctserial";
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(s)) {
            throw new CrmebException("获取资金流水失败" + userId);
        }
        try {
            AcctSerialResult result = JSON.parseObject(s, AcctSerialResult.class);
            if (result == null || !"0000".equals(result.getRet_code())) {
                throw new CrmebException("获取资金流水失败：" + result == null ? "请求结果为空" : result.getRet_msg());
            }
            return result;
        } catch (Exception e) {
            throw new CrmebException("获取资金流水失败:" + e.getMessage());
        }
    }
}
