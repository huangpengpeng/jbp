package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单移动端列表数据响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserRiseIndexResponse对象", description="用户升级指标对象")
public class UserRiseIndexResponse implements Serializable {

    private static final long serialVersionUID = 1387727608277207652L;

    @ApiModelProperty(value = "下一等级名称")
    private String capaName;

    @ApiModelProperty(value = "用户指标")
    private Integer userIndex;
    @ApiModelProperty(value = "实际完成人数")
    private Integer actualUser;

    @ApiModelProperty(value = "业绩指标")
    private BigDecimal performanceIndex;
   @ApiModelProperty(value = "实际业绩")
    private BigDecimal actualPerformance;
}
