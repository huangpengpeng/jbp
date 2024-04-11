package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankStoreClerkAdminListResponse对象", description = "共享仓门店店员列表对象")
public class TankStoreClerkAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer userId;
    private String username;
    private String cusername;
    private String cmobile;
    private String createdTime;
}
