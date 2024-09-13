package com.jbp.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotteryRecordExcel {

    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ExcelProperty(value = "用户账号", index = 1)
    private String account;

    @ExcelProperty(value = "用户昵称", index = 2)
    private String nickname;

    @ExcelProperty(value = "奖品信息", index = 3)
    private String prizeName;

    @ExcelProperty(value = "中奖类型", index = 4)
    private String prizeType;

    @ExcelProperty(value = "抽奖时间", index = 5)
    private Date createTime;

    @ExcelProperty(value = "备注", index = 6)
    private Date remark;

}
