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
@ApiModel(value="UserVisaRecordResponse对象", description="法大大签约对象")
public class UserVisaRecordResponse {


    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    @ApiModelProperty(value = "用户名")
    private String nickName;


    @ApiModelProperty(value = "法大大id")
    private String taskId;

    @ApiModelProperty(value = "是否签署")
    private Boolean visa;

    @ApiModelProperty(value = "合同名称")
    private String contract;


}
