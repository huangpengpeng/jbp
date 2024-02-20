package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.model.agent.FundClearingItemConfig;
import com.jbp.common.request.agent.FundClearingItemConfigRequest;
import com.jbp.service.dao.agent.FundClearingItemConfigDao;
import com.jbp.service.service.agent.FundClearingItemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingItemConfigServiceImpl extends ServiceImpl<FundClearingItemConfigDao, FundClearingItemConfig> implements FundClearingItemConfigService {

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



}
