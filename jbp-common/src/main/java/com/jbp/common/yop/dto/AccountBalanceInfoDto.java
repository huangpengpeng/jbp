package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AccountBalanceInfoDto implements Serializable {

    /**
     * SETTLE_ACCOUNT:待结算账户
     * FUND_ACCOUNT:商户资金账户
     * MARKET_ACCOUNT:营销账户
     * DIVIDE_ACCOUNT:待分账账户
     * FEE_ACCOUNT:手续费账户
     */
    private String accountType;//账户类型

    private String createTime;//开户时间

    private String balance;//余额

    /**
     * 可选项如下:
     * AVAILABLE:可用
     * FROZEN:冻结
     * FROZEN_CREDIT:冻结止收
     * FROZEN_DEBIT:冻结止付
     * CANCELLATION:销户
     */
    private String accountStatus;//账户状态

}
