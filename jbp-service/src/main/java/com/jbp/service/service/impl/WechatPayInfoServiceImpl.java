package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.WechatPayInfoDao;
import com.jbp.service.service.WechatPayInfoService;
import com.jbp.common.model.wechat.WechatPayInfo;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *  WechatPayInfoServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class WechatPayInfoServiceImpl extends ServiceImpl<WechatPayInfoDao, WechatPayInfo> implements WechatPayInfoService {

    @Resource
    private WechatPayInfoDao dao;

    /**
     * 获取详情（商户订单号）
     * @param outTradeNo 商户订单号
     * @return WechatPayInfo
     */
    @Override
    public WechatPayInfo getByNo(String outTradeNo) {
        LambdaQueryWrapper<WechatPayInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatPayInfo::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

