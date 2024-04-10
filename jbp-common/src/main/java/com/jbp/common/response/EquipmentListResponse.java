package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EquipmentListResponse对象", description = "共享仓设备列表对象")
public class EquipmentListResponse implements Serializable {




    private static final long serialVersionUID = 1L;

    private String equipmentSn;
    private String imei;
    private String name;
    private String activateStatus;
    private String onlineStatus;
    private String useStatus;
    private String prohibitStatus;
    private String status;


    private  String equipmentStatus;


}
