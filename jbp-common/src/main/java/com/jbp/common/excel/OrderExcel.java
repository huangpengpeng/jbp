package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderExcel implements Serializable {

    @ExcelProperty(value = "订单编号", index = 0)
    private Integer id;

    @ExcelProperty(value = "订单类型", index = 1)
    private String type;

    @ExcelProperty(value = "下单场景", index = 2)
    private String platform;

    @ExcelProperty(value = "订单单号", index = 3)
    private String platOrderNo;

    @ExcelProperty(value = "用户ID", index = 4)
    private Integer uid;

    @ExcelProperty(value = "用户昵称", index = 5)
    private String nickname;

    @ExcelProperty(value = "用户账户", index = 6)
    private String account;

    @ExcelProperty(value = "团队名称", index = 7)
    private String teamName;

    @ExcelProperty(value = "付款账户", index = 8)
    private String payAccount;

    @ExcelProperty(value = "付款金额", index = 9)
    private BigDecimal payPrice;

    @ExcelProperty(value = "运费金额", index = 10)
    private BigDecimal payPostage;

    @ExcelProperty(value = "优惠金额", index = 11)
    private BigDecimal couponPrice;

    @ExcelProperty(value = "抵扣金额", index = 12)
    private BigDecimal deductionFee;

    @ExcelProperty(value = "支付状态", index = 13)
    private String paidStr;

    @ExcelProperty(value = "支付方法", index = 14)
    private String payMethod;

    @ExcelProperty(value = "订单状态", index = 15)
    private String status;

    @ExcelProperty(value = "退款状态", index = 16)
    private String refundStatus;


    @ExcelProperty(value = "商品信息", index = 17)
    private String productInfo;

    @ExcelProperty(value = "收货人", index = 18)
    private String realName;

    @ExcelProperty(value = "收货人手机", index = 19)
    private String userPhone;

    @ExcelProperty(value = "配送方式", index = 20)
    private String shippingType;

    @ExcelProperty(value = "省", index = 21)
    private String province;

    @ExcelProperty(value = "市", index = 22)
    private String city;

    @ExcelProperty(value = "区", index = 23)
    private String district;

    @ExcelProperty(value = "街道", index = 24)
    private String street;

    @ExcelProperty(value = "地址详情", index = 25)
    private String address;

    @ExcelProperty(value = "用户备注", index = 26)
    private String remark;

    @ExcelProperty(value = "后台备注", index = 27)
    private String merchantRemark;

    @ExcelProperty(value = "创建时间", index = 28, format = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @ExcelProperty(value = "付款时间", index = 29, format = "yyyy-MM-dd hh:mm:ss")
    private Date payTime;

    @ExcelProperty(value = "下单前等级", index = 30)
    private String capaName;

    @ExcelProperty(value = "下单后等级", index = 31)
    private String successCapaName;

    @ExcelProperty(value = "用户是否确认收货", index = 32)
    private String ifUserVerifyReceive;

    @ExcelProperty(value = "确认收货时间", index = 33)
    private Date receiveTime;

    @ExcelProperty(value = "取消时间", index = 34, format = "yyyy-MM-dd hh:mm:ss")
    private Date cancelTime;

    @ExcelProperty(value = "购物抵扣", index = 35)
    private BigDecimal gouwu;

    @ExcelProperty(value = "奖励抵扣", index = 36)
    private BigDecimal jiangli;

    @ExcelProperty(value = "换购抵扣", index = 37)
    private BigDecimal huangou;

    @ExcelProperty(value = "福券抵扣", index = 38)
    private BigDecimal fuquan;

    @ExcelProperty(value = "发货时间", index = 39)
    private Date shipTime;
}