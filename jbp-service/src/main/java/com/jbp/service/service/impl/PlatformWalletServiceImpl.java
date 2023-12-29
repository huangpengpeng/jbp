package com.jbp.service.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;
import com.jbp.common.request.WalletRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.PlatformWalletDao;
import com.jbp.service.service.PlatformWalletFlowService;
import com.jbp.service.service.PlatformWalletService;
import com.jbp.service.service.WalletFlowService;
import com.jbp.service.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;
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
    @Resource
    private PlatformWalletFlowService platformWalletFlowService;




    @Override
    public PageInfo<PlatformWallet> pageList(PlatformWalletRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<PlatformWallet> platformWalletLambdaQueryWrapper = new LambdaQueryWrapper<PlatformWallet>()
                .eq(!Objects.isNull(request.getType()), PlatformWallet::getType, request.getType());
        Page<PlatformWallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<PlatformWallet> platformWalletList = platformWalletDao.selectList(platformWalletLambdaQueryWrapper);
        return CommonPage.copyPageInfo(page, platformWalletList);
    }

    @Override
    public PlatformWallet add(PlatformWalletRequest platformWalletRequest) {
        LambdaQueryWrapper<PlatformWallet> lambdaQueryWrapper = new LambdaQueryWrapper<PlatformWallet>()
                .eq(PlatformWallet::getType, platformWalletRequest.getType());
        List<PlatformWallet> list = platformWalletDao.selectList(lambdaQueryWrapper);
        if (list.size() > 0) {
            throw new RuntimeException("钱包类型已存在");
        }
        PlatformWallet platformWallet = new PlatformWallet();
        platformWallet.setType(platformWalletRequest.getType());
        platformWallet.setBalance(BigDecimal.valueOf(0));
        save(platformWallet);
        return platformWallet;
    }



    public PlatformWallet getType(Integer type) {
        LambdaQueryWrapper<PlatformWallet> platformWalletLambdaQueryWrapper = new LambdaQueryWrapper<PlatformWallet>()
                .eq(PlatformWallet::getType, type);
        return getOne(platformWalletLambdaQueryWrapper);
    }

    @Override
    public void transferToUser(Integer type, String action, String operate, String externalNo, String postscript, Long uid, BigDecimal transferIntegral) {
        PlatformWallet platformWallet = getType(type);
        if (Objects.isNull(platformWallet)) {
            platformWallet=  add(new PlatformWalletRequest().setType(type));
            log.info(String.format("平台钱包类型添加成功"));
        }
        Wallet wallet = walletService.getType(uid,type);
        if (Objects.isNull(wallet)) {
            WalletRequest walletRequest = WalletRequest
                    .builder()
                    .type(type)
                    .uid(uid).build();
            wallet=walletService.add(walletRequest);
            log.info(String.format("{}用户钱包类型添加成功", uid));
        }
        if (ArithmeticUtils.less(platformWallet.getBalance(),transferIntegral)) {
            throw new CrmebException(StrUtil.format("减少平台积分不足， type={}, Balance={}", platformWallet.getType(), platformWallet.getBalance()));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean reduce = reduce(type, operate, externalNo, postscript, uid, transferIntegral);
            Boolean walletReduce= walletService.reduce(type,operate,externalNo,postscript,uid,transferIntegral);
            if(!reduce){
                throw new CrmebException();
            }
            if(!walletReduce){
                throw new CrmebException();
            }
            return Boolean.TRUE;
        });
        if(!execute){
            throw new CrmebException();
        }
    }
    @Override
    public  Boolean reduce(Integer type,String operate, String externalNo, String postscript, Long uid, BigDecimal transferIntegral){
        PlatformWallet platformWallet = getType(type);
        if (ArithmeticUtils.less(platformWallet.getBalance(),transferIntegral)) {
            throw new CrmebException();
        }
        BigDecimal platformWalletOrgBalance = platformWallet.getBalance();
        BigDecimal platformWalletTagBalance = platformWallet.setBalance(platformWallet.getBalance().subtract(transferIntegral)).getBalance();
        boolean platformWalletUpdate = updateById(platformWallet);
        platformWalletFlowService.addByPlatformWalletFlow(type, operate,"支出",externalNo,postscript,transferIntegral,platformWalletOrgBalance,platformWalletTagBalance);
        return platformWalletUpdate;
    }
}
