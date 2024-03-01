package com.jbp.service.service;

import com.jbp.common.lianlian.params.LztFundTransferParams;
import com.jbp.common.lianlian.params.LztOpenacctApplyParams;
import com.jbp.common.lianlian.result.*;

import java.math.BigDecimal;

public interface LztService {

   /**
    * 创建连连用户
    */
   OpenacctApplyResult createUser(String oidPartner, String priKey, String txnSeqno,
                                  String userId, String userType, String notifyUrl, String returnUrl, String flagChnl, String businessScope);

   /**
    * 查询连连用户
    */
   UserInfoResult queryUserInfo(String oidPartner, String priKey, String userId);

   /**
    * 查询连连账户资金
    */
   AcctInfoResult queryAcct(String oidPartner, String priKey, String userId, String userType);


   /**
    * 银行账户申请
    */
   LztOpenacctApplyResult createBankUser(String oidPartner, String priKey, String userId, String txnSeqno, String shopId,
                                         String shopName, String province, String city, String area, String address, String notifyUrl);


   /**
    * 查询银行账户资金
    */
   LztQueryAcctInfoResult queryBankAcct(String oidPartner, String priKey, String userId);

   /**
    * 来账通账户资金划拨
    */
   LztFundTransferResult fundTransfer(String oidPartner, String priKey, String txnSeqno, String userId, String bankAccountNo, String amt, String notifyUrl);

   /**
    * 来账通账户资金划拨查询
    */
   LztQueryFundTransferResult queryFundTransfer(String oidPartner, String priKey, String userId, String accpTxno);

   /**
    * 来账通内部代发申请
    */
   TransferMorepyeeResult transferMorepyee(String oidPartner, String priKey,String payerId, String orderNo, Double amt, String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String notify_url);

   /**
    * 来账通内部代发结果查询
    */
   QueryPaymentResult queryTransferMorepyee(String oidPartner, String priKey,String accpTxno);

   /**
    * 来账通提现
    */
   WithdrawalResult withdrawal(String oidPartner, String priKey,String payeeNo, String drawNo, BigDecimal amt, String postscript, String password, String random_key, String ip, String notifyUrl);

   /**
    * 来账通提现查询
    */
   QueryWithdrawalResult queryWithdrawal(String oidPartner, String priKey,String accpTxno);

   /**
    * 来账通获取密码空间token
    */
   ApplyPasswordElementResult getPasswordToken(String oidPartner, String priKey, String userId, String payCode, String pyee_name, BigDecimal amount, String scan);

}
