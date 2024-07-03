package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MerchantInfoModifyParams extends BaseYopRequest {

    private String requestNo;//请求号 示例值：a04cf8bded8b4413a43ab455b21eedcd
    private String merchantNo;//商户编号
    private String notifyUrl;//回调地址

    private String merchantSubjectInfo; // { "licenceUrl":"商户证件照片地址", "signName":"商户签约名", "licenceNo":"商户证件号码", "shortName":"商户简称" }

    private String businessAddressInfo;

    private String accountInfo;
    //
    private String merchantCorporationInfo;

}
