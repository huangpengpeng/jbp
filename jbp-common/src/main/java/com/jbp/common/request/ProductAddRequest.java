package com.jbp.common.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.mybatis.ProductDeductionListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品添加对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductAddRequest对象", description = "商品添加对象")
public class ProductAddRequest implements Serializable {

    private static final long serialVersionUID = -452373239606480650L;

    @ApiModelProperty(value = "商品id|添加时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "商品图片", required = true)
    @NotBlank(message = "商品图片不能为空")
    @Length(max = 255, message = "商品图片名称长度不能超过255个字符")
    private String image;

    @ApiModelProperty(value = "展示图")
    @Length(max = 1000, message = "展示图名称长度不能超过1000个字符")
    private String flatPattern;

    @ApiModelProperty(value = "轮播图", required = true)
    @NotBlank(message = "轮播图不能为空")
    @Length(max = 2000, message = "轮播图名称长度不能超过2000个字符")
    private String sliderImage;

    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空")
    @Length(max = 128, message = "商品名称长度不能超过128个字符")
    private String name;

    @ApiModelProperty(value = "商品简介", required = true)
    @NotBlank(message = "商品简介不能为空")
    @Length(max = 256, message = "商品简介长度不能超过256个字符")
    private String intro;

    @ApiModelProperty(value = "关键字", required = true)
    @Length(max = 255, message = "关键字长度不能超过255个字符")
    @NotBlank(message = "关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "商户商品分类id|逗号分隔", required = true)
    @Length(max = 64, message = "商品分类组合长度不能超过64个字符")
    private String cateId;

    @ApiModelProperty(value = "品牌id", required = true)
    @NotNull(message = "品牌id不能为空")
    private Integer brandId;

    @ApiModelProperty(value = "平台分类id", required = true)
    @NotNull(message = "平台分类id不能为空")
    private Integer categoryId;

    @ApiModelProperty(value = "保障服务ids(英文逗号拼接)")
    private String guaranteeIds;

    @ApiModelProperty(value = "单位名", required = true)
    @NotBlank(message = "单位名称不能为空")
    @Length(max = 32, message = "单位名长度不能超过32个字符")
    private String unitName;

    @ApiModelProperty(value = "运费模板ID", required = true)
    @NotNull(message = "运费模板ID不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "规格 0单 1多", required = true)
    @NotNull(message = "商品规格类型不能为空")
    private Boolean specType;

    @ApiModelProperty(value = "是否单独分佣", required = true)
    @NotNull(message = "请选择是否单独分佣")
    private Boolean isSub;

    @Valid
    @ApiModelProperty(value = "商品属性", required = true)
    @NotEmpty(message = "商品属性不能为空")
    private List<ProductAttrAddRequest> attr;

    @Valid
    @ApiModelProperty(value = "商品属性详情", required = true)
    @NotEmpty(message = "商品属性详情不能为空")
    private List<ProductAttrValueAddRequest> attrValue;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "优惠券id集合")
    private List<Integer> couponIds;

    @ApiModelProperty(value = "付款方式  0 在线支付  1 积分支付")
    private Integer payType;

    @ApiModelProperty(value = "消费抵扣")
    @TableField(typeHandler = ProductDeductionListHandler.class)
    private List<ProductDeduction> deductionList;

    @ApiModelProperty(value = "购买限制模版ID")
    private Long buyLimitTempId;

    @ApiModelProperty(value = "显示限制模版ID")
    private Long showLimitTempId;

}
