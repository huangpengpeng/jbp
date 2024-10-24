package com.jbp.front.controller.pay;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.annotation.CustomResponseAnnotation;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCashier;
import com.jbp.common.request.pay.*;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SignUtil;
import com.jbp.common.vo.WeChatOauthToken;
import com.jbp.common.yop.YopApi;
import com.jbp.common.yop.dto.WechatConfigAddResponse;
import com.jbp.common.yop.dto.WechatConfigQueryResponse;
import com.jbp.common.yop.params.WechatConfigAddRequest;
import com.jbp.common.yop.params.WechatConfigQueryRequest;
import com.jbp.service.service.WechatService;
import com.jbp.service.service.pay.PayCashierMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import com.jbp.service.service.pay.PayUnifiedRefundOrderMng;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UrlPathHelper;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
    private PayUnifiedRefundOrderMng payUnifiedRefundOrderMng;
    @Resource
    private PayUnifiedOrderMng payUnifiedOrderMng;
    @Resource
    private YopApi yopApi;
    @Autowired
    private WechatService wechatService;



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
        request.setTxnSeqno("PAY_20241016144966");
        request.setPayAmt(BigDecimal.valueOf(0.1));
        List<PayOrderInfoDto> orderInfo = Lists.newArrayList();
        PayOrderInfoDto dto = new PayOrderInfoDto("商品名称", 1, BigDecimal.valueOf(0.1));
        orderInfo.add(dto);
        request.setOrderInfo(orderInfo);
        request.setCreateTime("2024-10-16 14:54:55");
        request.setExpireTime("2024-10-19 14:54:55");
        request.setNotifyUrl("http://127.0.0.1:8383/api/front/pay/call/PAY_20241016144966");
        request.setReturnUrl("http://127.0.0.1:8383/api/front/pay/call/PAY_20241016144966");
        request.setUserNo("12113");
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
    public CommonResult<PayCreateResponse> create(String token, String method,String openId) {
        PayCreateResponse result = payUnifiedOrderMng.create(token, method,openId);
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
    public CommonResult<PayRefundResponse> refund(@RequestBody @Validated PayRefundRequest request) {
        validSign(request.getAppKey(), request.getTimeStr(), request.getMethod(), request.getSign());
        PayRefundResponse refund = payUnifiedRefundOrderMng.refund(request);
        return CommonResult.success(refund);
    }

    @ApiOperation(value = "支付退款查询")
    @RequestMapping(value = "/refundQuery", method = RequestMethod.POST)
    public CommonResult<PayRefundResponse> refundQuery(@RequestBody @Validated PayRefundQueryRequest request) {
        validSign(request.getAppKey(), request.getTimeStr(), request.getMethod(), request.getSign());
        PayRefundResponse refund = payUnifiedRefundOrderMng.refundQuery(request);
        return CommonResult.success(refund);
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


    @ApiOperation(value = "微信公众号appid报备")
    @RequestMapping(value = "/wechatReport", method = RequestMethod.GET)
        public CommonResult<WechatConfigAddResponse> create(String merchantNo) {
        WechatConfigAddRequest request = new WechatConfigAddRequest(merchantNo);
        WechatConfigAddResponse wechatConfigAddResponse = yopApi.wechatConfigAdd(request);
        return CommonResult.success(wechatConfigAddResponse);
    }




    @ApiOperation(value = "微信公众号appid报备查询")
    @RequestMapping(value = "/wechatReportQuery", method = RequestMethod.GET)
        public CommonResult<WechatConfigQueryResponse> wechatReportQuery(String merchantNo) {

        WechatConfigQueryRequest request2 = new WechatConfigQueryRequest(merchantNo, "OFFICIAL_ACCOUNT");
        WechatConfigQueryResponse wechatConfigQueryResponse2 = yopApi.wechatConfigQuery(request2);
        return CommonResult.success(wechatConfigQueryResponse2);
    }


    @ApiOperation(value = "公众号获取用户openid")
    @RequestMapping(value = "/get/openId", method = RequestMethod.GET)
    public CommonResult<String> getOpenId(@RequestParam String code) {
        WeChatOauthToken oauthToken = wechatService.getOauth2AccessToken(code);
        return CommonResult.success(oauthToken.getOpenId());
    }


}
