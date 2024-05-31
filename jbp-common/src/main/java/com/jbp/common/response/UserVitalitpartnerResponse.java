package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserVitalitpartnerResponse对象", description="用户合作伙伴")
public class UserVitalitpartnerResponse implements Serializable {

    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "区域")
    private String area;
    @ApiModelProperty("店铺区域图标")
    private List<String> list;


}
