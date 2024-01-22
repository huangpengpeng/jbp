package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WalletRequest;

import java.math.BigDecimal;

public interface WalletService extends IService<Wallet> {
    PageInfo<Wallet> pageList(Integer uid,Integer type, PageParamRequest pageParamRequest);
    Wallet add(Integer uid, Integer type);

    Wallet getByUser(Integer uid, Integer type);

    Boolean increase(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    Boolean reduce(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);


    Boolean transferToPlatform(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    void tradePassword(String phone, String code, String tradePassword);
}
