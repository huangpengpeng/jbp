package com.jbp.common.model.agent;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Objects;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

import java.math.BigDecimal;

/**
 * 商品佣金
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_comm")
@ApiModel(value="ProductComm对象", description="商品佣金")
public class ProductComm extends BaseModel {

    private static final long serialVersionUID = 1L;

    public ProductComm(Integer productId, Integer type, String name, BigDecimal scale, String rule, Boolean  status) {
        this.productId = productId;
        this.type = type;
        this.name = name;
        this.scale = scale;
        this.rule = rule;
        this.status = status;
    }

    public Boolean hasError() {
        if (StringUtils.isEmpty(rule)) {
            return true;
        }
        return ObjectUtils.allNotNull(productId, type, name, scale);
    }

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "佣金类型")
    private Integer type;

    @ApiModelProperty(value = "佣金名称")
    private String name;

    @ApiModelProperty(value = "系数")
    private BigDecimal scale;

    @ApiModelProperty(value = "规则 json对象或者jsonArray 由规则自己解析")
    private String rule;

    @ApiModelProperty(value = "状态 true开启 false 关闭")
    private Boolean status;
}
