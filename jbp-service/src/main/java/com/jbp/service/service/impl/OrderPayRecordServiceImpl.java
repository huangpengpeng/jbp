package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayRecord;
import com.jbp.service.dao.OrderPayRecordDao;
import com.jbp.service.service.OrderPayRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderPayRecordServiceImpl extends ServiceImpl<OrderPayRecordDao, OrderPayRecord> implements OrderPayRecordService {

    @Override
    public OrderPayRecord getByOrderNo(String orderNo) {
        return getOne(new LambdaQueryWrapper<OrderPayRecord>().eq(OrderPayRecord::getOrderNo, orderNo).last("limit 1"));
    }

    @Override
    public OrderPayRecord create(String type, String orderNo, String payMethod, String payType, String payChannel,
                                 String merchantName, String merchantNo, BigDecimal payPrice, String payTime) {
        OrderPayRecord record = OrderPayRecord.builder().type(type).orderNo(orderNo).payMethod(payMethod).payType(payType)
                .payChannel(payChannel).merchantName(merchantName).merchantNo(merchantNo).payPrice(payPrice)
                .refundPrice(BigDecimal.ZERO).payTime(payTime).build();
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
