package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.response.pay.PayCreateResponse;

import java.util.List;

public interface PayUnifiedOrderMng extends IService<PayUnifiedOrder> {


     PayCreateResponse create(String token, String method);

     PayUnifiedOrder success(String txnSeqno);

     PayUnifiedOrder getByTxnSeqno(Long payUserId, String txnSeqno);

}
