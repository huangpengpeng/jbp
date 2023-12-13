package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.OrderDetailDao;
import com.jbp.service.service.OrderDetailService;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
*  OrderDetailServiceImpl 接口实现
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
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {

    @Resource
    private OrderDetailDao dao;

    /**
     * 根据主订单号获取
     * @param orderNo 订单编号
     * @return List
     */
    @Override
    public List<OrderDetail> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderDetail::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 订单商品评论列表
     * @param userId 用户id
     * @param isReply 是否评价
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderDetail> getReplyList(Integer userId, Boolean isReply, PageParamRequest pageRequest) {
        Page<OrderDetail> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<OrderDetail> orderDetailList = dao.findReplyList(userId, isReply ? 1 : 0);
        return CommonPage.copyPageInfo(page, orderDetailList);
    }

    /**
     * 订单收货
     * @param orderNo 订单号
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        LambdaUpdateWrapper<OrderDetail> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(OrderDetail::getIsReceipt, true);
        wrapper.eq(OrderDetail::getOrderNo, orderNo);
        return update(wrapper);
    }

    /**
     * 售后申请列表(可申请售后列表)
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderDetail> findAfterSaleApplyList(Integer uid, String orderNo, PageParamRequest pageParamRequest) {
        Page<OrderDetail> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderDetail> orderDetailList = dao.findAfterSaleApplyList(uid, orderNo);
        return CommonPage.copyPageInfo(page, orderDetailList);
    }

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getSalesNumByDateAndProductId(String date, Integer proId) {
        return dao.getSalesNumByDateAndProductId(date, proId);
    }

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesByDateAndProductId(String date, Integer proId) {
        return dao.getSalesByDateAndProductId(date, proId);
    }

    /**
     * 订单发货获取订单详情列表
     * @param orderNo 订单号
     * @return 订单详情列表
     */
    @Override
    public List<OrderDetail> getShipmentByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.select(OrderDetail::getId, OrderDetail::getProductName, OrderDetail::getImage, OrderDetail::getSku,
                OrderDetail::getPayNum, OrderDetail::getDeliveryNum, OrderDetail::getRefundNum);
        lqw.eq(OrderDetail::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 获取待评价数量
     * @return 待评价数量
     */
    @Override
    public Integer getAwaitReplyCount(Integer userId) {
        return dao.getAwaitReplyCount(userId);
    }
}

