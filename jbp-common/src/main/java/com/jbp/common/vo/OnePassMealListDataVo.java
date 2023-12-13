package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 一号通用户套餐列表数据对象
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
@ApiModel(value = "OnePassMealListDataVo对象", description = "一号通用户套餐列表数据对象")
public class OnePassMealListDataVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "可用条数")
    private Integer id;

    @ApiModelProperty(value = "数量")
    private Integer num;

    @ApiModelProperty(value = "价格")
    private String price;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类型:sms-短信，copy-商品采集，expr_query-物流查询，expr_dump-电子面单")
    private String type;
}
