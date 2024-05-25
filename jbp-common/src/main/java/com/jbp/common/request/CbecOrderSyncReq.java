package com.jbp.common.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CbecOrderSyncReq implements Serializable {
    private String bizId;
    private String createTime;
    private String flowPrice;
    private String freightPrice;
    private String goodsPrice;
    private String orderSn;
    private String orderStatus;
    private String payPoints;
    private String paymentTime;
    private List<CbecOrderSyncGoodsReq> orderItems;
}
