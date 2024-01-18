package com.jbp.common.response;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import netscape.javascript.JSObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderCenterResponse对象", description="个人中心订单数量响应对象")
public class OrderCenterResponse {
    @ApiModelProperty(value = "编号")
    private Integer id;
    @ApiModelProperty(value = "对应的数据组id")
    private Integer gid;
    @ApiModelProperty(value = "数据值")
    private JSONArray value;

}
