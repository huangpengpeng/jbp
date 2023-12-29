package com.jbp.service.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.request.PlatformWalletFlowRequest;

import java.math.BigDecimal;

public interface WalletFlowService extends IService<WalletFlow> {
    void addByPlatformWalletFlowRequest(Integer type, String operate, String action, String externalNo, BigDecimal transferIntegral, BigDecimal walletOrgBalance, BigDecimal walletTagBalance, Long uid, String postscript);
}
