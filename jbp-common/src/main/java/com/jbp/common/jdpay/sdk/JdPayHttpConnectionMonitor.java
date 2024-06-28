package com.jbp.common.jdpay.sdk;

import com.jbp.common.jdpay.util.JdPayApiUtil;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * 用于监控空闲的连接池连接
 */
public class JdPayHttpConnectionMonitor extends Thread {

    // 轮询检查时间间隔，单位毫秒
    private static final int MONITOR_INTERVAL_MS = 5000;
    // 连接最大空闲时间，单位毫秒
    private static int IDLE_ALIVE_MS = 20000;

    private final HttpClientConnectionManager httpClientConnectionManager;

    private volatile boolean shutdown;

    JdPayHttpConnectionMonitor(HttpClientConnectionManager httpClientConnectionManager, int idleAliveMs) {
        super();
        this.httpClientConnectionManager = httpClientConnectionManager;
        IDLE_ALIVE_MS = idleAliveMs;
        this.shutdown = false;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(MONITOR_INTERVAL_MS);
                    // 关闭无效的连接
                    httpClientConnectionManager.closeExpiredConnections();
                    // 关闭空闲时间超过IDLE_ALIVE_MS的连接
                    httpClientConnectionManager.closeIdleConnections(IDLE_ALIVE_MS, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException e) {
            JdPayApiUtil.logger.error("连接池管理任务异常:", e);
        }
    }

    // 关闭后台连接
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
