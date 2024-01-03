package com.jbp.common.model.product;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@TableName(value = "eb_Product_Profit", autoResultMap = true)
@ApiModel(value="ProductProfit对象", description="购买商品收益")
public class ProductProfit extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "收益类型")
    private Integer type;

    @ApiModelProperty(value = "收益类型")
    private String name;

    @ApiModelProperty(value = "规则json对象")
    private String rule;


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
