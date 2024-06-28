package com.jbp.common.jdpay.sdk;


import java.io.InputStream;
import java.util.HashMap;

/**
 * 京东支付配置类
 */
public abstract class JdPayConfig {

    /**
     * 接口超时时间全局配置
     */
    private static HashMap<String, Integer> HTTP_READ_TIMEOUT_CONFIG = new HashMap<String, Integer>() {{
        put(JdPayConstant.CREATE_ORDER_URL, 15000);
        put(JdPayConstant.TRADE_QUERY_URL, 5000);
        put(JdPayConstant.REFUND_URL, 5000);
        put(JdPayConstant.REFUND_QUERY_URL, 5000);
        put(JdPayConstant.AGREEMENT_PAY_URL, 10000);
        put(JdPayConstant.AGREEMENT_QUERY_URL, 5000);
        put(JdPayConstant.AGREEMENT_CANCEL_URL, 5000);
    }};

    /**
     * 获取merchantNo
     *
     * @return merchantNo
     */
    public abstract String getMerchantNo();

    /**
     * 获取signKey
     *
     * @return signKey
     */
    public abstract String getSignKey();

    /**
     * 获取 私钥证书
     *
     * @return 私钥证书
     */
    public abstract InputStream getPriCert();

    /**
     * 获取 私钥证书密钥
     *
     * @return 私钥证书密钥
     */
    public abstract String getPriCertPwd();

    /**
     * 获取 公钥证书
     *
     * @return 公钥证书
     */
    public abstract InputStream getPubCert();

    /**
     * 获取域名-新api接口
     *
     * @return 域名-新api接口
     */
    public abstract String getApiDomain();

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     *
     * @return 连接时间
     */
    public int getHttpConnectTimeoutMs() {
        return 6 * 1000;
    }

    /**
     * 设置HTTP(S) 读数据超时时间，单位毫秒
     *
     * @param urlSuffix         api地址除去域名后的路径
     * @param httpReadTimeoutMs 读超时时间，单位毫秒
     */
    public void setHttpReadTimeoutMs(String urlSuffix, int httpReadTimeoutMs) {
        HTTP_READ_TIMEOUT_CONFIG.put(urlSuffix, httpReadTimeoutMs);
    }

    /**
     * 查询HTTP(S) 读数据超时时间，单位毫秒
     *
     * @return 读超时时间，单位毫秒
     */
    public int getHttpReadTimeoutMs(String requestPath) {
        if (!HTTP_READ_TIMEOUT_CONFIG.containsKey(requestPath)) {
            return 15000;
        }
        return HTTP_READ_TIMEOUT_CONFIG.get(requestPath);
    }

    /**
     * 是否自动上报异常请求。默认为 true
     * 若要关闭，子类中实现该函数返回 false 即可。
     */
    public boolean shouldAutoReport() {
        return true;
    }

    /**
     * 进行异常上报的线程的数量
     */
    public int getReportWorkerNum() {
        return 1;
    }

    /**
     * 批量上报，一次报多条异常数据
     */
    public int getReportBatchSize() {
        return 5;
    }

    /**
     * 异常上报缓存消息队列最大数量。
     * 队列满后，不会上报新增的异常信息
     */
    public int getReportQueueMaxSize() {
        return 50;
    }

    /**
     * 是否使用http连接池
     */
    public boolean useHttpConnectPool() {
        return false;
    }

    /**
     * http连接池最大连接数量
     */
    public int getHttpConnectMaxTotal() {
        return 800;
    }

    /**
     * http连接池默认连接数量
     */
    public int getHttpConnectDefaultTotal() {
        return 100;
    }

    /**
     * http连接最大闲置时长，单位毫秒
     */
    public int getHttpConnectIdleAliveMs() {
        return 20000;
    }
}
