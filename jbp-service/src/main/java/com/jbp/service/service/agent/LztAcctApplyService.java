package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LztAcctApply;

public interface LztAcctApplyService extends IService<LztAcctApply> {

    LztAcctApply apply(Integer merId, String userId, String shopId, String shopName, String province, String city, String area, String address);


    LztAcctApply refresh(String userId);


    LztAcctApply getByUserId(String userId);

    LztAcctApply getByTxnSeqno(String txnSeqno);


}
