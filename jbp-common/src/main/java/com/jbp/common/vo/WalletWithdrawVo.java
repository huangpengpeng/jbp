package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WalletWithdrawVo对象", description="微信模板发送类")
public class WalletWithdrawVo {
}
