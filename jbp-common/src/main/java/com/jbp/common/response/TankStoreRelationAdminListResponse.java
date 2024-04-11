package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankStoreRelationAdminListResponse对象", description = "共享仓门店店住列表对象")
public class TankStoreRelationAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;
    private Integer tankUserId;
    private String username;
    private Integer storeUserId;
    private String storeusername;
    private String mobile;
    private String createdTime;
}
