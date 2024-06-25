package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.*;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.mybatis.ProductDeductionListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order", autoResultMap = true)
@ApiModel(value = "Order对象", description = "订单表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID,商户订单等级有值")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "钱包抵扣")
    private BigDecimal walletDeductionFee;

    @ApiModelProperty(value = "钱包抵扣")
    @TableField(typeHandler = ProductDeductionListHandler.class)
    private List<ProductDeduction> walletDeductionList;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付方法")
    private String payMethod;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue, wallet, lianlian, confirmPay, quickPay")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App, wallet, lianlian")
    private String payChannel;

    @ApiModelProperty(value = "支付网关 -1 统一支付 0 在线支付 1 积分支付")
    private Integer payGateway;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private Integer status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "取消状态：0-未取消，1-系统取消，2-用户取消")
    private Integer cancelStatus;

    @ApiModelProperty(value = "用户是否删除")
    private Boolean isUserDel;

    @ApiModelProperty(value = "商户是否删除")
    private Boolean isMerchantDel;

    @ApiModelProperty(value = "赠送积分")
    private Integer gainIntegral;

    @ApiModelProperty(value = "商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String outTradeNo;

    @ApiModelProperty(value = "支付重定向地址")
    private String redirect;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单,2-秒杀订单")
    private Integer type;

    @ApiModelProperty(value = "订单等级:0-平台订单，1-商户订单")
    private Integer level;

    @ApiModelProperty(value = "平台订单号")
    private String platOrderNo;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "收货时间")
    private Date receivingTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "商户优惠券金额")
    private BigDecimal merCouponPrice;

    @ApiModelProperty(value = "平台优惠券金额")
    private BigDecimal platCouponPrice;

    @ApiModelProperty(value = "平台优惠券id")
    private Integer platCouponId;

    @ApiModelProperty(value = "支付用户ID")
    private Integer payUid;

    @ApiModelProperty(value = "下单用户IP")
    private String ip;

    @ApiModelProperty(value = "下单场景（报单，换购）")
    private String platform;

    @ApiModelProperty(value = "是否被拉取")
    private Boolean ifPull;

    @ApiModelProperty(value = "是否用户确认收货")
    private Boolean ifUserVerifyReceive;


    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version = 1;

    public String getOrderType() {
        String typeStr = "";
        if (this.getType() == null) {
            return typeStr;
        }
        switch (this.getType()) {
            case 0:
                typeStr = "普通";
                break;
            case 1:
                typeStr = "视频号";
                break;
            case 2:
                typeStr = "秒杀";
                break;
        }
        return typeStr;
    }

    public String getOrderRefundStatus() {
        String refundStatusStr = "";
        if (this.getRefundStatus() == null) {
            return refundStatusStr;
        }
        switch (this.getRefundStatus()) {
            case 0:
                refundStatusStr = "未退款";
                break;
            case 1:
                refundStatusStr = "申请中";
                break;
            case 2:
                refundStatusStr = "部分退款";
                break;
            case 3:
                refundStatusStr = "已退款";
                break;
        }
        return refundStatusStr;
    }

    public String getOrderStatus() {
        String statusStr = "";
        if (this.getStatus() == null) {
            return statusStr;
        }
        switch (this.getStatus()) {
            case 0:
                statusStr = "待支付";
                break;
            case 1:
                statusStr = "待发货";
                break;
            case 2:
                statusStr = "部分发货";
                break;
            case 3:
                statusStr = "待核销";
                break;
            case 4:
                statusStr = "待收货";
                break;
            case 5:
                statusStr = "已收货";
                break;
            case 6:
                statusStr = "已完成";
                break;
            case 9:
                statusStr = "已取消";
                break;
        }
        return statusStr;
    }

    public String getOrderPayChannel() {
        String payChannelStr = "";
        if (this.getPayChannel() == null) {
            return payChannelStr;
        }
        switch (this.getPayChannel()) {
            case "public":
                payChannelStr = "公众号";
                break;
            case "mini":
                payChannelStr = "小程序";
                break;
            case "h5":
                payChannelStr = "微信网页支付";
                break;
            case "yue":
                payChannelStr = "余额";
                break;
            case "wechatIos":
                payChannelStr = "微信Ios";
                break;
            case "wechatAndroid":
                payChannelStr = "微信Android";
                break;
            case "alipay":
                payChannelStr = "支付宝";
                break;
            case "alipayApp":
                payChannelStr = "支付宝App";
                break;
            case "wallet":
                payChannelStr = "积分";
                break;
            case "lianlian":
                payChannelStr = "连连";
                break;
        }
        return payChannelStr;
    }

    public String getOrderPayType() {
        String payTypeStr = "";
        if (this.getPayType() == null) {
            return payTypeStr;
        }
        switch (this.getPayType()) {
            case "weixin":
                payTypeStr = "微信支付";
                break;
            case "alipay":
                payTypeStr = "支付宝支付";
                break;
            case "yue":
                payTypeStr = "余额支付";
                break;
            case "wallet":
                payTypeStr = "积分支付";
                break;
            case "lianlian":
                payTypeStr = "连连支付";
                break;
            case "confirmPay":
                payTypeStr = "人工确认";
                break;
        }
        return payTypeStr;
    }


}
