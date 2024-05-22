package com.jbp.common.yop.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SnMultiChannelOpenAccountDTO implements Serializable {

    private String socialCreditCodeImageUrl; // 营业执照图片

    private String legalMobileNo;// 法人手机号

    private String operatorName; // 经办人姓名

    private String mobileNo;// 经办人手机号

    private String legalCardImageFont;// 法人证件照片正面

    private String legalCardImageBack;// 法人证件照片反面

    private List<BenefitDTO> benefitDTOList; // 最终受益人

}
