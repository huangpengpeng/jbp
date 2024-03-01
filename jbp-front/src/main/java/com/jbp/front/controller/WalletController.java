package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.model.user.User;
import com.jbp.common.request.WalletChangeRequest;
import com.jbp.common.request.WalletTradePasswordRequest;
import com.jbp.common.request.WalletTransferRequest;
import com.jbp.common.request.WalletWithdrawRequest;
import com.jbp.common.request.agent.ChannelIdentityRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
    @RequestMapping("api/front/wallet")
@Api(tags = "用户积分")
public class WalletController {
    @Resource
    private ChannelCardService channelCardService;
    @Resource
    private ChannelIdentityService channelIdentityService;
    @Resource
    private WalletService walletService;
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private UserService userService;
    @Resource
    private WalletConfigService walletConfigService;
    @Resource
    private WalletWithdrawService walletWithdrawService;
    @Resource
    private SystemConfigService systemConfigService;


    @PostMapping("/identity")
    @ApiOperation("认证")
    public CommonResult identity(@Validated @RequestBody ChannelIdentityRequest request) {
        User info = userService.getInfo();
        channelIdentityService.identity(info.getId(), request);
        return CommonResult.success();
    }

    @PostMapping("/identity/get")
    @ApiOperation("认证")
    public CommonResult<JSONObject> identityGet() {
        User info = userService.getInfo();
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = StringUtils.isEmpty(channelName) ? "平台" : channelName;
        JSONObject json = new JSONObject();
        json.put("card", channelCardService.getByUser(info.getId(), channelName));
        json.put("identity", channelIdentityService.getByUser(info.getId(), channelName));
        return CommonResult.success(json);
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "设置交易密码")
    @GetMapping("/trade/password")
    @ApiOperation("设置交易密码")
    public CommonResult tradePassword(WalletTradePasswordRequest request) {
        userService.tradePassword(request.getPhone(), request.getCode(), request.getTradePassword());
        return CommonResult.success();
    }

    @GetMapping("/get")
    @ApiOperation("获取用户积分信息")
    public CommonResult<Wallet> getWallet(Integer type) {
        WalletConfig walletConfig = walletConfigService.getByType(type);
        if (walletConfig.getStatus() == 0) {
            return CommonResult.failed("状态已禁用");
        }
        User info = userService.getInfo();
        return CommonResult.success(walletService.getByUser(info.getId(), type));
    }


    @GetMapping("/details")
    @ApiOperation("账单明细")
    public CommonResult<List<WalletFlow>> details(String action, Integer type) {
        WalletConfig walletConfig = walletConfigService.getByType(type);
        if (walletConfig.getStatus() == 0) {
            return CommonResult.failed("状态已禁用");
        }
        User user = userService.getInfo();
        return CommonResult.success(walletFlowService.details(user.getId(), action));

    }

    @PostMapping("/withdraw")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "用户提现")
    @ApiOperation("用户提现")
    public CommonResult<WalletWithdraw> withdraw(@RequestBody @Validated WalletWithdrawRequest request) {
        User user = userService.getInfo();
        if (StringUtils.isEmpty(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getPwd(), user.getAccount()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }
        WalletConfig walletConfig = walletConfigService.getByType(request.getWalletType());
        if (!walletConfig.getCanWithdraw()) {
            throw new CrmebException("类型积分不可提现");
        }
        WalletWithdraw walletWithdraw = walletWithdrawService.create(user.getId(), user.getAccount(), walletConfig.getType(),
                walletConfig.getName(), request.getAmt(), request.getPostscript());
        return CommonResult.success(walletWithdraw);
    }

    @PostMapping("/change/score")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "兑换积分")
    @ApiOperation("兑换积分获取")
    public CommonResult<JSONObject> changeScore(Integer walletType, BigDecimal amt) {
        WalletConfig walletConfig = walletConfigService.getByType(walletType);
        if (walletConfig == null || amt == null) {
            throw new CrmebException("积分信息不存在");
        }
        if (walletConfig.getChangeType() == null || walletConfig.getChangeScale() == null
                || ArithmeticUtils.lessEquals(walletConfig.getChangeScale(), BigDecimal.ZERO)) {
            throw new CrmebException("兑换信息未配置, 请联系管理员");
        }
        JSONObject json = new JSONObject();
        BigDecimal bigDecimal = walletConfig.getChangeScale().multiply(amt).setScale(2, BigDecimal.ROUND_DOWN);
        json.put("changeScore", bigDecimal);
        json.put("walletName", walletConfig.getName());
        return CommonResult.success(json);
    }

    @PostMapping("/change")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "兑换积分")
    @ApiOperation("兑换")
    public CommonResult change(@RequestBody @Validated WalletChangeRequest request) {
        User user = userService.getInfo();
        if (StringUtils.isEmpty(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getPwd(), user.getAccount()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }
        WalletConfig walletConfig = walletConfigService.getByType(request.getWalletType());
        if (walletConfig == null) {
            throw new CrmebException("积分信息不存在");
        }
        if (walletConfig.getChangeType() == null || walletConfig.getChangeScale() == null
                || ArithmeticUtils.lessEquals(walletConfig.getChangeScale(), BigDecimal.ZERO)) {
            throw new CrmebException("兑换信息未配置, 请联系管理员");
        }
        walletService.change(user.getId(), request.getAmt(), request.getWalletType(), walletConfig.getChangeType(), request.getPostscript());
        return CommonResult.success();
    }


    @PostMapping("/transfer")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "用户积分转账")
    @ApiOperation("转账")
    public CommonResult transfer(@RequestBody @Validated WalletTransferRequest request) {
        User user = userService.getInfo();
        if (StringUtils.isEmpty(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getPwd(), user.getAccount()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }
        WalletConfig walletConfig = walletConfigService.getByType(request.getType());
        if (!walletConfig.getCanTransfer()) {
            throw new CrmebException("类型积分不可转账");
        }
        // 转账用户
        User receiveUser = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(receiveUser)) {
            throw new CrmebException("账号不存在");
        }
        walletService.transfer(user.getId(), receiveUser.getId(), request.getAmt(), request.getType(), request.getPostscript());
        return CommonResult.success();
    }
}
