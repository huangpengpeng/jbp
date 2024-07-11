package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class TransferParams {

    public TransferParams(String timestamp, String oid_partner) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.check_flag = "Y";
    }

    /**
     * 时间戳，格式yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * 商户号，ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 是否需要审核
     */
    private String check_flag;

    /**
     * 标识该笔代发交易是否是连续代发，默认：N
     * Y：连续代发
     * N：普通代发
     */
    private String continuously_flag;


    /**
     * 风控标识
     */
    private String risk_item;

    /**
     * 订单信息
     */
    private TransferOrderInfo orderInfo;

    /**
     * 付款方信息
     */
    private TransferPayerInfo payerInfo;

    /**
     * 收款方信息
     */
    private TransferPayeeInfo payeeInfo;
}
