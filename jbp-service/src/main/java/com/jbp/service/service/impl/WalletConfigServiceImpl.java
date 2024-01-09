package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletConfigRequest;
import com.jbp.service.dao.agent.WalletConfigDao;
import com.jbp.service.service.WalletConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WalletConfigServiceImpl extends ServiceImpl<WalletConfigDao, WalletConfig> implements WalletConfigService {

    @Override
    public PageInfo<WalletConfig> pageList(String name, Integer status, Boolean canWithdraw, Boolean recharge, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<WalletConfig> walletConfigLambdaQueryWrapper=new LambdaQueryWrapper<WalletConfig>();
        walletConfigLambdaQueryWrapper.like(!ObjectUtil.isNull(name)&&name!="",WalletConfig::getName,name);
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(status),WalletConfig::getStatus,status );
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(canWithdraw),WalletConfig::getCanWithdraw,canWithdraw);
        walletConfigLambdaQueryWrapper.eq(!ObjectUtil.isNull(recharge), WalletConfig::getRecharge, recharge);
        Page<WalletConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(walletConfigLambdaQueryWrapper));
    }

    @Override
    public WalletConfig getByType(Integer type) {
        LambdaQueryWrapper<WalletConfig> walletConfigLambdaQueryWrapper=new LambdaQueryWrapper<WalletConfig>();
        walletConfigLambdaQueryWrapper.eq(WalletConfig::getType,type);
        return getOne(walletConfigLambdaQueryWrapper);

    }

    @Override
    public void update(Integer id, String name, int status, Boolean canWithdraw, Boolean recharge, int changeType, int changeScale) {
        WalletConfig walletConfig = getByType(id);
        walletConfig.setName(name);
        walletConfig.setStatus(status);
        walletConfig.setCanWithdraw(canWithdraw);
        walletConfig.setRecharge(recharge);
        walletConfig.setChangeType(changeType);
        walletConfig.setChangeScale(changeScale);
        updateById(walletConfig);
    }
}
