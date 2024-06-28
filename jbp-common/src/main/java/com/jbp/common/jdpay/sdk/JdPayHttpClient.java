package com.jbp.common.jdpay.sdk;


import com.jbp.common.jdpay.exception.JdPayException;
import com.jbp.common.jdpay.util.JdPayApiUtil;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JdPayHttpClient {

    private CloseableHttpClient httpClient = null;

    public JdPayHttpClient() {
    }

    /**
     * 远程调用
     *
     * @param jdPayConfig 京东支付配置
     * @param urlSuffix   接口url后缀
     * @param request     请求参数
     * @return 返回参数看
     * @throws Exception 远程调用异常信息
     */
    public String execute(JdPayConfig jdPayConfig, String urlSuffix, String request) {
        HttpClient httpClient = buildHttpClient(jdPayConfig);
        String url = jdPayConfig.getApiDomain() + urlSuffix;
        int connectTimeoutMs = jdPayConfig.getHttpConnectTimeoutMs();
        int readTimeoutMs = jdPayConfig.getHttpReadTimeoutMs(urlSuffix);
        try {
            return this.sendRequest(httpClient, url, request, connectTimeoutMs, readTimeoutMs);
        } catch (IOException e) {
            throw new JdPayException("HTTP read timeout");
        }
    }

    /**
     * 获取httpClient
     *
     * @param jdPayConfig 京东支付配置
     * @return httpClient
     */
    private HttpClient buildHttpClient(JdPayConfig jdPayConfig) {
        if (jdPayConfig.useHttpConnectPool()) {
            return getHttpClientByPoolingConnectionManager(jdPayConfig);
        } else {
            return getHttpClientByBasicConnectionManager();
        }
    }

    /**
     * 发送请求
     *
     * @param httpClient       httpClient
     * @param url              请求地址
     * @param data             请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs    读超时时间，单位是毫秒
     * @return api接口返回参数
     */
    private String sendRequest(HttpClient httpClient, String url, String data, int connectTimeoutMs, int readTimeoutMs) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, JdPayConstant.UTF8);
        httpPost.addHeader(JdPayConstant.CONTENT_TYPE, JdPayConstant.APPLICATION_JSON);
        httpPost.addHeader(JdPayConstant.UA, JdPayConstant.USER_AGENT);
        httpPost.setEntity(postEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) {
            throw new JdPayException(String.format("httpStatusCode: %s", statusCode));
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, JdPayConstant.UTF8);
    }

    private HttpClient getHttpClientByBasicConnectionManager() {
        HttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register(JdPayConstant.HTTP, PlainConnectionSocketFactory.getSocketFactory())
                        .register(JdPayConstant.HTTPS, SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );
        return HttpClientBuilder.create()
                // 连接池
                .setConnectionManager(connManager)
                // 重试策略
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    private HttpClient getHttpClientByPoolingConnectionManager(JdPayConfig jdPayConfig) {
        if (httpClient != null) {
            return httpClient;
        }
        synchronized (this) {
            if (httpClient == null) {
                long start = System.currentTimeMillis();
                PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(
                        RegistryBuilder.<ConnectionSocketFactory>create()
                                .register(JdPayConstant.HTTP, PlainConnectionSocketFactory.getSocketFactory())
                                .register(JdPayConstant.HTTPS, SSLConnectionSocketFactory.getSocketFactory())
                                .build()
                );
                // 设置最大连接数
                httpClientConnectionManager.setMaxTotal(jdPayConfig.getHttpConnectMaxTotal());
                // 将每个路由默认最大连接数
                httpClientConnectionManager.setDefaultMaxPerRoute(jdPayConfig.getHttpConnectDefaultTotal());
                httpClient = HttpClients.custom()
                        // 设置连接池
                        .setConnectionManager(httpClientConnectionManager)
                        // 连接存活策略
                        .setKeepAliveStrategy(getConnectionKeepAliveStrategy())
                        // 重试策略
                        .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
                // 连接回收策略
                JdPayHttpConnectionMonitor idleConnectionMonitor = new JdPayHttpConnectionMonitor(httpClientConnectionManager, jdPayConfig.getHttpConnectIdleAliveMs());
                idleConnectionMonitor.start();
                JdPayApiUtil.logger.info("初始化http连接池耗时:{}", System.currentTimeMillis() - start);
            }
        }
        return httpClient;
    }

    private ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                // Honor 'keep-alive' header
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && "timeout".equalsIgnoreCase(param)) {
                        try {
                            JdPayApiUtil.logger.info("Keep-Alive指定时长:{}", value);
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
                // Keep alive for 300 seconds only
                return 300 * 1000;
            }
        };
    }
}
