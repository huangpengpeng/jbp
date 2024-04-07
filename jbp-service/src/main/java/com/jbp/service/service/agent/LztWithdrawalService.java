package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.lianlian.result.WithdrawalResult;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.Date;

public interface LztWithdrawalService extends IService<LztWithdrawal> {

    LztWithdrawal getByTxnSeqno(String txnSeqno);

    LztWithdrawal getByAccpTxno(String accpTxno);

    LztWithdrawal withdrawal(Integer merId, String userId, String drawNo, BigDecimal amt, String postscript, String password, String random_key, String ip);
    LztWithdrawal check(Long id, String checkReturn, String checkReason);

    LztWithdrawal callBack(QueryWithdrawalResult result);

    LztWithdrawal refresh(String txnSeqno);

    PageInfo<LztWithdrawal> pageList(Integer merId, String userId, String txnSeqno, String accpTxno, String status, Date startTime, Date endTime, PageParamRequest pageParamRequest);

    LztWithdrawal detail(Long id);

}
