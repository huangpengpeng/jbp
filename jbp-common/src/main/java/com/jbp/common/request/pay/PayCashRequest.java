package com.jbp.common.request.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.mybatis.PayOrderInfoListHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PayCashRequest implements Serializable {

    @NotEmpty(message = "商户秘钥不能为空")
    private String appKey;

    @NotEmpty(message = "当前时间戳不能为空")
    private String timeStr;

    @NotEmpty(message = "调用方法不能为空")
    private String method;

    @NotEmpty(message = "支付签名不能为空")
    private String sign;

    @NotEmpty(message = "交易单号不能为空")
    private String txnSeqno;

    @NotEmpty(message = "用户编号")
    private String userNo;

    @NotNull(message = "交易金额不能为空")
    private BigDecimal payAmt;

    private List<PayOrderInfoDto> orderInfo;

    private String ext;

    @NotEmpty(message = "创单时间不能为空 yyyy-MM-dd hh:mm:ss")
    private String createTime;

    @NotEmpty(message = "交易过期不能为空 yyyy-MM-dd hh:mm:ss")
    private String expireTime;

    @NotEmpty(message = "交易通知地址不能为空")
    private String notifyUrl;

    @NotEmpty(message = "支付成功跳转地址不能为空")
    private String returnUrl;

    @NotEmpty(message = "客户ip地址不能为空")
    private String ip;

}
