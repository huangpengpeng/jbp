package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.request.WalletRequest;

public interface WalletService extends IService<Wallet> {
    void add(WalletRequest walletRequest);
    Wallet getType(Long uid, Integer type);
}
