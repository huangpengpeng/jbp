package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountTransferOrderParams extends BaseYopRequest {

    private String parentMerchantNo; // 主商户号

    private String requestNo;//  商户请求号

    private String fromMerchantNo;//  转出

    private String toMerchantNo;//  转入

    /**
     * 转入方账户类型
     * 可选项如下:
     * FUND_ACCOUNT:商户支付账户
     * MARKET_ACCOUNT:营销账户（当为此参数时，fromMerchantNo和toMerchantNo需要保持一致）
     * FEE_ACCOUNT:手续费账户（当为此参数时，fromMerchantNo和toMerchantNo需要保持一致）
     * SPECIAL_FUND_ACCOUNT:专款账户（当为此参数时，toAccountNo不能为空）
     * 默认值：默认FUND_ACCOUNT
     */
    private String toAccountType = "FUND_ACCOUNT";

    private String orderAmount; // 转账金额

    private String usage = "服务费";

    private String notifyUrl;
}
