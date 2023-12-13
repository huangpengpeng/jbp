package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import com.jbp.common.annotation.StringContains;

/**
 * 商户端用户查询请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MerchantUserSearchRequest对象", description="商户端用户查询请求对象")
public class MerchantUserSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "昵称（模糊搜索）")
    private String nikename;

    @ApiModelProperty(value = "手机号(全匹配)")
    private String phone;

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，H5-h5")
    @StringContains(limitValues = {"wechat","routine","h5"}, message = "请选择正确的用户注册类型")
    private String registerType;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "关注时间")
    private String dateLimit;
}
