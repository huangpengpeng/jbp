package com.jbp.common.lianlian.result;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class LztQueryAcctInfoResult {

    /**
     * 0000表示交易申请成功，最终支付结果以支付结果异步通知接口为准
     */
    private String ret_code;

    /**
     * 交易返回描述
     */
    private String ret_msg;

    /**
     * 返回结果信息
     */
    private List<LztQueryAcctInfo> list;


    private String drawBankAcctNo;

    private String drawBankName;

}
