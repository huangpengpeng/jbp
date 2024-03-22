package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品查询请求对象
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
@ApiModel(value = "ProductSearchRequest对象", description = "商品查询请求对象")
public class ProductSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型（-1:搜素可以加条件 0:全部   1：出售中（已上架），2：仓库中（未上架），3：已售罄，4：警戒库存，5：回收站,6:待审核，7：审核失败）")
    @NotNull(message = "商品类型不能为空")
    @Range(min = -1, max = 7, message = "未知的商品类型")
    private int type;

    @ApiModelProperty(value = "平台商品分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "商户商品分类ID")
    private String cateId;

    @ApiModelProperty(value = "关键字搜索，支持(商品名称, 关键字)")
    private String keywords;

    @ApiModelProperty(value = "商户ID,平台端商品列表使用")
    private Integer merId;

    @ApiModelProperty(value = "商户是否自营：0-非自营，1-自营,平台端商品列表使用")
    private Integer isSelf;
    @ApiModelProperty(value = "物料编码")
    private String materialsCode;
}
