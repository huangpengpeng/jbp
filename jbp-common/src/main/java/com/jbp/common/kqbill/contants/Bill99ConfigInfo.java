package com.jbp.common.kqbill.contants;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Bill99ConfigInfo {

    @Autowired
    private Environment environment;


    @PostConstruct
    private void init() {
        Bill99ConfigInfo.UMGW_ENV = environment.getProperty("kq.env");
        Bill99ConfigInfo.DE_PRI_NAME = environment.getProperty("kq.privateKey.name");
        Bill99ConfigInfo.DE_PRI_PWD = environment.getProperty("kq.privateKey.password");
        Bill99ConfigInfo.DE_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.privateKey.path")).getPath();
        Bill99ConfigInfo.DE_PUB_PATH = this.getClass().getResource(environment.getProperty("kq.publicKey.path")).getPath();
        Bill99ConfigInfo.SSL_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.ssl.privateKey.path")).getPath();
        Bill99ConfigInfo.SSL_PRI_PWD = environment.getProperty("kq.ssl.privateKey.password");
    }


//    public void refresh(int type) {
//        if(type==0){
//            Bill99ConfigInfo.DE_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.privateKey.path")).getPath();
//            Bill99ConfigInfo.DE_PUB_PATH = this.getClass().getResource(environment.getProperty("kq.publicKey.path")).getPath();
//            Bill99ConfigInfo.SSL_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.ssl.privateKey.path")).getPath();
//        }
//        if(type==1){
//            Bill99ConfigInfo.DE_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.privateKey.path2")).getPath();
//            Bill99ConfigInfo.DE_PUB_PATH = this.getClass().getResource(environment.getProperty("kq.publicKey.path2")).getPath();
//            Bill99ConfigInfo.SSL_PRI_PATH = this.getClass().getResource(environment.getProperty("kq.ssl.privateKey.path2")).getPath();
//        }
//
//    }


    /**
     * 请求快钱环境配置信息
     */
    public static String UMGW_ENV;
    /**
     * 默认证书配置信息
     */
    public static String DE_PRI_PATH;
    public static String DE_PUB_PATH;
    public static String SSL_PRI_PATH;


    public static String DE_PRI_NAME;
    public static String DE_PRI_PWD;


    /**
     * 证书库类型
     */
    public static final String STORE_TYPE = "PKCS12";


    /**
     * SSL通信配置信息
     */

    public static String SSL_PRI_PWD;

    /**
     * 通用接口地址配置信息
     */
    public static final String UMGW_SANDBOX_URL = "https://sandbox.99bill.com:7445/umgw/common/distribute.html";
    public static final String UMGW_PROD_URL = "https://umgw.99bill.com/umgw/common/distribute.html";

    /**
     * 进件接口地址配置信息
     */
    public static final String UMGW_BOSS_SANDBOX_URL = "https://sandbox.99bill.com:7445/umgw-boss/common/distribute.html";
    public static final String UMGW_BOSS_PROD_URL = "https://umgw.99bill.com/umgw-boss/common/distribute.html";

    /**
     * 进件接口messageType配置信息
     */
    public static final String UMGW_BOSS_MESSAGETYPE_PREFIX = "BS,PS1,PS2";

    public static final String SO_TIMEOUT = "60000";
    public static final String CONN_TIMEOUT = "10000";
    public static final String TLS_VERSION = "TLSv1.2";

}
