package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_score_flow")
@ApiModel(value="UserScoreFlow对象", description="用户分数详情记录表")
public class UserScoreFlow extends BaseModel {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "时间")
    private Date createTime;

    @ApiModelProperty(value = "描述")
    private String desc;

}
