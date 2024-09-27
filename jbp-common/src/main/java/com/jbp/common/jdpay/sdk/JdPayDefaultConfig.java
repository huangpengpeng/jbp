package com.jbp.common.jdpay.sdk;



import com.jbp.common.jdpay.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/*************************************************
 *
 * 商户默认配置类
 * 商户也可以自行实现JdPayConfig，调整配置属性
 *
 *************************************************/
public class JdPayDefaultConfig extends JdPayConfig {
    private String merchantNo;
    private String merchantNo2;
    private String merchantNo3;
    private String signKey;
    private byte[] priCert;
    private String priCertPwd;
    private byte[] pubCert;
    private String apiDomain;

    private String notifyUrl;

    private String returnUrl;

    public JdPayDefaultConfig(String merchantNo, String merchantNo2, String merchantNo3,String signKey, byte[] priCert,
                              String priCertPwd, byte[] pubCert, String apiDomain, String notifyUrl, String returnUr) {
        this.merchantNo = merchantNo;
        this.merchantNo2 = merchantNo2;
        this.merchantNo3 = merchantNo3;
        this.signKey = signKey;
        this.priCert = priCert;
        this.priCertPwd = priCertPwd;
        this.pubCert = pubCert;
        this.apiDomain = apiDomain;
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUr;
    }

    public JdPayDefaultConfig(String merchantNo, String signKey, String priCertPwd, String apiDomain, String priCertPath, String pubCertPath) {
        this.merchantNo = merchantNo;
        this.signKey = signKey;
        this.priCertPwd = priCertPwd;
        this.apiDomain = apiDomain;

        //加载商户私钥证书
        this.priCert = FileUtil.readFile(priCertPath);
        //加载商户公钥证书
        this.pubCert = FileUtil.readFile(pubCertPath);
    }

    @Override
    public String getMerchantNo() {
        return this.merchantNo;
    }

    @Override
    public String getMerchantNo2() {
        return this.merchantNo2;
    }

    @Override
    public String getMerchantNo3() {
        return this.merchantNo3;
    }

    @Override
    public String getNotifyUrl() {
        return this.notifyUrl;
    }

    @Override
    public String getReturnUrl() {
        return this.returnUrl;
    }

    @Override
    public String getSignKey() {
        return this.signKey;
    }

    @Override
    public InputStream getPriCert() {
        return new ByteArrayInputStream(this.priCert);
    }

    @Override
    public String getPriCertPwd() {
        return this.priCertPwd;
    }

    @Override
    public InputStream getPubCert() {
        return new ByteArrayInputStream(this.pubCert);
    }

    @Override
    public String getApiDomain() {
        return this.apiDomain;
    }
}
