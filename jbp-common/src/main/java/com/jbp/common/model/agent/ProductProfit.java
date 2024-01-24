package com.jbp.common.model.agent;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 购买商品收益
 * type
 * 1 等级
 * 2.星级
 * 3.白名单
 * 4.积分
 * 5.课程
 * 6.门票
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_profit", autoResultMap = true)
@ApiModel(value="ProductProfit对象", description="购买商品收益")
@NoArgsConstructor
public class ProductProfit extends BaseModel {

    private static final long serialVersionUID = 1L;

    public ProductProfit(Integer productId, Integer type, String name, String rule, Boolean status) {
        this.productId = productId;
        this.type = type;
        this.name = name;
        this.rule = rule;
        this.status = status;
    }

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "收益类型")
    private Integer type;

    @ApiModelProperty(value = "收益类型")
    private String name;

    @ApiModelProperty(value = "规则json对象")
    private String rule;

    @ApiModelProperty(value = "状态 true开启 false 关闭")
    private Boolean status;

    public Boolean hasError() {
        if (!ObjectUtils.allNotNull(productId, type, name, rule)) {
            return true;
        }
        if (StringUtils.isAnyBlank(name, rule)) {
            return true;
        }
        return false;
    }
}
