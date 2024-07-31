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
public class UserCapaSnapshotExcel implements Serializable {

    @ExcelProperty(value = "id",index = 0)
    private Long id;

    @ExcelProperty(value = "账号",index = 1)
    private String account;

    @ExcelProperty(value = "等级名称",index = 2)
    private String capaName;

    @ExcelProperty(value = "类型",index = 3)
    private String type;

    @ExcelProperty(value = "系统描述",index = 4)
    private String description;

    @ExcelProperty(value = "备注",index = 5)
    private String remark;

    @ExcelProperty(value = "创建时间",index = 6)
    private Date createdTime;







}
