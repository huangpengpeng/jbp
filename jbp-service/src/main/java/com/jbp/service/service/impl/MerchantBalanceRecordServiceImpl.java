package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.merchant.MerchantBalanceRecord;
import com.jbp.service.dao.MerchantBrokerageRecordDao;
import com.jbp.service.service.MerchantBalanceRecordService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * MerchantBalanceRecordServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class MerchantBalanceRecordServiceImpl extends ServiceImpl<MerchantBrokerageRecordDao, MerchantBalanceRecord> implements MerchantBalanceRecordService {

    @Resource
    private MerchantBrokerageRecordDao dao;

    /**
     * 通过关联单号查询记录
     * @param linkNo 关联单号
     * @return MerchantBalanceRecord
     */
    @Override
    public MerchantBalanceRecord getByLinkNo(String linkNo) {
        LambdaQueryWrapper<MerchantBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantBalanceRecord::getLinkNo, linkNo);
        lqw.last("limit 1");
        return dao.selectOne(lqw);
    }
}

