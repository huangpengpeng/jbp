package com.jbp.common.jdpay.vo;

public class JdPayAgreementPayResponse extends BaseResponse {
    /**
     * 京东交易订单号
     */
    private String tradeNo;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单总金额
     */
    private String tradeAmount;
    /**
     * 支付完成时间
     */
    private String finishDate;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 优惠金额
     */
    private String discountAmount;
    /**
     * 支付工具
     */
    private String payTool;
    /**
     * ]
     * 掩码卡号
     */
    private String maskCardNo;
    /**
     * 卡类型
     */
    private String cardType;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 白条分期数
     */
    private String installmentNum;


    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPayTool() {
        return payTool;
    }

    public void setPayTool(String payTool) {
        this.payTool = payTool;
    }

    public String getMaskCardNo() {
        return maskCardNo;
    }

    public void setMaskCardNo(String maskCardNo) {
        this.maskCardNo = maskCardNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getInstallmentNum() {
        return installmentNum;
    }

    public void setInstallmentNum(String installmentNum) {
        this.installmentNum = installmentNum;
    }

    @Override
    public String toString() {
        return "{\"tradeNo\":\"" + tradeNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"finishDate\":\"" + finishDate + "\""
                + ", \"tradeStatus\":\"" + tradeStatus + "\""
                + ", \"discountAmount\":\"" + discountAmount + "\""
                + ", \"payTool\":\"" + payTool + "\""
                + ", \"maskCardNo\":\"" + maskCardNo + "\""
                + ", \"cardType\":\"" + cardType + "\""
                + ", \"bankCode\":\"" + bankCode + "\""
                + ", \"installmentNum\":\"" + installmentNum + "\""
                + "}"
                + super.toString()
                ;
    }
}
