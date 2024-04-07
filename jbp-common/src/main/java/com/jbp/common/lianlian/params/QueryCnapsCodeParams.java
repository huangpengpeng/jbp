package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueryCnapsCodeParams {

    public QueryCnapsCodeParams(String timestamp, String oid_partner) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
    }

    private String timestamp;

    private String oid_partner;

    // 银行编码
    private String bank_code;

    // 开户支行名称， 支持模糊查询。
    private String brabank_name;

    // 开户行所在省市编码， 标准地市编码。 可参考省市编码表。
    private String city_code;


}
