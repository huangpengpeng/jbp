package com.jbp.service.service.impl;

import com.jbp.common.jdpay.enums.SceneTypeEnum;
import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.vo.*;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.JdPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class JdPayServiceImpl implements JdPayService {

    @Resource
    private JdPay jdPay;

    @Override
    public JdPayAggregateCreateOrderResponse jdPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                                   String ip, Date createTime) {
        JdPayAggregateCreateOrderRequest request = new JdPayAggregateCreateOrderRequest();
        request.setOutTradeNo(payCode);
        request.setTradeType("AGGRE");
        request.setTradeAmount(amt.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        request.setCreateDate(DateTimeUtils.format(createTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        request.setTradeExpiryTime("6000");
        request.setTradeSubject(goodsName);
        request.setTradeRemark(goodsName);
        request.setCurrency("CNY");
        request.setUserIp(ip);
        request.setBizTp("");
        request.setReturnParams("");
        request.setGoodsInfo(goodsName);
        request.setUserId(userId);
        request.setRiskInfo("");
        request.setSceneType(SceneTypeEnum.ONLINE_APP.getCode());

        try {
            return jdPay.aggregateCreateOrder(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public JdPayAggregateCreateOrderResponse aliPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                                    String ip, Date createTime) {
        JdPayAggregateCreateOrderRequest request = new JdPayAggregateCreateOrderRequest();
        request.setOutTradeNo(payCode);
        request.setTradeType("AGGRE_QR");
        request.setTradeAmount(amt.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        request.setCreateDate(DateTimeUtils.format(createTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        request.setTradeExpiryTime("6000");
        request.setTradeSubject(goodsName);
        request.setTradeRemark(goodsName);
        request.setCurrency("CNY");
        request.setUserIp(ip);
        request.setBizTp("");
        request.setReturnParams("");
        request.setGoodsInfo(goodsName);
        request.setUserId(userId);

        request.setRiskInfo("");
        request.setSceneType(SceneTypeEnum.ONLINE_PC.getCode());
        try {
            return jdPay.aggregateCreateOrder(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public JdPayQueryOrderResponse queryOrder(String payCode) {
        JdPayQueryOrderRequest q = new JdPayQueryOrderRequest();
        q.setOutTradeNo(payCode);
        try {
            return jdPay.queryOrder(q);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JdPayRefundResponse refund(String payCode, String refundCode, BigDecimal amt, Date createTime) {
        JdPayRefundRequest q = new JdPayRefundRequest();
        q.setOriginalOutTradeNo(payCode);
        q.setOutTradeNo(refundCode);
        q.setCurrency("CNY");
        q.setTradeAmount(amt.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        q.setTradeDate(DateTimeUtils.format(createTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        try {
            return jdPay.refund(q);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
