package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EquipmentNumberInfoAdminListResponse对象", description = "共享仓设备次数明细列表对象")
public class EquipmentNumberInfoAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String type;
    private Integer number;
    private String operationSn;
    private String createdTime;
    private String remark;

}
