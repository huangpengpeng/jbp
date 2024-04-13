package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("eb_user_visa")
@ApiModel(value="UserVisa对象", description="法大大签约记录表")
public class UserVisa extends BaseModel {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "是否签署")
    private Boolean visa;

    @ApiModelProperty(value = "合同名称")
    private String contract;

    @ApiModelProperty(value = "法大大id")
    private String taskId;
    @ApiModelProperty(value = "手机号")
    private String phone;


}
