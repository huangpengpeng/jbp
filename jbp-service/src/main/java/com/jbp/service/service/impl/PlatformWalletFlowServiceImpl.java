package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.service.dao.PlatformWalletFlowDao;
import com.jbp.service.service.PlatformWalletFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
