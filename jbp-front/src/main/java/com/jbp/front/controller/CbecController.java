package com.jbp.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.request.agent.CbecOrderSyncRequest;
import com.jbp.common.request.agent.CbecScoreEditRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.SignUtil;
import com.jbp.service.service.CbecOrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.service.agent.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/front/cbec")
@Api(tags = "跨境控制器")
@EncryptIgnore
public class CbecController {

    @Resource
    private CbecOrderService cbecOrderService;
    @Resource
    private UserService userService;
    @Resource
    private WalletService walletService;
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private PlatformWalletService platformWalletService;

    @ApiOperation(value = "获取用户信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/getUserInfo ", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult getUserInfo(String appKey, String timeStr, String method, String sign,
                                 String account, String channel) {
        validSign(appKey, timeStr, method, sign);
        if(StringUtils.isEmpty(account)){
            throw new CrmebException("账户不能为空");
        }
        User user = userService.getByAccount(account);
        if(user == null){
            throw new CrmebException("账户不存在");
        }
        JSONObject result = new JSONObject();
        result.put("nickname", user.getNickname());
        result.put("account", account);
        result.put("channel",channel);
        result.put("status",user.getStatus());
        WalletConfig walletConfig = walletConfigService.getCanPay();
        Wallet wallet = walletService.getByUser(user.getId(), walletConfig.getType());
        result.put("usableScore", wallet == null ? BigDecimal.ZERO : wallet.getBalance());
        return CommonResult.success(result);
    }


    @ApiOperation(value = "修改用户积分", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/editScore ", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult editScore(String appKey, String timeStr, String method, String sign,
                                 @RequestBody @Validated  CbecScoreEditRequest request) {
        validSign(appKey, timeStr, method, sign);
        if (request.getAction().intValue() != 0 && request.getAction().intValue() != 1) {
            throw new CrmebException("积分变动方向非法");
        }
        if (StringUtils.isEmpty(request.getPostscript())) {
            throw new CrmebException("附言不能为空");
        }
        BigDecimal score = request.getScore().setScale(2, BigDecimal.ROUND_DOWN);
        if (!ArithmeticUtils.equals(score, request.getScore())) {
            throw new CrmebException("积分金额保留两位小数");
        }
        if (!ArithmeticUtils.less(score, BigDecimal.valueOf(0.01))) {
            throw new CrmebException("最小金额0.01");
        }
        User user = userService.getByAccount(request.getAccount());
        if (user == null) {
            throw new CrmebException("账户不存在");
        }


        WalletConfig walletConfig = walletConfigService.getCanPay();
        WalletFlow flow = null;
        if (request.getAction().intValue() == 0) {
            List<WalletFlow> walletFlows = walletFlowService.getByUser(user.getId(), request.getOrderNo(), WalletFlow.OperateEnum.付款.toString(), WalletFlow.ActionEnum.支出.name());
            if (CollectionUtils.isEmpty(walletFlows)) {
                throw new CrmebException("当前订单已存在减少记录不允许反复减少");
            }
            walletService.transferToPlatform(user.getId(), walletConfig.getType(), request.getScore(), WalletFlow.OperateEnum.付款.toString(), request.getOrderNo(), request.getPostscript());
            walletFlows = walletFlowService.getByUser(user.getId(), request.getOrderNo(), WalletFlow.OperateEnum.付款.toString(), WalletFlow.ActionEnum.支出.name());
            flow = walletFlows.get(0);
        }

        if (request.getAction().intValue() == 1) {
            List<WalletFlow> walletFlows = walletFlowService.getByUser(user.getId(), request.getOrderNo(), WalletFlow.OperateEnum.付款.toString(), WalletFlow.ActionEnum.支出.name());
            if (CollectionUtils.isEmpty(walletFlows)) {
                throw new CrmebException("当前订单未查询到付款记录不允许增加");
            }
            walletFlows = walletFlowService.getByUser(user.getId(), request.getOrderNo(), WalletFlow.OperateEnum.退款.toString(), WalletFlow.ActionEnum.收入.name());
            if (CollectionUtils.isEmpty(walletFlows)) {
                throw new CrmebException("当前订单已存在增加记录不允许再次增加");
            }
            platformWalletService.transferToUser(user.getId(), walletConfig.getType(), request.getScore(), WalletFlow.OperateEnum.退款.toString(), request.getOrderNo(), request.getPostscript());
            walletFlows = walletFlowService.getByUser(user.getId(), request.getOrderNo(), WalletFlow.OperateEnum.退款.toString(), WalletFlow.ActionEnum.收入.name());
            flow = walletFlows.get(0);
        }
        if (flow == null) {
            throw new CrmebException("操作失败");
        }
        JSONObject result = new JSONObject();
        result.put("nickname", user.getNickname());
        result.put("account", request.getAccount());
        result.put("channel", request.getChannel());
        result.put("status", user.getStatus());
        Wallet wallet = walletService.getByUser(user.getId(), walletConfig.getType());
        result.put("usableScore", wallet == null ? BigDecimal.ZERO : wallet.getBalance());
        result.put("uniqueNo", flow.getUniqueNo());
        return CommonResult.success();
    }

    @ApiOperation(value = "跨境订单同步", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/syncOrder ", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult syncOrder(String appKey, String timeStr, String method, String sign,
                                  @RequestBody @Validated CbecOrderSyncRequest request) {
        validSign(appKey, timeStr, method, sign);
        return CommonResult.success(cbecOrderService.save(request));
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
