package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.model.order.OrderPayRecord;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.OrderPayRecordDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.OrderPayChannelService;
import com.jbp.service.service.OrderPayRecordService;
import com.jbp.service.service.YopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderPayRecordServiceImpl extends ServiceImpl<OrderPayRecordDao, OrderPayRecord> implements OrderPayRecordService {

    @Resource
    private OrderPayChannelService payChannelService;
    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private YopService yopService;

    @Override
    public OrderPayRecord getByOrderNo(String orderNo) {
        return getOne(new LambdaQueryWrapper<OrderPayRecord>().eq(OrderPayRecord::getOrderNo, orderNo).last("limit 1"));
    }

    @Override
    public OrderPayRecord scanPay(String payeeName, Integer merId, String payMethod, BigDecimal payPrice, String remark) {
        // 业务单号
        String orderNo = StringUtils.N_TO_10("SCAN_");
        //  支付单号
        String pay = StringUtils.N_TO_10("PAY_");

        OrderPayChannel payChannel = payChannelService.getServer(merId, payMethod);

        if(payChannel.getPayChannel().equals("易宝")){


        }
        if(payChannel.getPayChannel().equals("连连")){


        }


        return null;
    }

    @Override
    public OrderPayRecord create(String type, String orderNo, String payMethod, String payType, String payChannel,
                                 String merchantName, String merchantNo, BigDecimal payPrice, String payTime) {
        OrderPayRecord record = OrderPayRecord.builder().build();
        save(record);
        return record;
    }

    @Override
    public void refund(String orderNo, BigDecimal refundPrice) {
        OrderPayRecord record = getByOrderNo(orderNo);
        if (record == null) {
            return;
        }
        record.setRefundPrice(record.getRefundPrice().add(refundPrice));
        updateById(record);
    }
}
