package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearingItemConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingItemConfigRequest;
import com.jbp.service.dao.agent.FundClearingItemConfigDao;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.FundClearingItemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingItemConfigServiceImpl extends ServiceImpl<FundClearingItemConfigDao, FundClearingItemConfig> implements FundClearingItemConfigService {
    @Resource
    private WalletConfigService walletConfigService;

    @Override
    public void save(List<FundClearingItemConfigRequest> list) {
        List<FundClearingItemConfig> saveList = Lists.newArrayList();
        for (FundClearingItemConfigRequest configRequest : list) {
            FundClearingItemConfig clearingItemConfig = new FundClearingItemConfig(configRequest.getCommName(),
                    configRequest.getName(), configRequest.getScale(), configRequest.getWalletType());
            saveList.add(clearingItemConfig);
        }
        remove(new QueryWrapper<FundClearingItemConfig>().lambda().eq(FundClearingItemConfig::getCommName, list.get(0)));
        saveBatch(saveList);
    }

    @Override
    public PageInfo<FundClearingItemConfig> pageList(String commName, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<FundClearingItemConfig> lqw = new LambdaQueryWrapper<FundClearingItemConfig>()
                .like(!ObjectUtil.isNull(commName) && !commName.equals(""), FundClearingItemConfig::getCommName, commName);
        Page<FundClearingItemConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<FundClearingItemConfig> list = list(lqw);
        list.forEach(e->{
            e.setWalletTypeName(walletConfigService.getByType(e.getWalletType()).getName());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
