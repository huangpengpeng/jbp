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
@ApiModel(value = "TankStoreRelationListResponse对象", description = "舱主关联店主列表")
public class TankStoreClerkManagerListResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    private String username;
    private String   phone;
    private Long storeUserId;
    private Date createdTime;
    private Long id;
    private String name;
    private String clerkUserId;
}
