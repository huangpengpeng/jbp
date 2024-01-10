package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.agent.WalletFlowDao;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletFlowServiceImpl extends ServiceImpl<WalletFlowDao, WalletFlow> implements WalletFlowService {

    @Override
    public WalletFlow add(Integer uid, Integer type, BigDecimal amt, String operate, String action, String externalNo,
                          BigDecimal orgBalance, BigDecimal tagBalance, String postscript) {
        WalletFlow walletFlow = WalletFlow.builder()
                .uid(uid)
                .walletType(type)
                .action(action)
                .operate(operate)
                .uniqueNo(StringUtils.N_TO_10("UPW_"))
                .externalNo(externalNo)
                .postscript(postscript)
                .amt(amt)
                .orgBalance(orgBalance)
                .tagBalance(tagBalance)
                .build();
        save(walletFlow);
        return walletFlow;
    }


}
