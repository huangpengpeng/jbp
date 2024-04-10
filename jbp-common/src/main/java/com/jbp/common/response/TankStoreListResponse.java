package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TankStoreListResponse对象", description = "店主门店列表")
public class TankStoreListResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    private String username;
    private String name;
    private String mobile;


}
