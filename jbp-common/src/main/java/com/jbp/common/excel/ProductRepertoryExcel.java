package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRepertoryExcel implements Serializable {

    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ExcelProperty(value = "昵称", index = 1)
    private String nickname;

    @ExcelProperty(value = "账号", index = 2)
    private String account;

    @ExcelProperty(value = "团队", index = 3)
    private String teamName;

    @ExcelProperty(value = "商品名称", index = 4)
    private String productName;

    @ExcelProperty(value = "商品编码", index = 5)
    private String barCode;

    @ExcelProperty(value = "库存数", index = 6)
    private Integer count;

}
