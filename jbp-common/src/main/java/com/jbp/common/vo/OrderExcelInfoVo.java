package com.jbp.common.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderExcelInfoVo", description = "订单Excel VO对象")
public class OrderExcelInfoVo  implements Serializable {

    private static final long serialVersionUID = -8330957183745338822L;


    @ApiModelProperty(value = "表头 key=list 字段名称 value=表头名称")
    private JSONArray head;

    @ApiModelProperty(value = "导出数据")
    private List<OrderExcelVo> list;
}