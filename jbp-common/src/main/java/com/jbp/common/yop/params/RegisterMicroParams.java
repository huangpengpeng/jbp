package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RegisterMicroParams extends BaseYopRequest {


    private String requestNo;//入网请求号 商户自己生成 示例值：YBRWQQH20210622XXXXXX


    /**
     * a. 入驻商户：
     * 1、只能入驻到特定平台商的下级；2、目前仅支持小微的签约类型商户；
     * b. 分账接收方：
     * 1、只能作为特定平台商的下级商户；2、目前仅支持小微的签约类型商户。
     */
    private String businessRole;//入网商户的业务角色：  SETTLED_MERCHANT:入驻商户； SHARE_MERCHANT：分账接收方（仅允许开通结算产品，到银行账户）


    private String parentMerchantNo;// 对应平台商的商户编号。

    private String merchantSubjectInfo;//商户主体信息 示例值：{ "signName":"商户签约名", "shortName":"商户简称" }

    private String merchantCorporationInfo;// 商户法人信息示例值：{ "legalLicenceType":"法人证件类型", "legalLicenceNo":"法人证件编号", "legalLicenceFrontUrl":"法人证件正面照片地址", "legalLicenceBackUrl":"法人证件背面照片地址", "mobile":"法人手机号" }

    private String businessAddressInfo;// 经营地址 示例值：{ "province":"经营省", "city":"经营市", "district":"经营区", "address":"经营地址" }

    private String accountInfo;// 结算账户信息 示例值：{"bankAccountType":"银行账户类型","bankCardNo":"银行账户号码","bankCode":"开户总行编码"}

    /**
     * 1.用于接收电子签章地址，完成协议签署；
     * 2.用于接收审核已驳回状态下的原因；
     * 3.用于接收入网完成的通知。
     */
    private String notifyUrl;// 入网结果通知


    private String productInfo;//开通产品信息


}
