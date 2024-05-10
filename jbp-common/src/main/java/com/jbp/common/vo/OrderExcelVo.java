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
public class OrderExcelVo implements Serializable {

    private static final long serialVersionUID = -8330957183745338822L;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单,2-秒杀订单")
    private String type;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "场景")
    private String platform;

    @ApiModelProperty(value = "团队")
    private String team;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "下单账号")
    private String userAccount;

    @ApiModelProperty(value = "付款账号")
    private String payUserAccount;

    @ApiModelProperty(value = "整单运费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "整单优惠")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "用户昵称")
    private String userNickname;

    @ApiModelProperty(value = "整单货款")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付状态")
    private String paidStr;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App")
    private String payChannel;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private String status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private String refundStatus;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "商品信息")
    private String productInfo;

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

    @ApiModelProperty(value = "整单抵扣")
    private BigDecimal walletDeductionFee;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "下单前等级名称")
    private String capaName;

    @ApiModelProperty(value = "成功后等级")
    private String successCapaName;

    @ApiModelProperty(value = "用户是否确认收货")
    private String ifUserVerifyReceive;
}
