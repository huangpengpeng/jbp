package com.jbp.common.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动请求对象
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
@ApiModel(value = "SeckillActivityRequest对象", description = "秒杀活动请求对象")
public class SeckillActivityRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀活动ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "秒杀活动名称")
    private String name;

    @ApiModelProperty(value = "秒杀开始日期")
    private String startDate;

    @ApiModelProperty(value = "秒杀结束日期")
    private String endDate;

    @ApiModelProperty(value = "活动期间单笔下单购买数量，0不限制")
    private Integer oneQuota;

    @ApiModelProperty(value = "全部活动期间，用户购买总数限制，0不限制")
    private Integer allQuota;

    @ApiModelProperty(value = "开启状态: 0=关闭 1=开启")
    private Integer isOpen;

    @ApiModelProperty(value = "状态:0未开始，1进行中，2已结束")
    private Integer status;

    @ApiModelProperty(value = "删除标记 0=未删除 1=删除")
    private Integer isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
