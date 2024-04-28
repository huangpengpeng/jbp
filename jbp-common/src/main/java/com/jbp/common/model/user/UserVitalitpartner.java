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
@TableName("eb_user_vitalitpartner")
@ApiModel(value="UserVitalitpartner对象", description="元气合伙人记录表")
public class UserVitalitpartner extends BaseModel {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "用户uid")
    private Integer userId;

    private String type;
    private Boolean enable;
    private String remark;
    private String createTime;
    private String title;
}
