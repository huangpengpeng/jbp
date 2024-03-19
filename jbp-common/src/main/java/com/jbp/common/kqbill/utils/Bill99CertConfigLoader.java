package com.jbp.common.kqbill.utils;

import com.bill99.crypto.entity.Bill99CertConfig;
import com.jbp.common.kqbill.contants.Bill99ConfigInfo;

public class Bill99CertConfigLoader {

    private static Bill99CertConfig config = new Bill99CertConfig() ;

    public static Bill99CertConfig loadConfig(){
        //默认证书配置
        config.setMerchantDefaultPrivatePath(Bill99ConfigInfo.DE_PRI_PATH);
        config.setMerchantDefaultPrivatePassword(Bill99ConfigInfo.DE_PRI_PWD);
        config.setBill99DefaultPublicPath(Bill99ConfigInfo.DE_PUB_PATH);
        return config;

    }

}
