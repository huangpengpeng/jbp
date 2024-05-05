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
@TableName("tmp_user")
@ApiModel(value = "TmpUser对象", description = "临时用户表")
public class TmpUser extends BaseModel {

    @ApiModelProperty(value = "原系统ID")
    private Integer orgId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "原系统上级")
    private Integer orgPid;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "是否绑定上级")
    private Boolean ifBand;

}
