package com.jbp.admin.controller.publicly;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.annotation.CustomResponseAnnotation;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.PayCallbackService;
import com.jbp.service.service.agent.LztAcctApplyService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.service.agent.LztWithdrawalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 * 支付回调
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/publicly/payment/callback")
@Api(tags = "支付回调控制器")
@CustomResponseAnnotation
@EncryptIgnore
public class PayCallbackController {

    @Autowired
    private PayCallbackService callbackService;
    @Autowired
    private LianLianPayService lianLianPayService;
    @Resource
    private LztAcctApplyService lztAcctApplyService;
    @Resource
    private LztTransferMorepyeeService lztTransferMorepyeeService;
    @Resource
    private LztWithdrawalService lztWithdrawalService;

    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public String weChat(@RequestBody String  request) {
        System.out.println("微信支付回调 request ===> " + request);
        String response = callbackService.wechatPayCallback(request);
        System.out.println("微信支付回调 response ===> " + response);
        return response;
    }

    @ApiOperation(value = "支付宝支付回调 ")
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public String aliPay(HttpServletRequest request){
        //支付宝支付回调
        System.out.println("支付宝支付回调 request ===> " + JSON.toJSONString(request.getParameterMap()));
        return callbackService.aliPayCallback(request);
    }

    /**
     * 微信退款回调
     */
    @ApiOperation(value = "微信退款回调")
    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    public String weChatRefund(@RequestBody String request) {
        System.out.println("微信退款回调 request ===> " + request);
        String response = callbackService.weChatRefund(request);
        System.out.println("微信退款回调 response ===> " + response);
        return response;
    }

    @ApiOperation(value = "连连支付回调")
    @RequestMapping(value = "/lianlian", method = RequestMethod.POST)
    public String lianlian(HttpServletRequest request) {
        // 从请求头中获取签名值
        String signature = request.getHeader("Signature-Data");
        BufferedReader reader = null;
        try {
            // 从请求体中获取源串
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            log.info("[接收来自连连下发的异步通知] 签名值为：" + signature);
            log.info("[接收来自连连下发的异步通知] 签名源串为：" + stringBuilder.toString());

            // 进行验签
            if (lianLianPayService.checkSign(stringBuilder.toString(), signature)) {
                // 验签通过，处理系统业务逻辑
                log.info("验签通过！！！");
                callbackService.lianLianPayCallback(JSONObject.toJavaObject(JSONObject.parseObject(stringBuilder.toString()), QueryPaymentResult.class));
                // 返回Success，响应本次异步通知已经成功
                return "Success";
            } else {
                // 验签失败，进行预警。
                log.error("验签失败！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        // 没有其他意义，异步通知响应连连这边只认"Success"，返回非"Success"，连连会进行重发
        return "error";
    }



    @ApiOperation(value = "来账通回调")
    @RequestMapping(value = "/lianlian/lzt/{txnSeqno}")
    public String lzt(@PathVariable("txnSeqno") String txnSeqno) {
        if (txnSeqno.startsWith(LianLianPayConfig.TxnSeqnoPrefix.来账通开通银行虚拟户.getPrefix())) {
            LztAcctApply lztAcctApply = lztAcctApplyService.getByTxnSeqno(txnSeqno);
            if (lztAcctApply != null) {
                lztAcctApplyService.refresh(lztAcctApply.getLianLianAcct());
            }
        }
        if (txnSeqno.startsWith(LianLianPayConfig.TxnSeqnoPrefix.来账通内部代发.getPrefix())) {
            LztTransferMorepyee lztTransferMorepyee = lztTransferMorepyeeService.getByTxnSeqno(txnSeqno);
            if (lztTransferMorepyee != null) {
                lztTransferMorepyeeService.refresh(lztTransferMorepyee.getAccpTxno());
            }
        }
        if (txnSeqno.startsWith(LianLianPayConfig.TxnSeqnoPrefix.来账通提现.getPrefix())) {
            LztWithdrawal lztWithdrawal = lztWithdrawalService.getByTxnSeqno(txnSeqno);
            if (lztWithdrawal != null) {
                lztWithdrawalService.refresh(lztWithdrawal.getAccpTxno());
            }
        }
        return "SUCCESS";
    }
}



