package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.request.WalletRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.WalletDao;
import com.jbp.service.service.PlatformWalletService;
import com.jbp.service.service.WalletFlowService;
import com.jbp.service.service.WalletService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
    @Resource
    WalletFlowService walletFlowService;
    @Resource
    PlatformWalletService platformWalletService;
    @Override
    public Wallet add(WalletRequest walletRequest) {
        if (!ObjectUtil.isNull(getType(walletRequest.getUid(), walletRequest.getType()))) {
            throw new RuntimeException(String.format("用户钱包{}类型已存在", walletRequest.getType()));
        }
        Wallet wallet = new Wallet();
        wallet.setType(walletRequest.getType());
        wallet.setUId(walletRequest.getUid());
        wallet.setFreeze(BigDecimal.valueOf(0));
        wallet.setBalance(BigDecimal.valueOf(0));
        save(wallet);
        return wallet;
    }

    @Override
    public Wallet getType(Long uid, Integer type) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUId, uid)
                .eq(Wallet::getType, type);
        return getOne(wrapper);
    }

    @Override
    public BigDecimal subtract(BigDecimal balance, BigDecimal transferIntegral) {
        return balance.subtract(transferIntegral);
    }



    @Override
    public Boolean reduce(Integer type, String operate, String externalNo, String postscript, Long uid, BigDecimal transferIntegral) {
        Wallet wallet = getType(uid, type);
        PlatformWallet type1 = platformWalletService.getType(type);
        if (ArithmeticUtils.less(type1.getBalance(),transferIntegral)) {
            throw new CrmebException();
        }
        BigDecimal walletOrgBalance = wallet.getBalance();
        BigDecimal walletTagBalance = wallet.setBalance(wallet.getBalance().add( transferIntegral)).getBalance();
        boolean walletUpdate = updateById(wallet);
        walletFlowService.addByPlatformWalletFlowRequest(type,operate,"收入",externalNo,transferIntegral,walletOrgBalance,walletTagBalance,uid,postscript);
        return walletUpdate;
    }
}
