package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztSalaryPayer;
import com.jbp.common.model.agent.LztSalaryTransfer;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LztSalaryTransferRequest;

import java.util.List;

public interface LztSalaryTransferService extends IService<LztSalaryTransfer> {



    LztSalaryPayer getSalaryPayer(Integer merId);
    LztSalaryTransfer refresh(String txnSeqno);
    PageInfo<LztSalaryTransfer> pageList(Integer merId, String payerId, String txnSeqno,
                                         String bankAcctNo, String bankAcctName, String status, String time,
                                         PageParamRequest pageParamRequest);
    LztSalaryTransfer getByTxnSeqno(String txnSeqno);

    void create(LztAcct payer, List<LztSalaryTransferRequest> requests);

    void del(LztAcct payer, List<Long> idList);
    LztSalaryTransfer send(LztAcct payer, Long  id, String ip);

    LztSalaryTransfer check(Long id, String checkReturn, String checkReason);
}
