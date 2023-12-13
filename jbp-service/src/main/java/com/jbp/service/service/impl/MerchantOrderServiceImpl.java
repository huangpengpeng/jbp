package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.service.dao.MerchantOrderDao;
import com.jbp.service.service.MerchantOrderService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  MerchantOrderServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class MerchantOrderServiceImpl extends ServiceImpl<MerchantOrderDao, MerchantOrder> implements MerchantOrderService {

    @Resource
    private MerchantOrderDao dao;

    /**
     * 根据主订单号获取商户订单
     * @param orderNo 主订单号
     * @return List
     */
    @Override
    public List<MerchantOrder> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<MerchantOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantOrder::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 根据主订单号获取商户订单（支付完成进行商户拆单后可用）
     * @param orderNo 主订单号
     * @return MerchantOrder
     */
    @Override
    public MerchantOrder getOneByOrderNo(String orderNo) {
        LambdaQueryWrapper<MerchantOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantOrder::getOrderNo, orderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 虚拟发货
     * @param orderNo 订单号
     */
    @Override
    public Boolean virtual(String orderNo) {
        LambdaUpdateWrapper<MerchantOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantOrder::getDeliveryType, OrderConstants.ORDER_DELIVERY_TYPE_FICTITIOUS);
        wrapper.eq(MerchantOrder::getOrderNo, orderNo);
        return update(wrapper);
    }

    /**
     * 通过核销码获取订单
     * @param verifyCode 核销码
     */
    @Override
    public MerchantOrder getOneByVerifyCode(String verifyCode) {
        LambdaQueryWrapper<MerchantOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantOrder::getVerifyCode, verifyCode);
        lqw.orderByDesc(MerchantOrder::getId);
        lqw.last("limit 1");
        return dao.selectOne(lqw);
    }
}

