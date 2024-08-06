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
public class FundClearingRecordExcel implements Serializable {

    @ExcelProperty(value = "订单号", index = 0)
    private String externalNo;

    @ExcelProperty(value = "订单金额", index = 1)
    private BigDecimal price;

    @ExcelProperty(value = "付款时间", index = 2)
    private String payTime;

    @ExcelProperty(value = "订单状态", index = 3)
    private String status;

    @ExcelProperty(value = "下单账户", index = 4)
    private String orderAccount;

    @ExcelProperty(value = "下单昵称", index = 5)
    private String orderNickname;

    @ExcelProperty(value = "获得者账户", index = 6)
    private String account;

    @ExcelProperty(value = "获得者昵称", index = 7)
    private String nickname;

    @ExcelProperty(value = "获得金额", index = 8)
    private BigDecimal commAmt;

    @ExcelProperty(value = "退款时间", index = 9)
    private Date refundTime;

}
