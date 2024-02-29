package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.Date;

public interface LztTransferMorepyeeService extends IService<LztTransferMorepyee> {

    LztTransferMorepyee transferMorepyee(Integer merId, String payerId, String orderNo,
                                         BigDecimal amt, String txnPurpose, String pwd,
                                         String randomKey, String payeeId, String ip, String postscript);

    void callBack(QueryPaymentResult paymentResult);

    void refresh(String accpTxno);

    LztTransferMorepyee getByTxnSeqno(String txnSeqno);

    LztTransferMorepyee getByAccpTxno(String accpTxno);

    PageInfo<LztTransferMorepyee> pageList(Integer merId, String payerId, String payeeId, String txnSeqno,
                                       String accpTxno, String status, Date startTime, Date endTime,
                                           PageParamRequest pageParamRequest);
}
