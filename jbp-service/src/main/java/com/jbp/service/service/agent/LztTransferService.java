package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

public interface LztTransferService extends IService<LztTransfer> {


    LztTransfer create(String payerId, String txnSeqno, BigDecimal amt, String payeeType, String bankAcctNo,
                    String bankCode, String bankAcctName, String cnapsCode, String txnPurpose,
                       String pwd, String random_key, String postscript, String ip);

    LztTransfer refresh(String txnSeqno);

    LztTransfer detail(Long id);

    LztTransfer getByTxnSeqno(String txnSeqno);

    PageInfo<LztTransfer> pageList(Integer merId, String payerId, String txnSeqno,
                                   String bankAcctNo, String bankAcctName, String status, Date startTime, Date endTime,
                                   PageParamRequest pageParamRequest);

    LztTransfer check(Long id, String checkReturn, String checkReason);
}
