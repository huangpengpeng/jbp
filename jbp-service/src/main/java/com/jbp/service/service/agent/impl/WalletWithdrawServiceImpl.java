package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.exception.CrmebUpdateException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.request.agent.WalletWithdrawRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.WalletWithdrawDao;
import com.jbp.service.service.agent.WalletService;
import com.jbp.service.service.agent.WalletWithdrawService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletWithdrawServiceImpl extends ServiceImpl<WalletWithdrawDao, WalletWithdraw> implements WalletWithdrawService {

   @Resource
   private WalletService walletService;

    @Override
    public WalletWithdraw create(Integer uid, String account, Integer walletType, String walletName, BigDecimal amt, String postscript) {
        if (amt == null || ArithmeticUtils.less(amt, BigDecimal.ZERO)) {
            throw new CrmebException("提现金额异常");
        }
        Wallet wallet = walletService.getByUser(uid, walletType);
        if (wallet == null || ArithmeticUtils.less(wallet.getBalance(), amt)) {
            throw new CrmebException("余额不足");
        }
        WalletWithdraw walletWithdraw = new WalletWithdraw(uid, account, walletType, walletName, amt, postscript);
        save(walletWithdraw);
        walletService.reduce(uid, walletType, amt, WalletFlow.OperateEnum.提现.name(), walletWithdraw.getUniqueNo(), postscript);
        return walletWithdraw;
    }

    @Override
    public WalletWithdraw getByUniqueNo(String uniqueNo) {
        return getOne(new QueryWrapper<WalletWithdraw>().lambda().eq(WalletWithdraw::getUniqueNo, uniqueNo));
    }

    @Override
    public void send(List<WalletWithdrawRequest> walletWithdrawList) {
        if (CollectionUtils.isEmpty(walletWithdrawList)) {
            throw new CrmebException("提现信息不能为空");
        }
        List<WalletWithdraw> list = Lists.newArrayList();
        Date now = DateTimeUtils.getNow();
        int i = 1;
        for (WalletWithdrawRequest withdrawRequest : walletWithdrawList) {
            if (StringUtils.isEmpty(withdrawRequest.getUniqueNo())) {
                throw new CrmebException("提现信息单号不能为空，行号:" + i);
            }
            WalletWithdraw walletWithdraw = getByUniqueNo(withdrawRequest.getUniqueNo());
            if (walletWithdraw == null) {
                throw new CrmebException("提现信息不存在，行号:" + i);
            }
            if (!walletWithdraw.getStatus().equals(WalletWithdraw.StatusEnum.待出款.toString())) {
                throw new CrmebException("提现状态不是待出库，行号:" + i);
            }
            walletWithdraw.setStatus(WalletWithdraw.StatusEnum.已出款.toString());
            walletWithdraw.setRemark(withdrawRequest.getRemark());
            walletWithdraw.setSuccessTime(now);
            list.add(walletWithdraw);
            i++;
        }
        List<List<WalletWithdraw>> partition = com.google.common.collect.Lists.partition(list, 100);
        for (List<WalletWithdraw> walletWithdraws : partition) {
            final boolean b = updateBatchById(walletWithdraws);
            if(!b){
               throw  new CrmebUpdateException();
            }
        }
    }

    @Override
    public void cancel(List<WalletWithdrawRequest> walletWithdrawList) {
        if (CollectionUtils.isEmpty(walletWithdrawList)) {
            throw new CrmebException("提现信息不能为空");
        }
        List<WalletWithdraw> list = Lists.newArrayList();
        Date now = DateTimeUtils.getNow();
        int i = 1;
        for (WalletWithdrawRequest withdrawRequest : walletWithdrawList) {
            if (StringUtils.isEmpty(withdrawRequest.getUniqueNo())) {
                throw new CrmebException("提现信息单号不能为空，行号:" + i);
            }
            WalletWithdraw walletWithdraw = getByUniqueNo(withdrawRequest.getUniqueNo());
            if (walletWithdraw == null) {
                throw new CrmebException("提现信息不存在，行号:" + i);
            }
            if (!walletWithdraw.getStatus().equals(WalletWithdraw.StatusEnum.待出款.toString())) {
                throw new CrmebException("提现状态不是待出库，行号:" + i);
            }
            walletWithdraw.setStatus(WalletWithdraw.StatusEnum.已取消.toString());
            walletWithdraw.setRemark(withdrawRequest.getRemark());
            walletWithdraw.setSuccessTime(now);
            list.add(walletWithdraw);
            i++;
        }
        List<List<WalletWithdraw>> partition = com.google.common.collect.Lists.partition(list, 100);
        for (List<WalletWithdraw> walletWithdraws : partition) {
            for (WalletWithdraw walletWithdraw : walletWithdraws) {
                walletService.increase(walletWithdraw.getUid(), walletWithdraw.getWalletType(),
                        walletWithdraw.getAmt(), WalletFlow.OperateEnum.提现取消.toString(), walletWithdraw.getUniqueNo(), walletWithdraw.getPostscript());
            }
            updateBatchById(walletWithdraws);
        }
    }
}
