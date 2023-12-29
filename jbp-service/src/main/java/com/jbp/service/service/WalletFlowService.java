package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.WalletFlow;

import java.math.BigDecimal;

public interface WalletFlowService extends IService<WalletFlow> {
    WalletFlow add(Integer uid , Integer type, BigDecimal amt, String operate, String action, String externalNo,
                   BigDecimal orgBalance, BigDecimal tagBalance, String postscript);
}
