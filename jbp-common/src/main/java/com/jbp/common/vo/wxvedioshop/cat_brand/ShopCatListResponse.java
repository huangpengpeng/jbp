package com.jbp.common.vo.wxvedioshop.cat_brand;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

import com.jbp.common.vo.wxvedioshop.BaseResultResponseVo;

/** 自定义交易组件 商家类目列表
 * @Auther: 大粽子
 * @Date: 2022/9/26 18:32
 * @Description: 描述对应的业务场景
 */
@Data
public class ShopCatListResponse extends BaseResultResponseVo {

    @ApiModelProperty(value = "获取商家类目集合")
    private List<ShopCatListItemResponse> data;
}
