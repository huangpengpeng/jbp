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
public class WalletExcel implements Serializable {

    @ExcelProperty(value = "账户", index = 0)
    private String account;

    @ExcelProperty(value = "用户昵称", index = 1)
    private String nickname;

    @ExcelProperty(value = "团队", index = 2)
    private String name;

    @ExcelProperty(value = "钱包名称", index = 3)
    private String typeName;

    @ExcelProperty(value = "余额", index = 4)
    private BigDecimal balance;

    @ExcelProperty(value = "冻结金额", index = 5)
    private BigDecimal freeze;








}
