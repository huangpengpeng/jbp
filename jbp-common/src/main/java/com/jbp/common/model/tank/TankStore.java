package com.jbp.common.model.tank;

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
@TableName("eb_tank_store")
@ApiModel(value="TankStore对象", description="共享仓门店")
public class TankStore extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;



    @ApiModelProperty(value = "店主id")
    private Long userId;

    @ApiModelProperty(value = "门店名称")
    private String name;

    @ApiModelProperty(value = "门店地址")
    private String address;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "套餐（逗号隔开  10,20,30）")
    private String nest;



}
