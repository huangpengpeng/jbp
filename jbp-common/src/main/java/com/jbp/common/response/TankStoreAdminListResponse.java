package com.jbp.common.response;

import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.model.tank.TankStore;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankStoreAdminListResponse对象", description = "共享仓门店列表对象")
public class TankStoreAdminListResponse extends TankStore {

    private static final long serialVersionUID = 1L;

    private String username;



}
