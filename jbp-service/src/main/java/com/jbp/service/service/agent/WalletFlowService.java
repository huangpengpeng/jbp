package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.List;

public interface WalletFlowService extends IService<WalletFlow> {
    WalletFlow add(Integer uid , Integer type, BigDecimal amt, String operate, String action, String externalNo,
                   BigDecimal orgBalance, BigDecimal tagBalance, String postscript);


}