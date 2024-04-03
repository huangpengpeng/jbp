package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_receipt", autoResultMap = true)
@ApiModel(value="LztReceipt对象", description="回执单下载")
@NoArgsConstructor
public class LztReceipt extends BaseModel {

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "请求流水号")
    private String txnSeqno;

    @ApiModelProperty(value = "交易单号")
    private String tradeTxnSeqno;

    @ApiModelProperty(value = "交易类型")
    private String tradeBillType;

    @ApiModelProperty(value = "订单金额")
    private String totalAmount;

    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "ACCP系统单号")
    private String tradeAccpTxno;

    @ApiModelProperty(value = "电子回单流水号")
    private String receiptAccpTxno;

    @ApiModelProperty(value = "下载授权令牌")
    private String token;

}
