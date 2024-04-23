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
    private Integer visaId;


}
