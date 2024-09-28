package com.jbp.common.jdpay.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 分佣请求类
 */
public class JdPaySendCommissionRequest implements Serializable {


    /**
     * 分账目标子商户订单号[支付的分账单号]
     */
    private String orderNo;

    /**
     * 来源商户号  04
     */
    private String platNo;

    /**
     * 目标商户号  06
     */
    private String merchantNo;

    /**
     * @see com.jbp.common.jdpay.vo.JdPayCommissionInfo
     */
    private List<JdPayCommissionInfo> commissionInfos;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPlatNo() {
        return platNo;
    }

    public void setPlatNo(String platNo) {
        this.platNo = platNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public List<JdPayCommissionInfo> getCommissionInfos() {
        return commissionInfos;
    }

    public void setCommissionInfos(List<JdPayCommissionInfo> commissionInfos) {
        this.commissionInfos = commissionInfos;
    }
}
