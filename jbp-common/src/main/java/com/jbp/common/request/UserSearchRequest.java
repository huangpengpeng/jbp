package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

import com.jbp.common.annotation.StringContains;

import java.io.Serializable;

/**
 * 用户查询请求对象
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
@ApiModel(value = "UserSearchRequest对象", description = "用户查询请求对象")
public class UserSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "昵称（模糊搜索）")
    private String nikename;

    @ApiModelProperty(value = "手机号(全匹配)")
    private String phone;

    @ApiModelProperty(value = "用户标签")
    private String tagIds;

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，H5-H5,iosWx-微信ios，androidWx-微信安卓，ios-ios")
    @StringContains(limitValues = {"wechat", "routine", "h5", "iosWx", "androidWx", "ios"}, message = "请选择正确的用户注册类型")
    private String registerType;

    @ApiModelProperty(value = "是否关联公众号")
    private Boolean isWechatPublic;

    @ApiModelProperty(value = "是否关联小程序")
    private Boolean isWechatRoutine;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    private Boolean status;

    @ApiModelProperty(value = "消费情况")
    private String payCount;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    //时间类型
    @ApiModelProperty(value = "访问情况， 0 = 全部， 1 = 首次， 2 = 访问过， 3 = 未访问", allowableValues = "range[0,1,2,3]")
    @NotNull(message = "访问情况不能为空")
    private Integer accessType = 0;

    @ApiModelProperty(value = "访问时间")
    private String dateLimit;
}
