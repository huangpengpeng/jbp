package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RegisterMicroH5Params extends BaseYopRequest {

    private String requestNo;// 商户请求流水号 请求流水号需保证唯一，与‘入网h5页面地址’唯一对应， 如， 未完成注册，返回链接跳转至‘注册用户’页面； 已完成注册，返回链接跳转至‘注册信息’页面；

    private String parentMerchantNo;// 上级商户编号 对应平台商的商户编号

    private String notifyUrl;// 服务器回调地址  注册成功后，易宝将商户请求流水号及对应的商户编号返回到此地址

    private String returnUrl;// 注册用户后返回商户的页面地址

    private String mobile;
}
