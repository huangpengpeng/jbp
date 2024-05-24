package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.request.PageParamRequest;

import java.util.Date;

public interface LztAcctApplyService extends IService<LztAcctApply> {

    LztAcctApply apply(Integer merId, String userId, String shopId, String shopName,
                       String province, String city, String area, String address, String openBank);

    LztAcctApply yopApply(String userId, String merchantName, String openBankCode,
                          String openAccountType, String certificateNo, String socialCreditCodeImageUrl,
                          String legalCardImageFont, String legalCardImageBack, String legalMobile,
                          String operatorName, String operatorMobile, String benefitName, String benefitIdNo,
                          String benefitStartDate, String benefitStartEnd, String benefitAddress);

    LztAcctApply refresh(String userId, String notifyInfo);

    LztAcctApply getByUserId(String userId);

    LztAcctApply getByTxnSeqno(String txnSeqno);

    PageInfo<LztAcctApply> pageList(Integer merId, String userId, String username, String status, PageParamRequest pageParamRequest);

    void del(Long id);
}
