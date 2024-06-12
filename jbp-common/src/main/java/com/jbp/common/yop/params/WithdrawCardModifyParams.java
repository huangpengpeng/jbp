package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WithdrawCardModifyParams extends BaseYopRequest {

    private String merchantNo; //商户编号

    private String bindId;// 卡bin

    private String accountNo;// 银行账号

    private String bankCardOperateType; // MODIFY:修改 CANCELLED:注销

    private String bankCode; // 开户行编码

    private String branchCode; // 银行支行编码

}
