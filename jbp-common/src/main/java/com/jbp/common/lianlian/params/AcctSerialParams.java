package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 资金流水
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class AcctSerialParams {

    public AcctSerialParams(String timestamp, String oid_partner, String user_id, String user_type, String acct_type, String date_start,
                            String date_end, String flag_dc, String page_no, String page_size, String sort_type) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
        this.user_type = user_type;
        this.acct_type = acct_type;
        this.date_start = date_start;
        this.date_end = date_end;
        this.flag_dc = flag_dc;
        this.page_no = page_no;
        this.page_size = page_size;
        this.sort_type = sort_type;
    }

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
     * 用户类型
     * INNERMERCHANT:商户
     * INNERUSER：个人用户
     * INNERCOMPANY：企业用户
     */
    private String user_type;

    /**
     * 账户类型列表
     * USEROWN_PSETTLE	用户自有待结算账户
     * USEROWN_AVAILABLE	用户自有可用账户
     * MCHOWN_PSETTLE	平台商户自有待结算账户
     * MCHOWN_AVAILABLE	平台商户自有可用账户
     * MCHASSURE_PSETTLE	平台商户担保待结算账户
     * MCHASSURE_AVAILABLE	平台商户担保可用账户
     * MCHCOUPON_PSETTLE	平台商户优惠券待结算账户
     * MCHCOUPON_AVAILABLE	平台商户优惠券可用账户
     * MCHFEE_PSETTLE	平台商户手续费结算账户
     * MCHFEE_AVAILABLE	平台商户手续费可用账户
     * BANKCARD_DEBIT	银行账户（借记卡）
     * BANKCARD_CREDIT	银行卡账户（信用卡）
     * BANKCARD_ENTERPRISE	银行账户（对公）
     * THIRD_PARTY	第三方账户
     */
    private String acct_type;

    /**
     * 账期开始时间。交易账期查询开始时间，必须小于等于当前时间，闭区间。格式：yyyyMMddHHmmss
     */
    private String date_start;


    /**
     * 账期结束时间。交易账期查询结束时间，必须大于等于开始时间且小于等于当前时间，闭区间。格式：yyyyMMddHHmmss
     */
    private String date_end;

    /**
     * 账户出入账标识， DEBIT：出账 CREDIT：入账
     */
    private String flag_dc;

    /**
     * 请求页码。表示当前请求第几页，从1开始计数。
     */
    private String page_no;

    /**
     * 每页记录数,每页最大记录数为10。
     */
    private String page_size;

    /**
     * 排序方式
     * DESC:按交易时间降序
     * ASC:按交易时间升序
     */
    private String sort_type;
}
