package com.jbp.common.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatformWalletRequest对象", description = "平台积分请求对象")
public class PlatformWalletRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("钱包类型")
    private Integer type;
}
