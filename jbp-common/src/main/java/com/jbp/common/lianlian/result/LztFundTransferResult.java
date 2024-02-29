package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztFundTransferResult {

    /**
     * 0000表示交易申请成功，最终支付结果以支付结果异步通知接口为准
     */
    private String ret_code;

    /**
     * 交易结果描述
     */
    private String ret_msg;

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;


    /**
     * 入账用户号外部用户号,连连账户
     */
    private String user_id;

    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;

    /**
     * 受托支付订单ACCP系统交易单号
     */
    private String accp_txno;
}
