package com.jbp.common.kqbill.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KqPayInfoResult implements Serializable {

    /**
     * 商户编号
     */
    private String merchantCode;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 应用标识
     */
    private String applyName;

    /**
     * 域名
     */
    private String host;

    /**
     * 客户端ip
     */
    private String terminalIp;

    /**
     * 状态 0 关闭  1开启
     */
    private String status;
}
