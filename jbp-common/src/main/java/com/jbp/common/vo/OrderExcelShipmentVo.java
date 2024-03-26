package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jbp.common.model.product.ProductDeduction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单Excel VO对象
 * @Author 莫名
 * @Date 2023/6/28 12:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderExcelVo", description = "订单Excel VO对象")
public class OrderExcelShipmentVo implements Serializable {

    private static final long serialVersionUID = -8330957183745338822L;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单,2-秒杀订单")
    private String type;

    @ApiModelProperty(value = "平台单号")
    private String platOrderNo;

    @ApiModelProperty(value = "商户单号")
    private String orderNo;

    @ApiModelProperty(value = "场景")
    private String platform;

    @ApiModelProperty(value = "团队")
    private String team;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "下单账号")
    private String userAccount;

    @ApiModelProperty(value = "付款账号")
    private String payUserAccount;

    @ApiModelProperty(value = "整单货款")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "整单运费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "整单优惠")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "整单抵扣")
    private BigDecimal walletDeductionFee;

    @ApiModelProperty(value = "支付状态")
    private String paidStr;

    @ApiModelProperty(value = "支付方式:weixin==微信支付,alipay==支付宝支付,yue==余额支付，wallet==积分支付, lianlian==连连支付, confirmPay==人工确认")
    private String orderPayType;

    @ApiModelProperty(value = "支付方法")
    private String payMethod;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App，wallet==积分支付, lianlian==连连支付")
    private String payChannel;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private String status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private String refundStatus;

    @ApiModelProperty(value = "订单详情ID")
    private Integer orderDetailId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品编码")
    private String productBarCode;

    @ApiModelProperty(value = "商品数量")
    private Integer productQuantity;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "商品运费")
    private BigDecimal productPostage;

    @ApiModelProperty(value = "商品优惠")
    private BigDecimal productCouponPrice;

    @ApiModelProperty(value = "商品抵扣")
    private BigDecimal productWalletDeductionFee;

    @ApiModelProperty(value = "商品抵扣明细")
    private List<ProductDeduction> walletDeductionList;

    @ApiModelProperty(value = "物料名称")
    private String materialsName;

    @ApiModelProperty(value = "物料编码")
    private String materialsCode;

    @ApiModelProperty(value = "物料数量")
    private Integer materialsQuantity;;

    @ApiModelProperty(value = "物料总价")
    private BigDecimal materialsPrice;

    @ApiModelProperty(value = "收货人")
    private String realName;

    @ApiModelProperty(value = "收货人手机")
    private String userPhone;

    @ApiModelProperty(value = "配送方式  1=快递 ，2=门店自提")
    private String shippingType;

    @ApiModelProperty(value = "收货详情地址")
    private String userAddress;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "商户备注")
    private String merchantRemark;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
