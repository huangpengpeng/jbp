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
public class ScoreDownLoadExcel implements Serializable {

    @ExcelProperty(value = "账户编号", index = 0)
    private Integer uid;

    @ExcelProperty(value = "得奖账户", index = 1)
    private String account;

    @ExcelProperty(value = "账户昵称", index = 2)
    private String nickName;

    @ExcelProperty(value = "团队名称", index = 3)
    private String teamName;

    @ExcelProperty(value = "等级名称", index = 4)
    private String capaName;

    @ExcelProperty(value = "星级名称", index = 5)
    private String capaXsName;

    @ExcelProperty(value = "订单业绩", index = 6)
    private BigDecimal score;

    @ExcelProperty(value = "订单单号", index = 7)
    private String orderSn;

    @ExcelProperty(value = "下单产品", index = 8)
    private String productName;

    @ExcelProperty(value = "产品编码", index = 9)
    private String barCode;

    @ExcelProperty(value = "产品数量", index = 10)
    private Integer payNum;

    @ExcelProperty(value = "支付金额", index = 11)
    private BigDecimal payPrice;

    @ExcelProperty(value = "下单用户", index = 12)
    private Integer orderUid;

    @ExcelProperty(value = "下单账户", index = 13)
    private String orderAccount;

    @ExcelProperty(value = "付款时间", index = 14, format = "yyyy-MM-dd hh:mm:ss")
    private Date payTime;

    @ExcelProperty(value = "开始时间", index = 15, format = "yyyy-MM-dd hh:mm:ss")
    private Date startTime;

    @ExcelProperty(value = "结束时间", index = 16, format = "yyyy-MM-dd hh:mm:ss")
    private Date endTime;

    @ExcelProperty(value = "折算后业绩", index = 17)
    private BigDecimal score2;


}
