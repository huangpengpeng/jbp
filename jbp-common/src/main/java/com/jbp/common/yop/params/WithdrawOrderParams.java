package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawOrderParams extends BaseYopRequest {

    private String parentMerchantNo; // 平台商编

    private String requestNo; // 提现单号

    private String merchantNo;// 提现商户号

    private String bankCardId; // 提现卡bin

    private String receiveType = "REAL_TIME"; // 实时到账

    private String orderAmount; // 提现金额

    private String notifyUrl;
}
