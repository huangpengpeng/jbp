package com.jbp.common.response;

import com.jbp.common.model.user.UserBalanceRecord;
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
@ApiModel(value = "ActivateInfoResponse对象", description = "共享仓启动详情对象")
public class ActivateInfoResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    private String operationSn;

    private String username;
    private String imei;
    private String name;
    private String createdTime;
    private String endTime;
    private String status;
    private String tqname;
    private String equipmentSn;


}
