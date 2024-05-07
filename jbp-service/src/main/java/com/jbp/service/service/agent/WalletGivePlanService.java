package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletGivePlan;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletGivePlanRequest;

import java.math.BigDecimal;

public interface WalletGivePlanService extends IService<WalletGivePlan> {
    WalletGivePlan add(User user, WalletConfig walletConfig, BigDecimal amt, String externalNo, String postscript, String planTime);

    void release();

    void cancel(String externalNo);

    PageInfo<WalletGivePlan> pageList(WalletGivePlanRequest request, PageParamRequest pageParamRequest);

}
