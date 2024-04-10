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
@TableName("eb_tank_store_relation")
@ApiModel(value="TankStoreRelation对象", description="共享仓店主舱主关联")
public class TankStoreRelation extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;


    @ApiModelProperty(value = "舱主id")
    private Long tankUserId;

    @ApiModelProperty(value = "店主id")
    private Long storeUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;





}
