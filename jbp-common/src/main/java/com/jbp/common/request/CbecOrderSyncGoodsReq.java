package com.jbp.common.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CbecOrderSyncGoodsReq implements Serializable {
    private String flowPrice;
    private String goodPrice;
    private String goodsName;
    private String num;
    private String unitPrice;
}
