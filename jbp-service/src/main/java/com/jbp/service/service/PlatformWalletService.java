package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;


public interface PlatformWalletService extends IService<PlatformWallet> {

    PageInfo<PlatformWallet> pageList(PageParamRequest pageParamRequest);

    PlatformWallet getType(Integer type);

    Boolean increase(Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    Boolean reduce(Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    Boolean transferToUser(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

}
