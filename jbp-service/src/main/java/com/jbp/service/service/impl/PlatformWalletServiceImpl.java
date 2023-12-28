package com.jbp.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWallet;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;
import com.jbp.common.request.WalletRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.PlatformWalletDao;
import com.jbp.service.service.PlatformWalletService;
import com.jbp.service.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class PlatformWalletServiceImpl extends ServiceImpl<PlatformWalletDao, PlatformWallet> implements PlatformWalletService {
    @Resource
    private PlatformWalletDao platformWalletDao;
    @Resource
    private WalletService walletService;
    @Resource
    private TransactionTemplate transactionTemplate;



    @Override
    public PageInfo<PlatformWallet> pageList(PlatformWalletRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<PlatformWallet> platformWalletLambdaQueryWrapper = new LambdaQueryWrapper<PlatformWallet>()
                .eq(!Objects.isNull(request.getType()),PlatformWallet::getType,request.getType());
        Page<PlatformWallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<PlatformWallet> platformWalletList=platformWalletDao.selectList(platformWalletLambdaQueryWrapper);
        return CommonPage.copyPageInfo(page,platformWalletList);
    }

    @Override
    public void add(PlatformWalletRequest platformWalletRequest) {
        LambdaQueryWrapper<PlatformWallet> lambdaQueryWrapper=new LambdaQueryWrapper<PlatformWallet>()
                .eq(PlatformWallet::getType,platformWalletRequest.getType());
        List<PlatformWallet> list =platformWalletDao.selectList(lambdaQueryWrapper);
        if (list.size()>0){
            throw new RuntimeException("钱包类型已存在");
        }
        PlatformWallet platformWallet = new PlatformWallet();
        platformWallet.setType(platformWalletRequest.getType());
        platformWallet.setBalance(BigDecimal.valueOf(0));
        save(platformWallet);
    }

    @Override
    public void transferToUser(PlatformWalletFlowRequest platformWalletFlowRequest) {
        PlatformWallet platformWallet = getType(platformWalletFlowRequest.getType());
        if (Objects.isNull(platformWallet)){
                add(new PlatformWalletRequest().setType(platformWalletFlowRequest.getType()));
                log.info(String.format("平台钱包类型添加成功"));
        }
        Wallet wallet = walletService.getType(platformWalletFlowRequest.getUid(), platformWalletFlowRequest.getType());
        if (Objects.isNull(wallet)) {
            WalletRequest walletRequest = WalletRequest
                    .builder()
                    .type(platformWalletFlowRequest.getType())
                    .uid(platformWalletFlowRequest.getUid()).build();
            walletService.add(walletRequest);
            log.info(String.format("{}用户钱包类型添加成功",platformWalletFlowRequest.getUid()));
        }
        if ((ArithmeticUtils.gte(BigDecimal.ZERO, platformWallet.getBalance()))){
            throw new CrmebException(StrUtil.format("减少平台积分值不能小于0， type={}, Balance={}", platformWallet.getType(), platformWallet.getBalance()));
        }
        if (ArithmeticUtils.less(platformWallet.getBalance(), platformWalletFlowRequest.getTransferIntegral())) {
            throw new CrmebException(StrUtil.format("减少平台积分不足， type={}, Balance={}", platformWallet.getType(), platformWallet.getBalance()));
        }
        Boolean execute = transactionTemplate.execute(e -> {
           platformWallet.setBalance(platformWallet.getBalance().subtract(platformWalletFlowRequest.getTransferIntegral()));
           wallet.setBalance(wallet.getBalance().add(platformWalletFlowRequest.getTransferIntegral()));
           save(platformWallet);
           walletService.save(wallet);
            return Boolean.TRUE;
        });

    }

    @Override
    public PlatformWallet getType(Integer type) {
        LambdaQueryWrapper<PlatformWallet> platformWalletLambdaQueryWrapper=new LambdaQueryWrapper<PlatformWallet>()
                .eq(PlatformWallet::getType,type);
        return getOne(platformWalletLambdaQueryWrapper);
    }
}
