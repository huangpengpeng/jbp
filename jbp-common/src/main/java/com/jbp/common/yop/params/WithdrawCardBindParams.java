package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WithdrawCardBindParams extends BaseYopRequest {

    private String merchantNo; //商户编号

    private String bankCardType; // DEBIT_CARD:借记卡  ENTERPRISE_ACCOUNT:对公账号  UNIT_SETTLEMENT_CARD:单位结算卡

    private String accountNo;// 银行账号

    private String bankCode; // 开户行编码

    private String branchCode; // 银行支行编码

}