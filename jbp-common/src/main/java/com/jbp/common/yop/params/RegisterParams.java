package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @Author dengmin
 * @Created 2021/4/14 下午7:24
 */
@Setter
@Getter
@NoArgsConstructor
public class RegisterParams extends BaseYopRequest {

    private String parentMerchantNo;
    @NotBlank(message = "请求流水号")
    private String requestNo;
    @NotBlank(message = "入网商户业务角色")
    private String businessRole;
    @NotBlank(message = "商户主体信息")
    private String merchantSubjectInfo;
    @NotBlank(message = "商户法人信息")
    private String merchantCorporationInfo;
    @NotBlank(message = "商户联系人信息")
    private String merchantContactInfo;
    @NotBlank(message = "经营地址")
    private String businessAddressInfo;
    @NotBlank(message = "结算账户信息不能为空")
    private String settlementAccountInfo;
    @NotBlank(message = "通知回调地址不能为空")
    private String notifyUrl;
    @NotBlank(message = "开通产品信息不能为空")
    private String productInfo;
    @NotBlank(message = "开通产品资质不能为空")
    private String productQualificationInfo;
    // 是否开通分账服务  平台商户收款角色  分账给入驻商户角色   标准商户无需开通
    private String functionService;
    private String functionServiceQualificationInfo;
    //行业分类
    private String industryCategoryInfo="";
}
