package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderPayRecord;

import java.math.BigDecimal;
import java.util.List;

public interface OrderPayRecordService extends IService<OrderPayRecord> {

    OrderPayRecord getByOrderNo(String orderNo);

    OrderPayRecord scanPay(String payeeName, Integer merId, String payMethod, BigDecimal payPrice, String remark, String ip);

    void callBack(String orderNo);

    List<OrderPayRecord> getWaitPayByOrderNo(String orderNo);

    OrderPayRecord getByPayNo(String payNo);

    void refund(String payNo, BigDecimal refundAmt, String remark);

    void refundCallBack(String refundNo);


}
