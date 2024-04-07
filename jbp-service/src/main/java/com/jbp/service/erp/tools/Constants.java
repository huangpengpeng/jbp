package com.jbp.service.erp.tools;

public final  class Constants {


	/**
	 * 请求字符集
	 */
	public final static String  CHARSET="utf-8";
	/**
	 * 授权跳转地址
	 */
	public final static String AUTH_URL="https://openweb.jushuitan.com/auth?app_key=[app_key]&timestamp=[timestamp]&charset=[charset]&sign=[sign]&state=[state]";
	/**
	 * 聚水潭CONFIG 名字
	 */
	public final static String ERP_ASSESS_TOKEN_CNFIG="ERP_ASSESS_TOKEN_JUSHUITAN_CONFIG";
	
	public final static String REQUEST_CHARSET="application/x-www-form-urlencoded;charset=UTF-8";
	
	/**
	 * 获取TOKEN
	 */
	public final static String ACCESS_TOKEN_URL="https://openapi.jushuitan.com/openWeb/auth/accessToken";
	/**
	 * 刷新TOKEN
	 */
	public final static String ACCESS_TOKEN_REFRESH_URL="https://openapi.jushuitan.com/openWeb/auth/refreshToken";
	/**
	 * 订单上传
	 */
	public final static String ORDER_UPLOAD="https://openapi.jushuitan.com/open/jushuitan/orders/upload";
	/**
	 * 发货通知
	 */
	public final static String shipCallApi="https://[domain]/Jwebmalladmin/jushuitan/callApi1";
	/**
	 * 取消通知
	 */
	public final static String cancelCallApi="https://[domain]/Jwebmalladmin/jushuitan/callApi2";
	/**
	 * 库存变动通知
	 */
	public final static String repCallApi="https://[domain]/Jwebmalladmin/jushuitan/callApi3";
	/**
	 * 售后通知
	 */
	public final static String refundCallApi="https://[domain]/Jwebmalladmin/jushuitan/callApi4";
}
