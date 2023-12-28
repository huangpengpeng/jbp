package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.b2b.PlatformWallet;
import com.jbp.common.model.b2b.WalletFlow;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.PlatformWalletFlowRequest;
import com.jbp.common.request.PlatformWalletRequest;
import com.jbp.service.dao.PlatformWalletDao;
import com.jbp.service.service.PlatformWalletService;
import com.jbp.service.service.WalletService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class PlatformWalletServiceImpl extends ServiceImpl<PlatformWalletDao, PlatformWallet> implements PlatformWalletService {
    @Resource
    private PlatformWalletDao platformWalletDao;
    @Resource
    private WalletService walletService;


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
        List<PlatformWallet> list = list(lambdaQueryWrapper);
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
        getType(platformWalletFlowRequest.getType())

    }

    @Override
    public PlatformWallet getType(String type) {
        LambdaQueryWrapper<PlatformWallet> platformWalletLambdaQueryWrapper=new LambdaQueryWrapper<PlatformWallet>()
                .eq(PlatformWallet::getType,type);
        return getOne(platformWalletLambdaQueryWrapper);
    }
}
