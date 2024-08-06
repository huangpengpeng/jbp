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
public class OrdersFundSummaryExcel implements Serializable {

    @ExcelProperty(value = "单号id", index = 0)
    private Integer ordersId;

    @ExcelProperty(value = "团队", index = 1)
    private String name;

    @ExcelProperty(value = "单号", index = 2)
    private String ordersSn;

    @ExcelProperty(value = "支付金额", index = 3)
    private BigDecimal payPrice;

    @ExcelProperty(value = "支出佣金",index = 4)
    private BigDecimal commAmt;

    @ExcelProperty(value = "总PV", index = 5)
    private BigDecimal pv;

    @ExcelProperty(value = "付款时间", index = 6)
    private Date payTime;


}
