package com.jbp.front;


import com.jbp.service.service.agent.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExtendsThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ExtendsThread.class);

    private String threadName;

    private WalletService walletService;


    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }

    public ExtendsThread() {

    }

    public ExtendsThread(String threadName) {
        super(threadName);
    }

    @Override
    public void run() {
        getWalletService().transfer(37624, 37630, BigDecimal.valueOf(0.005), 1, "测试"
        );

        logger.info(">>>>>>>>>Extends Thread and Body in run, Thread name:{}", Thread.currentThread().getName());
    }



}
