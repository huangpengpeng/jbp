package com.jbp.front.controller.pay;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.annotation.CustomResponseAnnotation;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCashier;
import com.jbp.common.request.pay.PayCashRequest;
import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SignUtil;
import com.jbp.service.service.pay.PayCashierMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@EncryptIgnore
@CustomResponseAnnotation
@RequestMapping("api/front/publicly/pay")
@Api(tags = "前台支付控制器")
public class PayAct {

    @Resource
    private PayCashierMng payCashierMng;
    @Resource
    private PayUnifiedOrderMng payUnifiedOrderMng;

    public static void main(String[] args) {
        PayCashRequest request = new PayCashRequest();
        request.setAppKey("js20241016");
        request.setTimeStr("20241016144910");
        request.setMethod("cashier");

        Map<String, Object> map = new HashMap<>();
        map.put("appKey", request.getAppKey());
        map.put("timeStr", request.getTimeStr());
        map.put("method", request.getMethod());
        String tagSign = SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map);
        request.setSign(tagSign);
        request.setTxnSeqno("PAY_20241016144910");
        request.setPayAmt(BigDecimal.valueOf(0.1));
        List<PayOrderInfoDto> orderInfo = Lists.newArrayList();
        PayOrderInfoDto dto = new PayOrderInfoDto("商品名称", 1, BigDecimal.valueOf(0.1));
        orderInfo.add(dto);
        request.setOrderInfo(orderInfo);
        request.setCreateTime("2024-10-16 14:54:55");
        request.setExpireTime("2024-10-19 14:54:55");
        request.setNotifyUrl("http://127.0.0.1:8383/api/front/pay/call/PAY_20241016144910");
        request.setReturnUrl("http://127.0.0.1:8383/api/front/pay/call/PAY_20241016144910");
        request.setUserNo("123");
        request.setIp("127.0.0.1");
        System.out.println(JSONObject.toJSONString(request));

    }

    @ApiOperation(value = "唤起收银台[不需要登录]")
    @RequestMapping(value = "/cashier", method = RequestMethod.POST)
    public CommonResult<String> cash(@RequestBody @Validated PayCashRequest request ) {
        validSign(request.getAppKey(), request.getTimeStr(), request.getMethod(), request.getSign());
        Date createTime = DateTimeUtils.parseDate(request.getCreateTime());
        Date expireTime = DateTimeUtils.parseDate(request.getExpireTime());
        if (expireTime.before(createTime)) {
            throw new CrmebException("过期时间不能大于创单时间");
        }
        PayCashier save = payCashierMng.save( request) ;
        return CommonResult.success(save.getToken());
    }

    @ApiOperation(value = "获取支付方法")
    @RequestMapping(value = "/methodGet", method = RequestMethod.GET)
    public CommonResult<JSONObject> methodGet(String token) {
        return CommonResult.success(payCashierMng.getPayMethod(token));
    }

    @ApiOperation(value = "支付下单")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public CommonResult<PayCreateResponse> create(String token, String method) {
        PayCreateResponse result = payUnifiedOrderMng.create(token, method);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "支付查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public CommonResult<PayQueryResponse> query(@RequestBody @Validated PayQueryRequest request) {
        validSign(request.getAppKey(), request.getTimeStr(), request.getMethod(), request.getSign());
        return CommonResult.success(payUnifiedOrderMng.query(request.getAppKey(), request.getTxnSeqno()));
    }

    @ApiOperation(value = "支付回调")
    @RequestMapping(value = "/call/{appKey}/{txnSeqno}", method = RequestMethod.POST)
    public String callBack(@PathVariable("appKey") String appKey, @PathVariable("txnSeqno") String txnSeqno) {
        payUnifiedOrderMng.callBack(appKey, txnSeqno);
        return "SUCCESS";
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
