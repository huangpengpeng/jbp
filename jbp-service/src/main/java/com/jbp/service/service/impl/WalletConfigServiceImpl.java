package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserWalletInfoResponse;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.WalletConfigDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.service.agent.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class WalletConfigServiceImpl extends ServiceImpl<WalletConfigDao, WalletConfig> implements WalletConfigService {

    @Resource
    private UserService userService;
    @Resource
    private WalletService walletService;
    @Resource
    private SystemConfigService systemConfigService;

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
    public List<WalletConfig> getCanDeductionList() {
        LambdaQueryWrapper<WalletConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletConfig::getCanDeduction, true);
        return list(lambdaQueryWrapper);
    }

    public void update(Integer id, String name, int status, Boolean canDeduction, Boolean canPay, Boolean canWithdraw,
                       Boolean recharge, Boolean canTransfer, BigDecimal changeScale, Integer changeType) {
        WalletConfig walletConfig = getByType(id);
        // 1.设置可支付, 不能同时存在2个可支付的积分
        if (canPay) {
            WalletConfig canPayWallet = getCanPay();
            if (canPayWallet != null && canPayWallet.getId().compareTo(id) != 0) {
                throw new CrmebException("可支付积分:" + canPayWallet.getName() + ", 已经存在不允许重复设置");
            }
        }
        // 2.设置可提现, 不能同时存在2个可提现的积分
        if (canWithdraw) {
            WalletConfig canWithdrawWallet = getCanWithdraw();
            if (canWithdrawWallet != null && canWithdrawWallet.getId().compareTo(id) != 0) {
                throw new CrmebException("可提现积分:" + canWithdrawWallet.getName() + ", 已经存在不允许重复设置");
            }
        }
        // 3.不允许同时设置抵扣+支付
        if (canPay && canDeduction) {
            throw new CrmebException("可支付&&可抵扣不能同时设置, 可支付不允许抵扣, 可抵扣不允许可支付");
        }

        walletConfig.setName(name);
        walletConfig.setStatus(status);
        walletConfig.setCanPay(canPay);
        walletConfig.setCanDeduction(canDeduction);
        walletConfig.setCanWithdraw(canWithdraw);
        walletConfig.setCanTransfer(canTransfer);
        walletConfig.setRecharge(recharge);
        walletConfig.setChangeType(changeType);
        walletConfig.setChangeScale(changeScale);
        updateById(walletConfig);

    }

    @Override
    public WalletConfig getCanPay() {
        return getOne(new QueryWrapper<WalletConfig>().lambda().eq(WalletConfig::getCanPay, true).eq(WalletConfig::getStatus, 1));
    }

    @Override
    public WalletConfig getCanWithdraw() {
        return getOne(new QueryWrapper<WalletConfig>().lambda().eq(WalletConfig::getCanWithdraw, true).eq(WalletConfig::getStatus, 1));
    }

    @Override
    public Map<Integer, WalletConfig> getWalletMap() {
        return FunctionUtil.keyValueMap(list(), WalletConfig::getType);
    }

    @Override
    public List<UserWalletInfoResponse> getUserWalletInfo() {
        List<WalletConfig> list = list();
        List<UserWalletInfoResponse> userWalletInfoResponseList = new ArrayList<>();
        User user = userService.getInfo();
        list.forEach(e -> {

            UserWalletInfoResponse userWalletInfoResponse = new UserWalletInfoResponse();
            userWalletInfoResponse.setWalletConfig(e);
            Wallet wallet = walletService.getByUser(user.getId(), e.getType());
            userWalletInfoResponse.setBalance(wallet == null ? BigDecimal.ZERO : wallet.getBalance());
            userWalletInfoResponseList.add(userWalletInfoResponse);
        });

        return userWalletInfoResponseList;
    }

    @Override
    public Boolean hasPwd() {
        String walletPayOpenPassword = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_WALLET_PAY_OPEN_PASSWORD);
        return Constants.CONFIG_FORM_SWITCH_OPEN.equals(walletPayOpenPassword);
    }
}
