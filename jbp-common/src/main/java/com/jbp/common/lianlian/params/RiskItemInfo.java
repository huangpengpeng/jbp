package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RiskItemInfo implements Serializable {

    public RiskItemInfo(String frms_ware_category, String user_info_mercht_userno, String user_info_bind_phone,
                        String user_info_dt_register, String goods_name) {
        this.frms_ware_category = frms_ware_category;
        this.user_info_mercht_userno = user_info_mercht_userno;
        this.user_info_bind_phone = user_info_bind_phone;
        this.user_info_dt_register = user_info_dt_register;
        this.goods_name = goods_name;
    }

    /**
     * 风控类目找连连要
     */
    private String frms_ware_category;

    /**
     * 平台用户ID
     */
    private String user_info_mercht_userno;

    /**
     * 用户手机号
     */
    private String user_info_bind_phone;

    /**
     * 注册时间
     */
    private String user_info_dt_register;

    /**
     * 商品名称
     */
    private String goods_name;

    // 业务来源。H5。
    private String frms_client_chnl;
    // 用户交易请求IP
    private String frms_ip_addr;
    // 用户授权标记。0  1
    private String  user_auth_flag;

}
