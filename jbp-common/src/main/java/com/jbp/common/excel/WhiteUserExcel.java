package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhiteUserExcel implements Serializable {

    @ExcelProperty(value = "用户ID", index = 0)
    private Integer uid;

    @ExcelProperty(value = "用户账户", index = 1)
    private String account;

    @ExcelProperty(value = "白名单ID", index = 2)
    private Long whiteId;

    @ExcelProperty(value = "白名单名称", index = 3)
    private String whiteName;

    @ExcelProperty(value = "单号", index = 4)
    private String ordersSn;

    @ExcelProperty(value = "创建时间", index = 5)
    private Date createTime;
}
