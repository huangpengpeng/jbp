package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信调起支付参数对象
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
@ApiModel(value="DeclUserInfoResultVo对象", description="报单用户信息对象")
public class DeclUserInfoResultVo {

    @ApiModelProperty(value = "用户名称")
    private String userUame;

    @ApiModelProperty(value = "用户账号")
    private String account;

    @ApiModelProperty(value = "用户等级")
    private String capa;

    @ApiModelProperty(value = "服务人账号")
    private String raccount;

    @ApiModelProperty(value = "服务市场")
    private Integer node;

}
