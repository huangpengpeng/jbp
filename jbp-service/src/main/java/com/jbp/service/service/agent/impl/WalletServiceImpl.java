package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.agent.WalletDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.service.agent.WalletService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletServiceImpl extends ServiceImpl<WalletDao, Wallet> implements WalletService {
    @Resource
    WalletFlowService walletFlowService;
    @Resource
    PlatformWalletService platformWalletService;
    @Resource
    WalletConfigService walletConfigService;
    @Resource
    UserService userService;

    @Override
    public Wallet add(Integer uId, Integer type) {
        Wallet wallet = new Wallet(uId, type);
        save(wallet);
        return wallet;
    }

    @Override
    public Wallet getByUser(Integer uid, Integer type) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUId, uid)
                .eq(Wallet::getType, type);
        return getOne(wrapper);
    }


    @Override
    public Boolean increase(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "增加用户积分金额不能小于0:" + amt);
        }
        if (walletConfigService.getByType(type).getRecharge().equals(0)) {
            throw new CrmebException(type + "禁用充值或转账");
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null) {
            wallet = add(uid, type);
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().add(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.收入.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean reduce(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        if (amt == null || ArithmeticUtils.lessEquals(amt, BigDecimal.ZERO)) {
            throw new CrmebException(type + "减少用户积分金额不能小于0:" + amt);
        }
        if (walletConfigService.getByType(type).getCanWithdraw().equals(0)) {
            throw new CrmebException(type + "禁用提现或转账");
        }
        Wallet wallet = getByUser(uid, type);
        if (wallet == null || ArithmeticUtils.less(wallet.getBalance(), amt)) {
            throw new CrmebException("用户余额不足");
        }
        BigDecimal orgBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(amt));
        boolean ifSuccess = updateById(wallet);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }
        walletFlowService.add(uid, type, amt, operate, WalletFlow.ActionEnum.支出.name(), externalNo, orgBalance, wallet.getBalance(), postscript);
        return ifSuccess;
    }

    @Override
    public Boolean transferToPlatform(Integer uid, Integer type, BigDecimal amt, String operate, String externalNo, String postscript) {
        reduce(uid, type, amt, operate, externalNo, postscript);
        platformWalletService.increase(type, amt, operate, externalNo, postscript);
        return true;
    }

    @Override
    public Boolean virement(Integer uid, Integer virementid, BigDecimal amt, Integer type, String postscript, String operate,String externalNo) {
        reduce(uid,type,amt,operate,externalNo,postscript);
        platformWalletService.increase(type, amt, operate, externalNo, postscript);
        //平台转用户
        increase(virementid,type,amt,operate,externalNo,postscript);
        platformWalletService.reduce(type,amt,operate,externalNo,postscript);
        return true;
    }


    @Override
    public PageInfo<Wallet> pageList(Integer uid, Integer type, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<Wallet>()
                .eq(!ObjectUtil.isNull(uid), Wallet::getUId, uid)
                .eq(!ObjectUtil.isNull(type), Wallet::getType, type);
        Page<WalletFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Wallet> list = list(walletLambdaQueryWrapper);
        list.forEach(e -> {
            e.setTypeName(walletConfigService.getByType(e.getType()).getName());
            e.setAccount(userService.getById(e.getUId()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
