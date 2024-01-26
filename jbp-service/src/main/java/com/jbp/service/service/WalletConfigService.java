package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.List;

public interface WalletConfigService extends IService<WalletConfig> {
    PageInfo<WalletConfig> pageList(String name, Integer status, Boolean canWithdraw, Boolean recharge, PageParamRequest pageParamRequest);

    WalletConfig getByType(Integer type);

    void update(Integer id, String name, int status, Boolean canWithdraw, Boolean recharge, int changeType, BigDecimal changeScale);

    List<WalletConfig> getCanDeductionList();
    void update(Integer id, String name, int status, Boolean canDeduction, Boolean canPay, Boolean canWithdraw, Boolean recharge, Boolean canTransfer, BigDecimal changeScale);

}
