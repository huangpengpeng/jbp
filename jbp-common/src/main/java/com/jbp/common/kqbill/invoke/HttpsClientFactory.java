package com.jbp.common.kqbill.invoke;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

public class HttpsClientFactory {

    /*
     * @method function 创建连接池
     */
    public CloseableHttpClient createSSLClient( HttpClientCert httpClientCert) throws Exception {
        SSLContext sslContext = null;
        SSLContextBuilder builder = SSLContexts.custom();
        // 加载客户端证书
        if(httpClientCert.getKeyStore() != null) {
            if(StringUtils.isNotEmpty(httpClientCert.getKeyStorePwd())) {
                builder = builder.loadKeyMaterial(httpClientCert.getKeyStore() , null);
            }else {
                builder = builder.loadKeyMaterial(httpClientCert.getKeyStore() , httpClientCert.getKeyStorePwd().toCharArray());
            }
        }
        // 加载服务端证书
        if(httpClientCert.getTrustStore() != null) {
            sslContext = builder.loadTrustMaterial(httpClientCert.getTrustStore(), new TrustSelfSignedStrategy()).build();
        }else {
            /**
             * 客户端验证服务端
             */
            sslContext = builder.loadTrustMaterial(null, new TrustStrategy(){
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] xcs, String string) {
                    return true;
                }
            }).build();
        }
        SSLConnectionSocketFactory sslSocketFactory = getConnectionSocketFactory(sslContext,httpClientCert.getSSLVersion());
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.valueOf(httpClientCert.getSoTimeout()))
                .setConnectTimeout(Integer.valueOf(httpClientCert.getConnTimeout())).build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslSocketFactory).build();
    }

    private SSLConnectionSocketFactory getConnectionSocketFactory(SSLContext sslContext,String SSLVersion) {
        if(StringUtils.isEmpty(SSLVersion)) {
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        }else {
            return new SSLConnectionSocketFactory(sslContext, new String[] { SSLVersion }, null,
                    NoopHostnameVerifier.INSTANCE);
        }
    }


}
