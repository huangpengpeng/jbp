package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundOrderExcel implements Serializable {

    @ExcelProperty(value = "退款订单号",index = 0)
    private String refundOrderNo;

    @ExcelProperty(value = "主订单号",index = 1)
    private String orderNo;

    @ExcelProperty(value = "用户ID",index = 2)
    private Integer uid;

    @ExcelProperty(value = "用户昵称",index = 3)
    private String userNickName;

    @ExcelProperty(value = "售后状态",index = 4)
    private String refundStatus;

    @ExcelProperty(value = "退款金额",index = 5)
    private BigDecimal refundPrice;

    @ExcelProperty(value = "申请退款数量",index = 6)
    private Integer applyRefundNum;

    @ExcelProperty(value = "商品名称",index = 7)
    private String productName;

    @ExcelProperty(value = "商户名称",index = 8)
    private String merName;

    @ExcelProperty(value = "售后类型",index = 9)
    private String afterSalesType;

    @ExcelProperty(value = "收货人姓名",index = 10)
    private String realName;

    @ExcelProperty(value = "收货人电话",index = 11)
    private String userPhone;

    @ExcelProperty(value = "收货人详细地址",index = 12)
    private String userAddress;

    @ExcelProperty(value = "创建时间",index = 13)
    private Date createTime;

    @ExcelProperty(value = "退款时间",index = 14)
    private Date refundTime;
}
