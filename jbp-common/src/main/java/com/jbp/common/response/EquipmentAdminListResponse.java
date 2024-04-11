package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EquipmentAdminListResponse对象", description = "共享仓设备列表对象")
public class EquipmentAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String equipmentSn;
    private String orderbondactivateId;
    private String userId;
    private String username;
    private String storeUserId;
    private String storeusername;
    private String storename;
    private String quipmentname;
    private  String imei;
    private  String createdTime;
    private  String type;
    private  String status;
    private  String activateStatus;
    private  String onlineStatus;

    private  String useStatus;
    private  String prohibitStatus;
    private  String id;
}
