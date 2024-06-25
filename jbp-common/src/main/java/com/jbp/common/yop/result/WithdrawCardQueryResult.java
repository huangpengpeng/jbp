package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.dto.BankCardAccountDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class WithdrawCardQueryResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    /**
     * 提现卡列表
     */
    private List<BankCardAccountDto> bankCardAccountList;

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
