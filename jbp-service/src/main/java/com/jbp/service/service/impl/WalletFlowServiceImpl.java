package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.service.dao.WalletFlowDao;
import com.jbp.service.service.WalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
