package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.request.PlatformWalletFlowRequest;

import java.math.BigDecimal;

public interface PlatformWalletFlowService extends IService<PlatformWalletFlow> {

    void addByPlatformWalletFlow(Integer type,String operate, String action, String externalNo, String postscript, BigDecimal transferIntegral, BigDecimal platformWalletOrgBalance, BigDecimal platformWalletTagBalance);

}
