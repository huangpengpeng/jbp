package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.system.GroupConfig;
import com.jbp.common.model.system.JushuitanConfig;
import com.jbp.service.dao.JushuitanConfigDao;
import com.jbp.service.service.JushuitanConfigService;
import org.springframework.stereotype.Service;


@Service
public class JushuitanConfigServiceImpl extends ServiceImpl<JushuitanConfigDao, JushuitanConfig> implements JushuitanConfigService {


    @Override
    public JushuitanConfig def() {
        LambdaQueryWrapper<JushuitanConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(JushuitanConfig::getId);
        lqw.last(" limit 1");
        return getOne(lqw);
    }

    @Override
    public JushuitanConfig add(String accessToken, String appKey, String appSecret, String cancelCallApi, Long expiresIn, String refreshToken, String refundCallApi, String repCallApi, String scope, String shipCallApi, String shopId) {
        JushuitanConfig jushuitanConfig = new JushuitanConfig(accessToken,appKey,appSecret,cancelCallApi,expiresIn,refreshToken,refundCallApi,repCallApi,scope,shipCallApi,shopId);
        save(jushuitanConfig);
        return jushuitanConfig;
    }


}

