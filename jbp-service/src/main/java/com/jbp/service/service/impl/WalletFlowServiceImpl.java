package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.service.dao.WalletFlowDao;
import com.jbp.service.service.WalletFlowService;
import com.jbp.service.util.StringUtils;
import com.qcloud.cos.demo.AppendObjectDemo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
@Service
public class WalletFlowServiceImpl extends ServiceImpl<WalletFlowDao, WalletFlow> implements WalletFlowService {



    @Override
    public void addByPlatformWalletFlowRequest(Integer type, String operate, String action, String externalNo, BigDecimal transferIntegral, BigDecimal walletOrgBalance, BigDecimal walletTagBalance, Long uid, String postscript) {
        WalletFlow walletFlow=WalletFlow.builder()
                .uid(uid)
                .walletType(type)
                .action(action)
                .operate(operate)
                .uniqueNo(StringUtils.N_TO_10("PW_"))
                .externalNo(externalNo)
                .postscript(postscript)
                .amt(transferIntegral)
                .orgBalance(walletOrgBalance)
                .tagBalance(walletTagBalance)
                .build();
    save(walletFlow);
    }
}
