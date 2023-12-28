package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.request.WalletRequest;
import com.jbp.service.dao.WalletDao;
import com.jbp.service.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
    @Override
    public void add(WalletRequest walletRequest) {
        if (ObjectUtil.isNull(getType(walletRequest.getUid(), walletRequest.getType()))) {
            throw new RuntimeException(String.format("用户钱包{}类型已存在", walletRequest.getType()));
        }
        Wallet wallet = new Wallet();
        wallet.setType(walletRequest.getType());
        wallet.setUId(walletRequest.getUid());
        wallet.setFreeze(BigDecimal.valueOf(0));
        wallet.setBalance(BigDecimal.valueOf(0));
        save(wallet);

    }

    @Override
    public Wallet getType(Long uid, Integer type) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUId, uid)
                .eq(Wallet::getType, type);
        return getOne(wrapper);
    }
}
