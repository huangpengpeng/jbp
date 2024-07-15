package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.model.order.OrderScanPay;
import com.jbp.common.request.OrderScanPayRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.OrderScanPayDao;
import com.jbp.service.service.*;
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
    @Resource
    private UserService userService;
    @Resource
    private SystemAdminService systemAdminService;

    @Override
    public OrderScanPay create(OrderScanPayRequest request) {
        OrderPayChannel orderPayChannel = payChannelService.getServer(request.getPayMethod());
        if (orderPayChannel == null) {
            throw new RuntimeException("当前支付渠道未开通:" + request.getPayMethod());
        }
//        systemAdminService.get
//        OrderScanPay order = OrderScanPay.builder().orderNo(StringUtils.N_TO_10("OSP_")).merId().uid(request.getPayerId()).build();

        // 微信  支付宝  银行卡
        if (OrderScanPay.Enum.微信.toString().equals(request.getPayMethod())) {

            if (orderPayChannel.getPayChannel().equals("易宝")) {

//                yopService.wechatAlipayPay(orderPayChannel.getMerchantNo(), request.getPayerId(), String orderId, String orderAmount, String goodsName,
//                        String notifyUrl, String memo, String redirectUrl, String payWay, String channel,
//                        String appId, String openId, String ip)
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
