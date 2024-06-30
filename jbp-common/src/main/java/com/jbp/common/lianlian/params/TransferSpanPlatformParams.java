package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransferSpanPlatformParams implements Serializable {

    private String timestamp;
    private String oid_partner;
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
    private TransferPayeeInfo2 payeeInfo;


}
