package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.dto.AccountBalanceInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class AllAccountBalanceQueryResult extends BaseYopResponse {

    private String returnCode;//响应成功返回码为UA00000
    private String returnMsg;//返回信息
    private String initiateMerchantNo;//发起方商户编号
    private String merchantNo;//商户编号
    private String totalAccountBalance;//账户总余额
    private List<AccountBalanceInfoDto> accountInfoList; // 账户列表


    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
