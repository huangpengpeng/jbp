package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.request.PageParamRequest;

public interface LztAcctOpenService extends IService<LztAcctOpen> {

    LztAcctOpen apply(Integer merId, String userId,  String userType, String returnUrl, String businessScope);


    void refresh(String accpTxno);

    void del(Long id);

    LztAcctOpen getByTxnSeqno(String txnSeqno);

    LztAcctOpen getByAccpTxno(String accpTxno);

    Boolean has(String userId);

    PageInfo<LztAcctOpen> pageList(Integer merId, String userId, String status, PageParamRequest pageParamRequest);

}
