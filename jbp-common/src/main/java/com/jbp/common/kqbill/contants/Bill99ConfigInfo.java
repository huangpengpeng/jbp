package com.jbp.common.kqbill.contants;


import com.jbp.common.kqbill.utils.Bill99CertConfigLoader;
import com.jbp.common.kqbill.utils.PropertiesLoader;

public class Bill99ConfigInfo {

    /**
     * 加载快钱UMGW接口配置文件
     */
    private static final PropertiesLoader propertiesLoader =
            new PropertiesLoader("classpath:bill99-interface-umgw-config.properties");


    /**
     * 接口商户号信息
     */
    public static final String MEMBER_CODE = propertiesLoader.getProperty("merchant.memberCode");
    public static final String MERCHANT_ID = propertiesLoader.getProperty("merchant.merchantId");
    public static final String TERMINAL_ID = propertiesLoader.getProperty("merchant.terminalId");

    /**
     * 请求快钱环境配置信息
     */
    public static final String UMGW_ENV = propertiesLoader.getProperty("bill99.umgw.env");

    /**
     * 通用接口地址配置信息
     */
    public static final String UMGW_SANDBOX_URL = propertiesLoader.getProperty("bill99.umgw.sandbox.url");
    public static final String UMGW_PROD_URL = propertiesLoader.getProperty("bill99.umgw.prod.url");

    /**
     * 进件接口地址配置信息
     */
    public static final String UMGW_BOSS_SANDBOX_URL = propertiesLoader.getProperty("bill99.umgw.boss.sandbox.url");
    public static final String UMGW_BOSS_PROD_URL = propertiesLoader.getProperty("bill99.umgw.boss.prod.url");

    /**
     * 进件接口messageType配置信息
     */
    public static final String UMGW_BOSS_MESSAGETYPE_PREFIX = propertiesLoader.getProperty("bill99.umgw.boss.messageType.prefix");

    /**
     * 证书库类型
     */
    public static final String STORE_TYPE = propertiesLoader.getProperty("keyStore.storeType");

    /**
     *默认证书配置信息
     */
    public static final String DE_PRI_PATH = propertiesLoader.getProperty("default.privateKey.path");
    public static final String DE_PRI_PWD = propertiesLoader.getProperty("default.privateKey.password");
    public static final String DE_PUB_PATH = propertiesLoader.getProperty("default.99bill.publicKey.path");




    /**
     *SSL通信配置信息
     */
    public static final String SSL_PRI_PATH = propertiesLoader.getProperty("ssl.privateKey.path");
    public static final String SSL_PRI_PWD = propertiesLoader.getProperty("ssl.privateKey.password");
    public static final String SO_TIMEOUT = propertiesLoader.getProperty("ssl.socket.timeout");
    public static final String CONN_TIMEOUT = propertiesLoader.getProperty("ssl.conn.timeout");
    public static final String TLS_VERSION = propertiesLoader.getProperty("ssl.tls.version");

}
