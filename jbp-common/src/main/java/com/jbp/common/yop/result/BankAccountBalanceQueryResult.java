package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountBalanceQueryResult extends BaseYopResponse {


    private String returnCode;

    private  String returnMsg;

    private String merchantNo; // 商编

    private String accountAmt; // 账户余额

    private String useableAmt; // 可用金额

    private String frozenAmt; // 冻结金额


    @Override
    public boolean validate() {
        return true;
    }
}
