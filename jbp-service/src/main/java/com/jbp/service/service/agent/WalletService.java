package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.WalletExtResponse;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService extends IService<Wallet> {
    PageInfo<WalletExtResponse> pageList(Integer uid, Integer type, String teamId, String nickname,PageParamRequest pageParamRequest);
    Wallet add(Integer uid, Integer type);

    Wallet getByUser(Integer uid, Integer type);

    Boolean increase(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    Boolean reduce(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    Boolean transferToPlatform(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript);

    void deduction(Integer uid, List<ProductDeduction> deductionList, String externalNo, String postscript);

    void refundDeduction(Integer uid, String externalNo, String postscript);

    Boolean transfer(Integer uid, Integer receiveUserId, BigDecimal amt, Integer type, String postscript);

    Boolean change(Integer uid, BigDecimal amt, Integer type, Integer changeType, String postscript);

    Wallet getCanPayByUser(Integer uid);

    void init();

    void init2();

    void init3();
}
