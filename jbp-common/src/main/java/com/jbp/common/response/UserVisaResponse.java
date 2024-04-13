package com.jbp.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserVisaResponse对象", description="法大大签约对象")
public class UserVisaResponse {

    @ApiModelProperty(value = "平台")
    private String platfrom;

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

}
