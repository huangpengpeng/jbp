package com.jbp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CbecOrderSyncDTO implements Serializable {

    private static final long serialVersionUID = 7341847468600990748L;

    /**
     * 用户唯一标识
     */
    private String bizId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 单号
     */
    private String orderSn;

    /**
     * 状态
     */
    private String status;

    /**
     * 订单总金额  商品金额+运费+积分
     */
    private BigDecimal totalFee;

    /**
     * 商品金额
     */
    private BigDecimal goodsFee;

    /**
     * 运费
     */
    private BigDecimal postFee;

    /**
     * 积分
     */
    private BigDecimal score;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 付款时间
     */
    private Date paymentTime;

    /**
     * 发货时间
     */
    private Date shipmentTime;

    /**
     * 商品详情
     */
    private List<GoodsDetail> goodsDetails;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class GoodsDetail {
        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer quantity;
    }
}
