package com.jbp.common.model.user;

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
import java.util.Date;

/**
 * <p>
 * 签到记录表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_cbec_user")
@ApiModel(value="CbecUser对象", description="跨境账号表")
public class CbecUser extends BaseModel {

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "跨境积分账户")
    private String accountNo;

}
