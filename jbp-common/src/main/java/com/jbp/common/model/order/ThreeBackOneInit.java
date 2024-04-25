package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单发货单表
 * </p>
 *
 * @author HZW
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("three_back_one_init")
@ApiModel(value = "ThreeBackOneInit对象", description = "老系统推三返一记录")
public class ThreeBackOneInit extends BaseModel {

    private static final long serialVersionUID = 1L;



    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "数量")
    private Integer number;

}
