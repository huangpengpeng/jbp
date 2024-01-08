package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.PlatformWalletFlow;

import java.math.BigDecimal;

public interface PlatformWalletFlowService extends IService<PlatformWalletFlow> {

    PlatformWalletFlow add(Integer type, String operate, String action, String externalNo, String postscript, BigDecimal amt, BigDecimal orgBalance, BigDecimal tagBalance);

}
