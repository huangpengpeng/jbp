package com.jbp.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jbp.common.model.user.UserVisaOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserVisaOrderRecordResponse对象", description="法大大签约订单对象")
public class UserVisaOrderRecordResponse extends UserVisaOrder {

    @ApiModelProperty(value = "用户名")
    private String nickName;

}
