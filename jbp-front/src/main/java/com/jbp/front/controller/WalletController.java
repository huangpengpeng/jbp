package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.request.WalletChangeRequest;
import com.jbp.common.request.WalletEmbodyRequest;
import com.jbp.common.request.WalletTradePasswordRequest;
import com.jbp.common.request.WalletVirementRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.WalletFlowService;
import com.jbp.service.service.agent.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/front/wallet")
@Api(tags = "用户积分")
public class WalletController {
    @Resource
    private WalletService walletService;
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private UserService userService;
    @Resource
    private WalletConfigService walletConfigService;

    // 1、设置交易密码  【手机号+验证码】->  setPayPwd();
    @GetMapping("/trade/password")
    @ApiOperation("设置交易密码")
    public CommonResult tradePassword(WalletTradePasswordRequest request) {
        userService.tradePassword(request.getPhone(), request.getCode(), request.getTradePassword());
        return CommonResult.success();
    }

    // 2、（配置是否启用 没启用直接报错）获取wallet 信息  当前登录用户+钱包类型
    @GetMapping("/get/wallet")
    @ApiOperation("获取用户积分信息")
    public CommonResult<Wallet> getWallet(Integer type) {
        WalletConfig walletConfig = walletConfigService.getByType(type);
        if (walletConfig.getStatus() == 0) {
            return CommonResult.failed("状态已禁用");
        }
        User info = userService.getInfo();
        return CommonResult.success(walletService.getByUser(info.getId(), type));
    }

    // 3.（配置是否启用 没启用直接报错） 钱包明细 获取walletFlow   倒序   操作类型+方向+关键词：externalNo  or postscript
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

    // 4.提现   验证密码  验证配置
    // 4.1 直接扣用户明细
    // 4.2 增加提现记录【待定】
    @PostMapping("/embody")
    @ApiOperation("用户提现")
    public CommonResult embody(@RequestBody WalletEmbodyRequest request) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getTradePassword(), user.getPhone()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }
        walletService.reduce(user.getId(), request.getType(), request.getAmt(), WalletFlow.OperateEnum.提现.name(), request.getExternalNo(), request.getPostscript());
        return CommonResult.success();
    }

    // 5.兑换  原始积分 转平台   平台在新的积分给用户 自己兑换给自己【type-> type2】 自己减少   目标正价   系数  备注
    @PostMapping("/change")
    @ApiOperation("兑换")
    public CommonResult change(@RequestBody WalletChangeRequest request) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getTradePassword(), user.getPhone()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }

     return CommonResult.success();
    }

    // 6.转账  自己 转 其他人   其他人的账户【自己不能转给自己】、
    // 6.1  自己转平台    平台转其他人
    @PostMapping("/virement")
    @ApiOperation("转账")
    public CommonResult virement(@RequestBody WalletVirementRequest request) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user.getPayPwd())) {
            throw new CrmebException("请设置交易密码");
        }
        if (!CrmebUtil.encryptPassword(request.getTradePassword(), user.getPhone()).equals(user.getPayPwd())) {
            throw new CrmebException("交易密码不正确");
        }
//        转账用户
        User virementUser = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(virementUser)) {
            throw new CrmebException("账号不存在");
        }
        walletService.virement(user.getId(),virementUser.getId(),request.getAmt(),request.getType(),request.getPostscript(), WalletFlow.OperateEnum.转账.name(),request.getExternalNo());
        return CommonResult.success();
    }
}
