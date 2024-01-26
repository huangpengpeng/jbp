package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.WalletConfigDao;
import com.jbp.service.service.WalletConfigService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletConfigServiceImpl extends ServiceImpl<WalletConfigDao, WalletConfig> implements WalletConfigService {

    @Override
    public PageInfo<WalletConfig> pageList(String name, Integer status, Boolean canWithdraw, Boolean recharge, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WalletConfig> walletConfigLambdaQueryWrapper = new LambdaQueryWrapper<WalletConfig>();
        walletConfigLambdaQueryWrapper.like(!ObjectUtil.isNull(name) && name != "", WalletConfig::getName, name);
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(status), WalletConfig::getStatus, status);
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(canWithdraw), WalletConfig::getCanWithdraw, canWithdraw);
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(recharge), WalletConfig::getRecharge, recharge);
        Page<WalletConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(walletConfigLambdaQueryWrapper));
    }

    @Override
    public WalletConfig getByType(Integer type) {
        LambdaQueryWrapper<WalletConfig> walletConfigLambdaQueryWrapper = new LambdaQueryWrapper<WalletConfig>();
        walletConfigLambdaQueryWrapper.eq(WalletConfig::getType, type);
        return getOne(walletConfigLambdaQueryWrapper);

    }

    @Override
    public void update(Integer id, String name, int status, Boolean canWithdraw, Boolean recharge, int changeType, BigDecimal changeScale) {
        // 1.canPay  只能存在一条记录为true  如果更新为true 要检查数据库是否存在有true 并且不是当前 的记录 有就不允许改  canDeduction false
        LambdaQueryWrapper<WalletConfig> walletConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 2.设置canPay  不允许设置  canDeduction  true
        // 3.设置 canDeduction true 不允许设置  canPay  true

        // 4.canWithdraw == true  有且仅有一条

        WalletConfig walletConfig = getByType(id);
        walletConfig.setName(name);
        walletConfig.setStatus(status);
        walletConfig.setCanWithdraw(canWithdraw);
        walletConfig.setRecharge(recharge);
        walletConfig.setChangeType(changeType);
        walletConfig.setChangeScale(changeScale);
        updateById(walletConfig);
    }

    @Override
    public List<WalletConfig> getCanDeductionList() {
        LambdaQueryWrapper<WalletConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletConfig::getCanDeduction, true);
        return list(lambdaQueryWrapper);
    }
    
    public void update(Integer id, String name, int status, Boolean canDeduction, Boolean canPay, Boolean canWithdraw, Boolean recharge, Boolean canTransfer, BigDecimal changeScale) {
        WalletConfig walletConfig = getByType(id);
        if ((canPay && !canDeduction) || (!canPay && canDeduction)) {
            LambdaQueryWrapper<WalletConfig> lqw = new LambdaQueryWrapper<WalletConfig>()
                    .eq(WalletConfig::getCanPay, true);
            if (list(lqw).size() > 0) {
                throw new CrmebException("无法同时开启可支付商品");
            } else {
                walletConfig.setCanPay(canPay);
                walletConfig.setCanDeduction(canDeduction);
            }
        } else {
            throw new CrmebException("无法同时启用支付商品和可抵扣功能");
        }
        if (canWithdraw){
            LambdaQueryWrapper<WalletConfig> lqw = new LambdaQueryWrapper<WalletConfig>()
                    .eq(WalletConfig::getCanWithdraw, true);
            if (list(lqw).size() > 0) {
                throw new CrmebException("无法同时开启多个可提现商品");
            } else {
                walletConfig.setCanWithdraw(canWithdraw);
            }
        }
        walletConfig.setName(name);
        walletConfig.setStatus(status);
        walletConfig.setCanWithdraw(canWithdraw);
        walletConfig.setRecharge(recharge);
        walletConfig.setChangeScale(changeScale);
        updateById(walletConfig);

    }
}
