package com.jbp.common.jdpay.vo;

import java.io.Serializable;

/**
 * 三方聚合下单请求
 */
public class JdPayAggregateCreateOrderRequest implements Serializable {
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
     * 交易描述
     */
    private String tradeRemark;
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
     * 同步通知页面url
     */
    private String pageBackUrl;
    /**
     * 风控信息map-- json串
     */
    private String riskInfo;
    /**
     * 行业分类码
     */
    private String categoryCode;
    /**
     * 业务类型
     */
    private String bizTp;
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 报文格式
     */
    private String messageFormat;
    /**
     * 收货信息
     */
    private String receiverInfo;
    /**
     * 交易场景
     */
    private String sceneType;
    /**
     * 指定支付信息
     */
    private String identity;
    /**
     * 接入方式
     */
    private String accessType;
    /**
     * openId
     */
    private String subOpenId;
    /**
     * appId
     */
    private String subAppId;
    /**
     * 分帐信息
     *
     * @see JdPayDivisionAccount
     */
    private String divisionAccount;
    /**
     * 门店号
     */
    private String storeNum;

    @Override
    public String toString() {
        return "{\"JdPayAggregateCreateOrderRequest\":{"
                + "\"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"createDate\":\"" + createDate + "\""
                + ", \"tradeExpiryTime\":\"" + tradeExpiryTime + "\""
                + ", \"tradeSubject\":\"" + tradeSubject + "\""
                + ", \"tradeType\":\"" + tradeType + "\""
                + ", \"tradeRemark\":\"" + tradeRemark + "\""
                + ", \"currency\":\"" + currency + "\""
                + ", \"userIp\":\"" + userIp + "\""
                + ", \"returnParams\":\"" + returnParams + "\""
                + ", \"goodsInfo\":\"" + goodsInfo + "\""
                + ", \"userId\":\"" + userId + "\""
                + ", \"notifyUrl\":\"" + notifyUrl + "\""
                + ", \"pageBackUrl\":\"" + pageBackUrl + "\""
                + ", \"riskInfo\":\"" + riskInfo + "\""
                + ", \"categoryCode\":\"" + categoryCode + "\""
                + ", \"bizTp\":\"" + bizTp + "\""
                + ", \"orderType\":\"" + orderType + "\""
                + ", \"messageFormat\":\"" + messageFormat + "\""
                + ", \"receiverInfo\":\"" + receiverInfo + "\""
                + ", \"sceneType\":\"" + sceneType + "\""
                + ", \"identity\":\"" + identity + "\""
                + ", \"accessType\":\"" + accessType + "\""
                + ", \"subOpenId\":\"" + subOpenId + "\""
                + ", \"subAppId\":\"" + subAppId + "\""
                + ", \"divisionAccount\":\"" + divisionAccount + "\""
                + ", \"storeNum\":\"" + storeNum + "\""
                + "}}";
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public String getDivisionAccount() {
        return divisionAccount;
    }

    public void setDivisionAccount(String divisionAccount) {
        this.divisionAccount = divisionAccount;
    }

    public String getSubOpenId() {
        return subOpenId;
    }

    public void setSubOpenId(String subOpenId) {
        this.subOpenId = subOpenId;
    }

    public String getBizTp() {
        return bizTp;
    }

    public void setBizTp(String bizTp) {
        this.bizTp = bizTp;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    public String getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
}
