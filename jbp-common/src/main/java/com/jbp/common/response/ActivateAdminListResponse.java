package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AccountCapaResponse对象", description="账户等级对象")
public class ActivateAdminListResponse {
    private String operationSn;
    private String storeId;
    private String storeusername;

    private String storename;
    private String name;
    private String imei;
    private String nickName;

    private String createdTime;
    private String endTime;
    private String status;



}
