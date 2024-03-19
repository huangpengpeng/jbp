package com.jbp.common.kqbill.invoke;

import java.security.KeyStore;

public class HttpClientCert {

    /**
     * 客户端证书
     */
    private KeyStore keyStore;

    /**
     * 服务端证书
     */
    private KeyStore trustStore;

    /**
     * 客户端证书密码
     */
    private String keyStorePwd;

    /**
     * 服务端证书密码
     */
    private String trustStorePwd;

    /**
     * 连接超时时间
     */
    private int soTimeout;

    /**
     * 连接建立时间
     */
    private int connTimeout;

    /**
     * SSL版本
     */
    private String SSLVersion;

    public String getSSLVersion() {
        return SSLVersion;
    }

    public void setSSLVersion(String sSLVersion) {
        SSLVersion = sSLVersion;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    public String getTrustStorePwd() {
        return trustStorePwd;
    }

    public void setTrustStorePwd(String trustStorePwd) {
        this.trustStorePwd = trustStorePwd;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }
}
