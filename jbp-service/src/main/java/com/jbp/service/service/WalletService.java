package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.request.WalletRequest;

import java.math.BigDecimal;

public interface WalletService extends IService<Wallet> {
    Wallet add(WalletRequest walletRequest);

    Wallet getType(Long uid, Integer type);

    BigDecimal subtract(BigDecimal balance, BigDecimal transferIntegral);


    Boolean reduce(Integer type, String operate, String externalNo, String postscript, Long uid, BigDecimal transferIntegral);

}
