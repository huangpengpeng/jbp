package com.jbp.common.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_jushuitan_config")
@ApiModel(value = "JushuitanConfig对象", description = "聚水潭erp")
public class JushuitanConfig implements Serializable {

    public JushuitanConfig(String accessToken, String appKey, String appSecret, String cancelCallApi, Long expiresIn, String refreshToken, String refundCallApi,
                           String repCallApi, String scope, String shipCallApi, String shopId) {
        this.accessToken = accessToken;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.cancelCallApi = cancelCallApi;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refundCallApi = refundCallApi;
        this.repCallApi = repCallApi;
        this.scope = scope;
        this.shipCallApi = shipCallApi;
        this.shopId = shopId;
    }


    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "appkey")
    private String appKey;

    @ApiModelProperty(value = "appSecret")
    private String appSecret;

    @ApiModelProperty(value = "取消订单推送地址")
    private String cancelCallApi;

    @ApiModelProperty(value = "过期时间单位秒")
    private Long expiresIn;
    @ApiModelProperty(value = "刷新TOKEN")
    private String refreshToken;
    @ApiModelProperty(value = "售后收货推送地址")
    private String refundCallApi;
    @ApiModelProperty(value = "库存变动推送地址")
    private String repCallApi;
    @ApiModelProperty(value = "授权范围")
    private String scope;
    @ApiModelProperty(value = "发货推送地址")
    private String shipCallApi;
    @ApiModelProperty(value = "店铺ID")
    private String shopId;

}
