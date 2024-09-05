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
public class UserSkinExcel implements Serializable {

    @ExcelProperty(value = "ID", index = 0)
    private Integer uid;

    @ExcelProperty(value = "账号", index = 1)
    private String account;

    @ExcelProperty(value = "昵称", index = 2)
    private String nickname;

    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    @ExcelProperty(value = "序列号", index = 4)
    private String recordListNo;

    @ExcelProperty(value = "创建时间", index = 5)
    private Date gmtCreated;
}
