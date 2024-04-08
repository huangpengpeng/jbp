package com.jbp.common.kqbill.contants;


import com.jbp.common.kqbill.utils.PropertiesLoader;
import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class Bill99ConfigInfo {

    @Autowired
    private Environment environment;

    /**
     * 加载快钱UMGW接口配置文件
     */
    private static PropertiesLoader propertiesLoader ;


    @PostConstruct
    private void init() {
        if(StringUtils.isEmpty(environment.getProperty("kq.config"))){
            return;
        }
        propertiesLoader = new PropertiesLoader("classpath:"+environment.getProperty("kq.config"));
        Bill99ConfigInfo.UMGW_ENV = propertiesLoader.getProperty("kq.env");
        Bill99ConfigInfo.DE_PRI_NAME = propertiesLoader.getProperty("kq.privateKey.name");
        Bill99ConfigInfo.DE_PRI_PWD =propertiesLoader.getProperty("kq.privateKey.password");
        Bill99ConfigInfo.DE_PRI_PATH = propertiesLoader.getProperty("kq.privateKey.path");
        Bill99ConfigInfo.DE_PUB_PATH = propertiesLoader.getProperty("kq.publicKey.path");
        Bill99ConfigInfo.SSL_PRI_PATH = propertiesLoader.getProperty("kq.ssl.privateKey.path");
        Bill99ConfigInfo.SSL_PRI_PWD = propertiesLoader.getProperty("kq.ssl.privateKey.password");

    }



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
