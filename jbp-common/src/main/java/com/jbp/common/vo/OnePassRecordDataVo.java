package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 一号通记录数据对象
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
@ApiModel(value = "OnePassRecordDataVo对象", description = "一号通记录数据对象")
public class OnePassRecordDataVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "创建时间（通用）")
    private String add_time;

    @ApiModelProperty(value = "内容（短信、物流）")
    private Object content;

    @ApiModelProperty(value = "数量（短信、电子面单）")
    private Integer num;

    @ApiModelProperty(value = "手机号（短信、电子面单）")
    private String phone;

    @ApiModelProperty(value = "响应对象字符串（短信）")
    private String response;

    @ApiModelProperty(value = "状态（短信）")
    private Integer status;

    @ApiModelProperty(value = "商品复制url（商品采集）")
    private String url;

    @ApiModelProperty(value = "结果标识/物流状态（商品采集、物流、电子面单）")
    private String _resultcode;

    @ApiModelProperty(value = "快递公司名（物流、电子面单）")
    private String code;

    @ApiModelProperty(value = "发货人姓名（电子面单）")
    private String from_name;

    @ApiModelProperty(value = "收货人姓名（电子面单）")
    private String to_name;

    @ApiModelProperty(value = "订单编号（电子面单）")
    private String order_id;

}
