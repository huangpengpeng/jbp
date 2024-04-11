package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EquipmentNumberAdminListResponse对象", description = "共享仓设备次数列表对象")
public class EquipmentNumberAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private Integer number;



}
