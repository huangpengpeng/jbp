package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AcctSerialDetailResult {

    private String ret_code;

    private String ret_msg;

    private String oid_partner; // ACCP系统分配给平台商户的唯一编号。

    private String date_acct; // 账务日期。交易账期，格式：yyyyMMdd

    private String

            oid_acctno; // 账户号

    private String jno_acct;// 资金流水号。ACCP账务系统资金流水唯一标识。

    private String txn_seqno;//商户系统唯一交易流水号。

    private String accp_txnno;//该笔资金流水对应的ACCP系统交易单号：accp_txno。

    /**
     * 交易类型。
     * 用户充值：USER_TOPUP
     * 商户充值：MCH_TOPUP
     * 普通消费：GENERAL_CONSUME
     * 担保消费：SECURED_CONSUME
     * 担保确认：SECURED_CONFIRM
     * 内部代发：INNER_FUND_EXCHANGE
     * 定向内部代发：INNER_DIRECT_EXCHANGE
     * 外部代发：OUTER_FUND_EXCHANGE
     * 账户提现：ACCT_CASH_OUT
     * 手续费收取：SERVICE_FEE
     * 手续费应收应付核销：CAPITAL_CANCEL
     * 垫资调账：ADVANCE_PAY
     */
    private String txn_type;

    private String product_code;//  产品编码。产品编码列表

    private String other_acct;//  对手账号。

    private String other_acct_name;// 对手户名。


    private String txn_time; // yyyyMMddHHmmss

    /**
     * 账户出入账标识
     * DEBIT：出账
     * CREDIT：入账f
     */
    private String flag_dc;

    private String amt; // 出入账金额。单位 元。

    private String amt_bal; // 交易后余额。单位 元

    private String memo; // 资金流水备注

    private String jno_cli;// 商户订单号。资金流水对应的商户交易单号，一条商户订单号可能对应多条资金流水。


}
