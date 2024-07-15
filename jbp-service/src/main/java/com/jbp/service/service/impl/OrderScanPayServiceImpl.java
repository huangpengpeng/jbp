package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.model.order.OrderScanPay;
import com.jbp.common.request.OrderScanPayRequest;
import com.jbp.service.dao.OrderScanPayDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.OrderPayChannelService;
import com.jbp.service.service.OrderScanPayService;
import com.jbp.service.service.YopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderScanPayServiceImpl extends ServiceImpl<OrderScanPayDao, OrderScanPay> implements OrderScanPayService {

    @Resource
    private OrderPayChannelService payChannelService;
    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private YopService yopService;

    @Override
    public OrderScanPay create(OrderScanPayRequest request) {
        OrderPayChannel orderPayChannel = payChannelService.getServer(request.getPayMethod());
        if (orderPayChannel == null) {
            throw new RuntimeException("当前支付渠道未开通:" + request.getPayMethod());
        }
        // 微信  支付宝  银行卡
        if (OrderScanPay.Enum.微信.toString().equals(request.getPayMethod())) {

            if (orderPayChannel.getPayChannel().equals("易宝")) {

//                yopService.wechatAlipayPay()
            }
            if (orderPayChannel.getPayChannel().equals("连连")) {

            }

        }
        if (OrderScanPay.Enum.支付宝.toString().equals(request.getPayMethod())) {
            if (orderPayChannel.getPayChannel().equals("易宝")) {

            }
            if (orderPayChannel.getPayChannel().equals("连连")) {

            }
        }
        if (OrderScanPay.Enum.银行卡.toString().equals(request.getPayMethod())) {
            if (orderPayChannel.getPayChannel().equals("易宝")) {

            }
            if (orderPayChannel.getPayChannel().equals("连连")) {

            }
        }
        return null;
    }


}
