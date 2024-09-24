package com.jbp.common.lianlian.result;


import com.jbp.common.lianlian.params.QueryPaymentOrderInfo;
import com.jbp.common.lianlian.params.QueryPaymentPayeeInfo;
import com.jbp.common.lianlian.params.QueryPaymentPayerInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 支付结果查询 响应参数
 */
@Data
@EqualsAndHashCode
public class QueryPaymentResult {

    public QueryPaymentResult(String ret_code, String ret_msg, String oid_partner, String txn_type, String accounting_date,
                              String finish_time, String accp_txno, String chnl_txno, String txn_status,
                              QueryPaymentOrderInfo orderInfo, List<QueryPaymentPayerInfo> payerInfo, List<QueryPaymentPayeeInfo> payeeInfo) {
        this.ret_code = ret_code;
        this.ret_msg = ret_msg;
        this.oid_partner = oid_partner;
        this.txn_type = txn_type;
        this.accounting_date = accounting_date;
        this.finish_time = finish_time;
        this.accp_txno = accp_txno;
        this.chnl_txno = chnl_txno;
        this.txn_status = txn_status;
        this.orderInfo = orderInfo;
        this.payerInfo = payerInfo;
        this.payeeInfo = payeeInfo;
    }

    public QueryPaymentResult() {
    }

    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    /*
    交易类型。
    用户充值：USER_TOPUP
    商户充值：MCH_TOPUP
    普通消费：GENERAL_CONSUME
    担保消费：SECURED_CONSUME
    担保确认：SECURED_CONFIRM
    内部代发：INNER_FUND_EXCHANGE
    外部代发：OUTER_FUND_EXCHANGE
     */
    private String txn_type;
    // 账务日期
    private String accounting_date;
    // 支付完成时间
    private String finish_time;
    // ACCP系统交易单号
    private String accp_txno;
    // 渠道交易单号
    private String chnl_txno;
    /*
    支付交易状态。
    TRADE_WAIT_PAY:交易处理中
    TRADE_SUCCESS:交易成功
    TRADE_CLOSE:交易失败
    支付交易状态以此为准，商户必须依据此字段值进行后续业务逻辑处理。
     */
    private String txn_status;
    // 商户订单信息
    private QueryPaymentOrderInfo orderInfo;
    // 付款方信息（组合支付场景返回付款方信息数组）
    private List<QueryPaymentPayerInfo> payerInfo;
    // 收款方信息（交易分账场景返回收款方信息数组）
    private List<QueryPaymentPayeeInfo> payeeInfo;
}
