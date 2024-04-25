package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserVisaOrderRequest对象", description = "合同签订请求对象")
public class UserVisaOrderRequest implements Serializable {

    private Integer id;
    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    private String openTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "面试结果")
    private String interview;

    @ApiModelProperty(value = "考试结果")
    private String examination;

    @ApiModelProperty(value = "元气商城Id")
    private String vitalityId;


    @ApiModelProperty(value = "申请时间")
    private String applyTime;

    @ApiModelProperty(value = "考试通过时间")
    private String examinationTime;

    @ApiModelProperty(value = "面试通过时间")
    private String interviewTime;



}
