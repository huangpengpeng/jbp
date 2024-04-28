package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value="UserInviteInfoResponse对象", description="用户邀请关系详情响应对象")
public class UserInviteInfoResponse implements Serializable {

    private static final long serialVersionUID = 1387727608277207652L;

    @ApiModelProperty(value = "用户名")
    private String nickname;

    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "等级名称")
    private String iconUrl;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "成员人数")
    private Integer oneCount;

    @ApiModelProperty(value = "订单数")
    private String orderCount;

    @ApiModelProperty(value = "加入时间")
    private Date createTime;

    @ApiModelProperty(value = "uid")
    private Integer uid;
    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "等级名称")
    private String xsCapaUrl;

    @ApiModelProperty(value = "是否挂载")
    private Boolean ifMonth;
}
