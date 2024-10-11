package com.jbp.common.jdpay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class JdPayOauth2Response implements Serializable {

    private String access_token;//	接口调用令牌
    private String expires_in;//	令牌有效时间, 单位秒
    private String refresh_token;//是用来程序方式延长 token 有效期, token 维度新增加 refresh_token 调用次数限制为 500次/月
    private String scope;//用户授权的作用域，使用逗号（,）分隔
    private String xid;//用户的唯一标识
}
