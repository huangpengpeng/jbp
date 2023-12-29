package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.PlatformWallet;

import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;

import java.math.BigDecimal;


public interface PlatformWalletService extends IService<PlatformWallet> {

    PageInfo<PlatformWallet> pageList(PlatformWalletRequest request, PageParamRequest pageParamRequest);

    PlatformWallet add(PlatformWalletRequest platformWalletRequest);


    PlatformWallet getType(Integer type);
    public  Boolean reduce(Integer type,String operate, String externalNo, String postscript, Long uid, BigDecimal transferIntegral);
    void    transferToUser(Integer type,String action,String operate,String externalNo,String postscript,Long uid,BigDecimal transferIntegral);

}
