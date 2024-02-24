package com.jbp.common.response;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AliBankcardResponse implements Serializable {

    public AliBankcardResponse(String cardNum, String area, String cardType, String bankName) {
        this.cardNum = cardNum;
        this.area = area;
        this.cardType = cardType;
        this.bankName = bankName;
    }

    @ApiModelProperty("卡号")
    private String cardNum;

    @ApiModelProperty("开户地址")
    private String area;

    @ApiModelProperty("银行卡类型")
    private String cardType;

    @ApiModelProperty("银行名称")
    private String bankName;

    public static AliBankcardResponse get(String result) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject == null) {
            return null;
        }
        JSONObject body = jsonObject.getJSONObject("showapi_res_body");
        if (body == null || !"0".equals(body.getString("ret_code"))) {
            return null;
        }
        return new AliBankcardResponse(body.getString("cardNum"),
                body.getString("area"), body.getString("cardType"), body.getString("bankName"));
    }


    /**
     * {
     *     "showapi_res_error": "",
     *     "showapi_fee_num": 1,
     *     "showapi_res_id": "6562cc099c4640d911282aec",
     *     "showapi_res_body": {
     *         "area": "浙江 - 杭州",
     *         "cardNum": "1111122222",
     *         "cardType": "借记卡",
     *         "logo": "http://static1.showapi.com/app2/banklogo/cmb.png",
     *         "formatBankName": "招商银行",
     *         "bankName": "招商银行",
     *         "tel": "95555",
     *         "simpleCode": "CMB",
     *         "ret_code": 0,
     *         "brand": "银联IC金卡",
     *         "url": "www.cmbchina.com"
     *     },
     *     "showapi_res_code": 0
     * }
     */
}
