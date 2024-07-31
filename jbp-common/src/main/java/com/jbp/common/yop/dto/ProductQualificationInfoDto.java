package com.jbp.common.yop.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductQualificationInfoDto implements Serializable {

    public ProductQualificationInfoDto() {
        this.internetType = Lists.newArrayList("MOBILE");
        this.terminalType = "WECHANT_APPLET";
        this.terminalName = "OAGELESS";
//        this.terminalName = "响灿健康";
        this.terminalTestAccount = "ZH367623";
        this.terminalTestPassword = "123456";
    }

    private List<String> internetType;

    // H5、WECHANT_OFFICIAL_ACCOUNT（微信公众号）、WECHANT_APPLET（微信小程序）、ALIPAY_OFFICIAL_ACCOUNT（支付宝生活号）、ALIPAY_APPLET（支付宝小程序）
    private String terminalType;

    private String terminalName;

    private String terminalTestAccount;

    private String terminalTestPassword;

}
