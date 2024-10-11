package com.jbp.common.jdpay.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 分佣请求类
 */
@Data
public class JdPaySendCommissionResponse extends BaseResponse {

    /**
     * 返回码说明见章节5.1
     */
    private  String code;

    /**
     * 返回说明码值见章节5.1
     */
    private String message;

    /**
     * 佣金和指令均受理成功时为true
     */
    private boolean success;

    /**
     * 指令受理成功时返回受理时间
     */
    private Date commandTime;

    /**
     * List<CommissionFailInfo>	该字段在返回码为F0003时，展示每条佣金的受理详情（其余失败返回码该字段为null，佣金未受理的原因同message）
     */
    private String data;
}
