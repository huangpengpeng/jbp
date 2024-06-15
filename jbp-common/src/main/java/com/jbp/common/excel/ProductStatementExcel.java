package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatementExcel implements Serializable {

    @ExcelProperty(value = "商品Id", index = 0)
    private Integer productId;

    @ExcelProperty(value = "商品条码",index = 1)
    private String barCode;

    @ExcelProperty(value = "商品名称", index = 2)
    private String productName;

    @ExcelProperty(value = "销售数量", index = 3)
    private Integer salesNum;

    @ExcelProperty(value = "销售总价",index = 4)
    private BigDecimal salesPrice;

    @ExcelProperty(value = "周期",index = 5)
    private String date;

}
