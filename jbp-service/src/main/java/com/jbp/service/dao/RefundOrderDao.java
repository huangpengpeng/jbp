package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.response.RefundOrderInfoResponse;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * 退款单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface RefundOrderDao extends BaseMapper<RefundOrder> {

    /**
     * 退款订单详情
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(@Param("refundOrderNo") String refundOrderNo);
}
