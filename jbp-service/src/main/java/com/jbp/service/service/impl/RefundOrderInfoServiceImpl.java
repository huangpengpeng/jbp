package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.RefundOrderInfoDao;
import com.jbp.service.service.RefundOrderInfoService;
import com.jbp.common.model.order.RefundOrderInfo;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
*  RefundOrderInfoServiceImpl 接口实现
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
public class RefundOrderInfoServiceImpl extends ServiceImpl<RefundOrderInfoDao, RefundOrderInfo> implements RefundOrderInfoService {

    @Resource
    private RefundOrderInfoDao dao;

    /**
     * 根据退款单号获取详情
     * @param refundOrderNo 退款单号
     * @return RefundOrderInfo
     */
    @Override
    public RefundOrderInfo getByRefundOrderNo(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrderInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrderInfo::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

