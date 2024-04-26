package com.jbp.common.response;

import com.jbp.common.model.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@ApiModel(value="UserPlatformInfoResponse对象", description="用户各平台账号信息响应对象")
public class UserPlatformInfoResponse extends User {

    private static final long serialVersionUID = 1387727608277207652L;

    @ApiModelProperty(value = "用户信息")
    private User user;

    @ApiModelProperty(value = "平台图片")
    private String imgUrl;
    @ApiModelProperty(value = "平台名称")
    private String platform;

    @ApiModelProperty(value = "平台微信appId")
    private String appId;

    @ApiModelProperty(value = "是否邀请")
    private Boolean invite;
}
