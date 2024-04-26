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
@TableName("eb_user_visa_order")
@ApiModel(value="UserVisaOrder对象", description="法大大签约订单记录表")
public class UserVisaOrder extends BaseModel {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String mark;
    @ApiModelProperty(value = "签约订单")
    private Long visaId;

    @ApiModelProperty(value = "面试结果")
    private String interview;

    @ApiModelProperty(value = "考试结果")
    private String examination;

    @ApiModelProperty(value = "元气商城Id")
    private String vitalityId;

    @ApiModelProperty(value = "合同签订时间")
    private Date signTime;

    @ApiModelProperty(value = "申请时间")
    private String applyTime;

    @ApiModelProperty(value = "考试通过时间")
    private String examinationTime;

    @ApiModelProperty(value = "面试通过时间")
    private String interviewTime;

    @ApiModelProperty(value = "面试通过时间")
    private String openTime;

}
