package com.jbp.common.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErpOrderShipSyncRequest implements Serializable {

    private String ordersSn;

    private String shipName;

    private String shipNo;
}
