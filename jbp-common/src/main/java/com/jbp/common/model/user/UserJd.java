package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_jd")
@ApiModel(value="UserJd对象", description="用户京东表")
public class UserJd extends BaseModel {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "京东账号")
    private String xid;



}
