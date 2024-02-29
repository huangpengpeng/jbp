package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztQueryFundTransferResult {

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
     * ACCP系统交易单号
     */
    private String accp_txno;

    /**
     * 金额，单位为元，精确到小数点后两位
     */
    private String amt;

    /**
     * 支付交易状态
     * CREATE：创建,
     * PROCESS：处理中,
     * SUCCESS：成功,
     * FAIL：失败
     */
    private String txn_status;
}
