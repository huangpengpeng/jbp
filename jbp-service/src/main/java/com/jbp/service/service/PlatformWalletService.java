package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.PlatformWallet;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.White;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;

import java.util.List;


public interface PlatformWalletService extends IService<PlatformWallet> {

    PageInfo<PlatformWallet> pageList(PlatformWalletRequest request, PageParamRequest pageParamRequest);

    void add(PlatformWalletRequest platformWalletRequest);

    void transferToUser(PlatformWalletFlowRequest platformWalletFlowRequest);
    PlatformWallet getType(Integer type);
}
