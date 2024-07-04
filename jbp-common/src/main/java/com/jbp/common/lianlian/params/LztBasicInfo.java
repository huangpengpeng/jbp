package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztBasicInfo {

    private String business_type; //抖音：DOUYIN
    private String shop_id	;//店铺id。
    private String shop_name	;//	店铺名称。
    private String shop_address	;//	店铺地址url。
    private String province	;//	省份编码。可参考省市编码表。
    private String city	;//	城市编码。可参考省市编码表。
    private String area;//	区县编码。可参考区县编码表。
    private String address	;//	详细地址。
    private String  memo	;//	备注。
    private String  notify_url	;//	备注。
    /**
     *     新网银行：xwbank
     *     华通银行：onebank
     *     厦门国际银行：xib
     */
    private String open_bank	;//可选	String	不限	仅支持单个传入，支持范围根据商户实际配置，目前支持枚举为


}
