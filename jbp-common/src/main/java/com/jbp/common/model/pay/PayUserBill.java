package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_user_bill", autoResultMap = true)
@ApiModel(value = "PayUserBill对象", description = "支付收款用户账单")
public class PayUserBill extends BaseModel {
}
