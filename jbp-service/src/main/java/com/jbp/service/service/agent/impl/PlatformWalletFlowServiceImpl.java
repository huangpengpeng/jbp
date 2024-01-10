package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.service.dao.agent.PlatformWalletFlowDao;
import com.jbp.service.service.agent.PlatformWalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class PlatformWalletFlowServiceImpl extends ServiceImpl<PlatformWalletFlowDao, PlatformWalletFlow> implements PlatformWalletFlowService {

    @Override
    public PlatformWalletFlow add(Integer type, String operate, String action, String externalNo, String postscript,
                                  BigDecimal amt, BigDecimal orgBalance, BigDecimal tagBalance) {
        PlatformWalletFlow platformWalletFlow = PlatformWalletFlow
                .builder()
                .walletType(type)
                .action(action)
                .operate(operate)
                .uniqueNo(StringUtils.N_TO_10("PW_"))
                .externalNo(externalNo)
                .postscript(postscript)
                .amt(amt)
                .orgBalance(orgBalance)
                .tagBalance(tagBalance).build();
        save(platformWalletFlow);
        return platformWalletFlow;
    }



}
