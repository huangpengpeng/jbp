package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;

import java.util.List;

public interface PayUnifiedOrderMng extends IService<PayUnifiedOrder> {


     PayCreateResponse create(String token, String method,String openId);

     PayUnifiedOrder callBack(String appKey, String txnSeqno);

     PayQueryResponse query(String appKey, String txnSeqno);

     PayUnifiedOrder refresh(PayUnifiedOrder payUnifiedOrder);

     PayUnifiedOrder getByTxnSeqno(Long payUserId, String txnSeqno);

}
