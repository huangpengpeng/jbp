package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.dto.CbecOrderDetailDto;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.CbecOrderDetailListHandler;
import com.jbp.common.mybatis.FundClearingProductListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.BooleanUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_cbec_order")
@ApiModel(value = "CbecOrder对象", description = "跨境订单表")
public class CbecOrder extends BaseModel {

    @ApiModelProperty(value = "用户uid")
    private Integer userId;

    @ApiModelProperty(value = "跨境积分账户")
    private String accountNo;

    @ApiModelProperty(value = "跨境手机号")
    private String cbecMobile;

    @ApiModelProperty(value = "跨境单号")
    private String cbecOrderNo;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalFee;

    @ApiModelProperty(value = "商品金额")
    private BigDecimal goodsFee;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postFee;

    @ApiModelProperty(value = "支付积分")
    private BigDecimal score;

    @ApiModelProperty(value = "订单pv值")
    private BigDecimal pv;

    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;

    @ApiModelProperty(value = "订单付款时间")
    private Date paymentTime;

    @ApiModelProperty(value = "订单发货时间")
    private Date shipmentsTime;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "产品详情")
    @TableField(typeHandler = CbecOrderDetailListHandler.class)
    private List<CbecOrderDetailDto> goodsDetails;

    @ApiModelProperty(value = "是否结算")
    private Boolean ifClearing;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "佣金总额")
    private BigDecimal commAmt;


    public CbecOrder(Integer userId, String accountNo, String cbecMobile, String cbecOrderNo, String status,
                     BigDecimal totalFee, BigDecimal goodsFee, BigDecimal postFee, BigDecimal score, BigDecimal pv, Date createTime,
                     Date paymentTime, Date shipmentsTime, Date refundTime, List<CbecOrderDetailDto> goodsDetails, Boolean ifClearing, BigDecimal commAmt) {


        this.userId = userId;
        this.accountNo = accountNo;
        this.cbecMobile = cbecMobile;
        this.cbecOrderNo = cbecOrderNo;
        this.status = status;
        this.totalFee = totalFee;
        this.goodsFee = goodsFee;
        this.postFee = postFee;
        this.score = score;
        this.pv = pv;
        this.createTime = createTime;
        this.paymentTime = paymentTime;
        this.shipmentsTime = shipmentsTime;
        this.refundTime = refundTime;
        this.goodsDetails = goodsDetails;
        this.ifClearing = ifClearing;
        this.commAmt = commAmt;
    }


    public static enum CbecStatusEnum {

        已发货("DELIVERED", "已发货"),
        已付款("UNDELIVERED", "已付款"),
        其他("OTHER", "其他");

        @Getter
        private String code;
        @Getter
        private String name;

        CbecStatusEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public static String parseCode(String code) {
        Optional<CbecStatusEnum> first = Arrays.stream(CbecStatusEnum.values()).filter(s -> s.getCode().equals(code)).findFirst();

        if (BooleanUtils.isNotTrue(first.isPresent())) {
            return null;
        }
        return first.get().getName();
    }


}
