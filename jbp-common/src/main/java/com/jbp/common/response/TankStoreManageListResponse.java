package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankStoreManageListResponse对象", description = "共享仓门店管理列表对象")
public class TankStoreManageListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer enumber;
    private Integer number;

    private String address;
    private Integer id;





}
