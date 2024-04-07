package com.jbp.common.kqbill.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KqRefundResult implements Serializable {

    /**
     * 返回编码  0000 交易成功 通用 成功
     */
    private String bizResponseCode;

    /**
     * 返回消息
     */
    private String bizResponseMessage;

    /**
     * 商户编号
     */
    private String merchantAcctId;

    public Boolean  ifSuccess(){
        if(this == null){
            return false;
        }
        return "0000".equals(this.bizResponseCode);
    }
}
