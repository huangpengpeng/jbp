package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 来账通开户返回信息
 */
@Data
@EqualsAndHashCode
public class LztOpenacctApplyResult {

    /**
     * 0000表示交易申请成功，最终支付结果以支付结果异步通知接口为准
     */
    private String ret_code;

    /**
     * 交易返回描述
     */
    private String ret_msg;

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户
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

    /**
     * 用户开户网关地址，用户跳转至该地址完成开户过程。跳转方式：商户前端Get请求该地址。
     */
    private String gateway_url;

}
