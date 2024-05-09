package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 佣金导出表格
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundClearingExcel implements Serializable {

    @ExcelProperty(value = "佣金编号", index = 0)
    private Long id;

    @ExcelProperty(value = "账户编号", index = 1)
    private String uid;

    @ExcelProperty(value = "得奖账户", index = 2)
    private String account;

    @ExcelProperty(value = "账户昵称", index = 3)
    private String nickName;

    @ExcelProperty(value = "团队名称", index = 4)
    private String teamName;

    @ExcelProperty(value = "佣金名称", index = 5)
    private String commName;

    @ExcelProperty(value = "佣金金额", index = 6)
    private BigDecimal commAmt;

    @ExcelProperty(value = "实发金额", index = 7)
    private BigDecimal sendAmt;

    @ExcelProperty(value = "结算状态", index = 8)
    private String status;

    @ExcelProperty(value = "是否退回", index = 9)
    private Boolean ifRefund;

    @ExcelProperty(value = "流水单号", index = 10)
    private String uniqueNo;

    @ExcelProperty(value = "外部单号", index = 11)
    private String externalNo;

    @ExcelProperty(value = "真实姓名", index = 12)
    private String realName;

    @ExcelProperty(value = "身份证", index = 13)
    private String idCardNo;

    @ExcelProperty(value = "预留手机号", index = 14)
    private String phone;

    @ExcelProperty(value = "银行名称", index = 15)
    private String bankName;

    @ExcelProperty(value = "银行卡号", index = 16)
    private String bankCode;

    @ExcelProperty(value = "结算时间", index = 17, format="yyyy-MM-dd hh:mm:ss")
    private Date clearingTime;

    @ExcelProperty(value = "创建时间", index = 18, format="yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @ExcelProperty(value = "描述", index = 19)
    private String description;

    @ExcelProperty(value = "备注", index = 20)
    private String remark;
}
