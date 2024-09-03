package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.OrderRegisterHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_ext", autoResultMap = true)
@ApiModel(value="OrderExt对象", description="订单扩展信息")
public class OrderExt extends BaseModel {

    public OrderExt(Integer shardUid, String orderNo, Long capaId, Long capaXsId,
                    Long successCapaId, Long successCapaXsId, OrderRegister orderRegister) {
        this.shardUid = shardUid;
        this.orderNo = orderNo;
        this.capaId = capaId;
        this.capaXsId = capaXsId;
        this.successCapaId = successCapaId;
        this.successCapaXsId = successCapaXsId;
        this.orderRegister = orderRegister;
    }

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "分享用户")
    private Integer shardUid;

    @ApiModelProperty(value = "下单前等级")
    private Long capaId;

    @ApiModelProperty(value = "=下单前星级")
    private Long capaXsId;

    @ApiModelProperty(value = "成功后等级")
    private Long successCapaId;

    @ApiModelProperty(value = "成功后星级")
    private Long successCapaXsId;

    @ApiModelProperty(value = "下单注册账号注册信息")
    @TableField(value = "order_register", typeHandler = OrderRegisterHandler.class)
    private OrderRegister orderRegister;

    @ApiModelProperty(value = "服务码")
    private String serverSn;


    @ApiModelProperty(value = "AI服务码")
    private String aiServerSn;
    @ApiModelProperty(value = "AI服务天数")
    private Integer aiDay;


    @ApiModelProperty(value = "AI服务码是否推送")
    private Boolean aiPushServer;

    @ApiModelProperty(value = "订单商品扩展信息")
    private String orderGoodsInfo;

    @ApiModelProperty(value = "序列号")
    private String number;
}
