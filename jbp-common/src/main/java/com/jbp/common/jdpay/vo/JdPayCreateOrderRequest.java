package com.jbp.common.jdpay.vo;


import java.io.Serializable;


public class JdPayCreateOrderRequest implements Serializable {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单总金额
     */
    private String tradeAmount;
    /**
     * 订单创建时间
     */
    private String createDate;
    /**
     * 订单时效时间 分钟
     */
    private String tradeExpiryTime;
    /**
     * 交易名称
     */
    private String tradeSubject;
    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易场景
     */
    private String sceneType;
    /**
     * 交易描述
     */
    private String tradeRemark;

    private String extendParam;
    /**
     * 请求的端
     */
    private String clientType;
    /**
     * 币种
     */
    private String currency;
    /**
     * 用户ip
     */
    private String userIp;
    /**
     * 回传字段
     */
    private String returnParams;
    /**
     * 商品信息list-- json串
     */
    private String goodsInfo;
    /**
     * 商户用户标识
     */
    private String userId;

    /**
     * 交易异步通知url
     */
    private String notifyUrl;

    /**
     * 签约异步通知url
     */
    private String signNotifyUrl;
    /**
     * 同步通知页面url
     */
    private String pageBackUrl;
    /**
     * 风控信息map-- json串
     */
    private String riskInfo;
    /**
     * 代扣模板号
     */
    private String templateNo;
    /**
     * 业务类型
     */
    private String bizTp;
    /**
     * 门店号
     */
    private String storeNum;

    public String getDivisionAccount() {
        return divisionAccount;
    }

    public void setDivisionAccount(String divisionAccount) {
        this.divisionAccount = divisionAccount;
    }

    private String divisionAccount;

    @Override
    public String toString() {
        return "{ \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"createDate\":\"" + createDate + "\""
                + ", \"tradeExpiryTime\":\"" + tradeExpiryTime + "\""
                + ", \"tradeSubject\":\"" + tradeSubject + "\""
                + ", \"tradeType\":\"" + tradeType + "\""
                + ", \"sceneType\":\"" + sceneType + "\""
                + ", \"tradeRemark\":\"" + tradeRemark + "\""
                + ", \"clientType\":\"" + clientType + "\""
                + ", \"currency\":\"" + currency + "\""
                + ", \"userIp\":\"" + userIp + "\""
                + ", \"returnParams\":\"" + returnParams + "\""
                + ", \"goodsInfo\":\"" + goodsInfo + "\""
                + ", \"userId\":\"" + userId + "\""
                + ", \"notifyUrl\":\"" + notifyUrl + "\""
                + ", \"signNotifyUrl\":\"" + signNotifyUrl + "\""
                + ", \"pageBackUrl\":\"" + pageBackUrl + "\""
                + ", \"riskInfo\":\"" + riskInfo + "\""
                + ", \"templateNo\":\"" + templateNo + "\""
                + ", \"bizTp\":\"" + bizTp + "\""
                + ", \"storeNum\":\"" + storeNum + "\""
                + ", \"divisionAccount\":\"" + divisionAccount + "\""
                + "}"
                ;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }

    public String getBizTp() {
        return bizTp;
    }

    public void setBizTp(String bizTp) {
        this.bizTp = bizTp;
    }

    public String getSignNotifyUrl() {
        return signNotifyUrl;
    }

    public void setSignNotifyUrl(String signNotifyUrl) {
        this.signNotifyUrl = signNotifyUrl;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTradeExpiryTime() {
        return tradeExpiryTime;
    }

    public void setTradeExpiryTime(String tradeExpiryTime) {
        this.tradeExpiryTime = tradeExpiryTime;
    }

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeRemark() {
        return tradeRemark;
    }

    public void setTradeRemark(String tradeRemark) {
        this.tradeRemark = tradeRemark;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getPageBackUrl() {
        return pageBackUrl;
    }

    public void setPageBackUrl(String pageBackUrl) {
        this.pageBackUrl = pageBackUrl;
    }

    public String getRiskInfo() {
        return riskInfo;
    }

    public void setRiskInfo(String riskInfo) {
        this.riskInfo = riskInfo;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(String extendParam) {
        this.extendParam = extendParam;
    }
}
