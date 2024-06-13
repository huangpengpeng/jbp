package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.Date;

public interface LztFundTransferService extends IService<LztFundTransfer> {

    LztFundTransfer fundTransfer(Integer merId, String userId, String bankAccountNo, BigDecimal amt, String postscript);


    LztFundTransfer refresh(String txnSeqno);

    LztFundTransfer getByTxnSeqno(String txnSeqno);

    PageInfo<LztFundTransfer> pageList(Integer merId, String userId, String username,  String bankAccountNo, String txnSeqno,
                                       String accpTxno, Date startTime, Date endTime, PageParamRequest pageParamRequest);

    void autoFundTransfer();


}
