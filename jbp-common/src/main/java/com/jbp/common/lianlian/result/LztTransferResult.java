package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztTransferResult {

    /**
     * 请求结果代码。
     * 0000 表示代发申请成功，最终代发处理结果以异步通知为准。
     * 8888 表示代发需要再次信息短信验证码校验。
     */
    private String ret_code;

    /**
     * 请求结果描述
     */
    private String ret_msg;

    /**
     * ACCP系统分配给平台商户的唯一编号。
     */
    private String oid_partner;

    /**
     * 商户用户唯一编号。用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户。
     */
    private String user_id;

    /**
     * 商户系统唯一交易流水号。
     */
    private String txn_seqno;


    /**
     * 订单总金额，单位为元，精确到小数点后两位。
     */
    private double total_amount;

    /**
     * 手续费
     */
    private double fee_amount;


    /**
     * 请求受理成功时返回
     * ACCP系统交易单号。
     */
    private String accp_txno;

    /**
     * 支付授权令牌，有效期30分钟。当交易需要二次验证的时候，需要通过token调用调用交易二次短信验证接口
     */
    private String token;

    /**
     * 账务日期。ACCP系统交易账务日期，交易成功时返回，格式：yyyyMMdd
     */
    private String accounting_date;

}
