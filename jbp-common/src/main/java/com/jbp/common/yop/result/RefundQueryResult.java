package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 查询退款的响应信息
 * @Author dengmin
 * @Created 2021/4/22 上午11:38
 */
@Getter
@Setter
@NoArgsConstructor
public class RefundQueryResult extends BaseYopResponse {

    private String code;
    private String message;

    private String parentMerchantNo;
    private String merchantNo;
    //商户收款请求号
    private String orderId;
    //商户退款请求号
    private String refundRequestId;
    //易宝收款订单号
    private String uniqueOrderNo;
    //易宝退款订单号
    private String uniqueRefundNo;
    //退款申请金额,单位:元
    private String refundAmount;

    private String refundAccountType;
    //退回商户手续费金额,单位:元
    private String returnMerchantFee;
    private String disAccountAmount;
    /**
     * PROCESSING：处理中
     * SUCCESS：退款成功
     * FAILED：退款失败
     * CANCEL:退款关闭,商户线下通知易宝结束该笔退款后返回该状态
     */
    private String status;
    //退款原因的简要描述
    private String description;
    //退款受理时间
    //示例值：2021-01-01 00:00:00
    private String refundRequestDate;
    //退款成功时间
    //示例值：2021-01-01 00:00:00
    private String refundSuccessDate;
    //退款失败原因
    private String failReason;
    //实际退款金额
    //用户付手续费场景下,实际退款金额即退款金额和退费金额之和
    private String realRefundAmount;
    //用户实退金额
    private String cashRefundFee;

    //渠道侧优惠退回列表
    private BankPromotionInfoDTOList[] bankPromotionInfoDTOList;

    public boolean validate() {
        if (StringUtils.equals(this.getCode(), "OPR00000")) {
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }

    public boolean ifSuccess() {
        if (null == this) {
            return false;
        }
        return StringUtils.equals("SUCCESS", this.getStatus());
    }

    public boolean ifCanRefund() {
        if (ifSuccess()) {
            return false;
        }
        return StringUtils.isEmpty(uniqueRefundNo);

    }


    @Getter
    @Setter@NoArgsConstructor
    static class BankPromotionInfoDTOList implements Serializable {
        //优惠券编码
        private String promotionId;
        //优惠券名称
        private String promotionName;
        //优惠券退回金额
        private String amountRefund;
        //优惠券活动id
        private String activityId;
        //渠道出资
        private String channelContribute;
        //商户出资
        private String merchantContribute;
        //其他出资
        private String otherContribute;
        //备注信息
        private String memo;
    }



}
