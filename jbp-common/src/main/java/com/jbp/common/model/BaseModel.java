package com.jbp.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * @Author Franky
 * @Date 2021/4/1 10:54
 * @Version 1.0
 */
@Data
public class BaseModel<T extends BaseModel<?>> extends Model {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField( fill = FieldFill.INSERT)
    private Date gmtCreated;

    @JsonIgnore
    @TableField( fill = FieldFill.INSERT_UPDATE, update = "now()")
    private Date gmtModify;
}
