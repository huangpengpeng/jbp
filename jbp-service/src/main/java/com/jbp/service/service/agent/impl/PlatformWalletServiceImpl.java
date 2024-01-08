package com.jbp.service.service.agent.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.PlatformWallet;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.agent.PlatformWalletDao;
import com.jbp.service.service.agent.PlatformWalletFlowService;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Service
@Slf4j
public class PlatformWalletServiceImpl extends ServiceImpl<PlatformWalletDao, PlatformWallet> implements PlatformWalletService {
    @Resource
    private PlatformWalletDao platformWalletDao;
    @Resource
    private WalletService walletService;
    @Resource
    private PlatformWalletFlowService platformWalletFlowService;

    @Override
    public PageInfo<PlatformWallet> pageList(PageParamRequest pageParamRequest) {
        Page<PlatformWallet> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list());
    }


    public PlatformWallet getType(Integer type) {
        return getOne(new LambdaQueryWrapper<PlatformWallet>().eq(PlatformWallet::getType, type));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Boolean increase(Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "增加平台积分金额不能小于0:" + amt);
        }
        PlatformWallet platformWallet = getType(type);
        if (null == platformWallet) {
            platformWallet = new PlatformWallet(type);
            save(platformWallet);
        }
        BigDecimal orgBalance = platformWallet.getBalance();
        platformWallet.setBalance(platformWallet.getBalance().add(amt));
        boolean ifSuccess = updateById(platformWallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        platformWalletFlowService.add(type, operate, PlatformWalletFlow.ActionEnum.收入.toString(),
                externalNo, postscript, amt, orgBalance, platformWallet.getBalance());
        return ifSuccess;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Boolean reduce(Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "减少平台积分金额不能小于0:" + amt);
        }
        PlatformWallet platformWallet = getType(type);
        if (ArithmeticUtils.less(platformWallet.getBalance(), amt)) {
            throw new CrmebException(type + "减少平台积分金额小于:" + amt);
        }
        BigDecimal orgBalance = platformWallet.getBalance();
        platformWallet.setBalance(platformWallet.getBalance().subtract(amt));
        boolean ifSuccess = updateById(platformWallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        platformWalletFlowService.add(type, operate, PlatformWalletFlow.ActionEnum.支出.toString(),
                externalNo, postscript, amt, orgBalance, platformWallet.getBalance());
        return ifSuccess;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Boolean transferToUser(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        reduce(type, amt, operate, externalNo, postscript);
        walletService.increase(uid, type, amt, operate, externalNo, postscript);
        return Boolean.TRUE;
    }

}
