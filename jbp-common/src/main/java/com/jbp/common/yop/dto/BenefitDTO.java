package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BenefitDTO implements Serializable {

    private String benefitName;//受益人姓名
    private String benefitIdType;//ID_CARD：身份证

    private String benefitIdNo;//受益人证件号码
    private String benefitStartDate;// 受益人证件生效日期，支持格式：yyyyMMdd

    private String benefitExpireDate;//受益人证件失效日期， forever(长期有效)
    private String benefitImageFont;//受益人证件照正面
    private String benefitImageBack;// 受益人证件照反面
    private String benefitAddress;//受益人地址
}
