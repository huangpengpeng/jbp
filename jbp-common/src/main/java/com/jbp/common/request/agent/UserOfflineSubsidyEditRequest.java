package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "UserOfflineSubsidyEditRequest对象", description = "用户线下补贴编辑请求对象")
public class UserOfflineSubsidyEditRequest implements Serializable {

    @ApiModelProperty("id")
    private Long id;
//    @ApiModelProperty("省份")
//    private String province;
//    @ApiModelProperty("城市")
//    private String city;
//    @ApiModelProperty("区域")
//    private String area;
    @ApiModelProperty("状态  申请中 已开通 已取消")
    private String status;
    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市id")
    private Integer cityId;

    @ApiModelProperty(value = "区/县id")
    private Integer areaId;

}
