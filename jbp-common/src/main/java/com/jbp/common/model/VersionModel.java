package com.jbp.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * @Author Franky
 * @Date 2021/4/1 10:55
 * @Version 1.0
 */
@Data
public class VersionModel extends BaseModel {

    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version = 1;
}
