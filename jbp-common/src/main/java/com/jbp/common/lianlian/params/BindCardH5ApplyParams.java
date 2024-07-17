package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BindCardH5ApplyParams {



    /**
     * 格式yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * 商户号，ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 用户在商户系统中的唯一编号
     */
    private String user_id;

    /**
     * ANONYMOUSUSER：临时用户
     * INNERUSER：个人用户
     * INNERCOMPANY：企业用户
     */
    private String user_type;

    /**
     * 绑卡方式：
     * BIND_CARD：新增绑卡
     * CHANGE_BIND_CARD：换绑卡
     * 企业用户默认传 CHANGE_BIND_CARD
     * 个人用户默认传 BIND_CARD
     */
    private String bind_cardtype;

    private String txn_seqno;
    private String txn_time;
    private String notify_url;
    private String risk_item;
}
