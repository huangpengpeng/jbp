package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransferMorepyeePayerInfo implements Serializable {

    /**
     * 付款方类型。
     * 用户：USER
     * 平台商户：MERCHANT
     */
    private String payer_type;

    /**
     * 付款方标识。
     * 付款方为用户时设置user_id
     * 付款方为商户时设置平台商户号
     */
    private String payer_id;

    /**
     * 付款方账户类型。付款方类型为商户时需要指定平台商户账户类型。
     * USEROWN：用户自有可用账户。
     * MCHOWN：平台商户自有可用账户。
     */
    private String payer_accttype;

    /**
     * 支付密码。非委托代发，付款方为用户时必填。
     * 通过密码控件加密成密文传输。
     */
    private String password;
    /**
     * 密码随机因子key。随机因子获取接口返回，或弹框控件回调函数返回。
     */
    private String random_key;

    /**
     * 委托代发协议号。账户+返回的的代扣协议号，委托代发时必输。该字段需要RSA公钥加密传输。
     * 通过用户委托协议签约接口获取。
     */
    private String pap_agree_no;


}
