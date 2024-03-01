package com.jbp.common.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantPayInfo对象", description = "商户支付信息表")
public class MerchantPayInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "连连商户号")
    private String oidPartner;

    @ApiModelProperty(value = "连连私钥")
    private String priKey;


}
