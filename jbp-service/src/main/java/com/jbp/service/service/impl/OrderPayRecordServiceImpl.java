package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.lianlian.result.PaymentGwResult;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.model.order.OrderPayRecord;
import com.jbp.common.utils.DateTimeUtils;
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
import java.util.List;

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
    public OrderPayRecord scanPay(String payeeName, Integer merId, String payMethod, BigDecimal payPrice, String remark, String ip) {
        // 业务单号
        String orderNo = StringUtils.N_TO_10("SCAN_");
        //  支付单号
        String payNo = StringUtils.N_TO_10("PAY_");
        OrderPayChannel payChannel = payChannelService.getServer(merId, payMethod);
        String notify_url = payChannel.getNotifyUrl() + "/" + orderNo;
        String return_url = payChannel.getReturnUrl() + "/" + orderNo;

        OrderPayRecord orderPayRecord = new OrderPayRecord(payChannel.getId(), merId, payeeName,
                OrderPayRecord.StatusEnum.收款码.toString(), orderNo, payNo,
                payPrice, payPrice.multiply(payChannel.getFeeScale()));

        if (payChannel.getPayChannel().equals("连连")) {
            if (payChannel.getPayMethod().equals("微信")) {
                PaymentGwResult paymentGwResult = lianLianPayService.wechatScanPay(payChannel, payNo, payPrice,
                        payPrice.multiply(payChannel.getFeeScale()), notify_url, return_url, remark, ip);
                orderPayRecord.setReceiptNo(paymentGwResult.getAccp_txno());
                orderPayRecord.setOrderResultInfo(JSONObject.toJSONString(paymentGwResult));

            }
            if (payChannel.getPayMethod().equals("支付宝")) {
                PaymentGwResult paymentGwResult = lianLianPayService.aliScanPay(payChannel, payNo, payPrice,
                        payPrice.multiply(payChannel.getFeeScale()), notify_url, return_url, remark, ip);
                orderPayRecord.setReceiptNo(paymentGwResult.getAccp_txno());
                orderPayRecord.setOrderResultInfo(JSONObject.toJSONString(paymentGwResult));
            }
        }
        save(orderPayRecord);
        return orderPayRecord;
    }

    @Override
    public void callBack(String orderNo) {
        List<OrderPayRecord> list = getWaitPayByOrderNo(orderNo);
        for (OrderPayRecord record : list) {
            OrderPayChannel payChannel = payChannelService.getById(record.getOrderPayChannelId());
            if (payChannel.getPayChannel().equals("连连")) {
                QueryPaymentResult result = lianLianPayService.queryPayResult(record.getPayNo());
                if (result != null) {
                    record.setQueryResultInfo(JSONObject.toJSONString(result));
                    if ("TRADE_SUCCESS".equals(result.getTxn_status())) {
                        record.setStatus("已付款");
                        record.setPayTime(DateTimeUtils.format(DateTimeUtils.parseDate(result.getFinish_time()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                        record.setUnionPayNo(result.getChnl_txno());

                    }
                    if ("TRADE_CLOSE".equals(result.getTxn_status())) {
                        record.setStatus("交易失败");
                    }
                    record.setQueryResultInfo(JSONObject.toJSONString(result));
                }
            }
        }
    }

    @Override
    public List<OrderPayRecord> getWaitPayByOrderNo(String orderNo) {
        return list(new QueryWrapper<OrderPayRecord>().lambda().eq(OrderPayRecord::getOrderNo, orderNo));
    }
}
