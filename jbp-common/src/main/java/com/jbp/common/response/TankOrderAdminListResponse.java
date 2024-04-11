package com.jbp.common.response;

import com.jbp.common.model.order.Order;
import com.jbp.common.model.tank.TankOrders;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankOrderAdminListResponse对象", description = "共享仓订单列表对象")
public class TankOrderAdminListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private TankOrders order;
    private String username;
    private String storeusername;



}
