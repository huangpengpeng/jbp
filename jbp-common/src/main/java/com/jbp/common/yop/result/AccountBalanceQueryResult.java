package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountBalanceQueryResult extends BaseYopResponse {


    private String returnCode;

    private String returnMsg;

    private String merchantNo; // 商编


    private String accountCreateTime;// 账户创建时间


    private String balance; // 账户余额

    /**
     * AVAILABLE：可用（账户状态可用，可使用余额进行交易）
     * FROZEN：冻结（账户状态不可用，请勿发起交易）
     * FROZEN_CREDIT：冻结止收（当前账户请勿发起资金入账交易）
     * FROZEN_DEBIT：冻结止付（当前账户状态下请勿发起资金出款交易）
     */
    private String accountStatus;


    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
