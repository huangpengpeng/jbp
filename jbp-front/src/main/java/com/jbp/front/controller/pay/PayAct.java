package com.jbp.front.controller.pay;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCash;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.request.merchant.MerchantSettledApplyRequest;
import com.jbp.common.request.pay.PayCashRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SignUtil;
import com.jbp.service.service.pay.PayCashMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/front/pay")
@Api(tags = "前台支付控制器")
public class PayAct {

    @Resource
    private PayCashMng payCashMng;
    @Resource
    private PayUnifiedOrderMng payUnifiedOrderMng;

    @ApiOperation(value = "唤起收银台[不需要登录]")
    @RequestMapping(value = "/cash", method = RequestMethod.POST)
    public CommonResult<String> cash(@RequestBody @Validated PayCashRequest request ) {
        validSign(request.getAppKey(), request.getTimeStr(), request.getMethod(), request.getSign());
        Date createTime = DateTimeUtils.parseDate(request.getCreateTime());
        Date expireTime = DateTimeUtils.parseDate(request.getExpireTime());
        if (expireTime.before(createTime)) {
            throw new CrmebException("过期时间不能大于创单时间");
        }
        PayCash save = payCashMng.save(request.getAppKey(), request.getTxnSeqno(), request.getPayAmt(),
                request.getOrderInfo(), request.getExt(), DateTimeUtils.parseDate(request.getCreateTime()),
                DateTimeUtils.parseDate(request.getExpireTime()));
        return CommonResult.success(save.getToken());
    }

    @ApiOperation(value = "获取支付方法")
    @RequestMapping(value = "/methodGet", method = RequestMethod.GET)
    public CommonResult<List<String>> methodGet(String token) {
        return CommonResult.success(payCashMng.getPayMethod(token));
    }

    @ApiOperation(value = "支付下单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<PayCreateResponse> create(String token, String method) {
        PayCreateResponse result = payUnifiedOrderMng.create(token, method);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "支付查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public CommonResult<String> query(String token, String method) {
        return CommonResult.success();
    }

    @ApiOperation(value = "支付回调")
    @RequestMapping(value = "/call", method = RequestMethod.POST)
    public CommonResult<String> callBack(String token, String method) {
        return CommonResult.success();
    }


    @ApiOperation(value = "支付退款")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public CommonResult<String> refund(String token, String method) {
        return CommonResult.success();
    }

    @ApiOperation(value = "支付退款查询")
    @RequestMapping(value = "/refundQuery", method = RequestMethod.POST)
    public CommonResult<String> refundQuery(String token, String method) {
        return CommonResult.success();
    }


    private void validSign(String appKey, String timeStr, String method, String sign) {
        if (StringUtils.isAnyBlank(appKey, timeStr, method, sign)) {
            throw new RuntimeException("签名参数错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("appKey", appKey);
        map.put("timeStr", timeStr);
        map.put("method", method);
        String tagSign = SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map);
        if (!tagSign.equals(sign)) {
            throw new RuntimeException("签名错误");
        }
    }

}
