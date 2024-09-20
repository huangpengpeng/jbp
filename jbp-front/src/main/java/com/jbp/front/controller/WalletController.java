package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.request.agent.ChannelIdentityRequest;
import com.jbp.common.request.agent.WalletFlowListRequest;
import com.jbp.common.request.agent.WalletRequest;
import com.jbp.common.response.UserWalletInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.front.service.LoginService;
import com.jbp.service.product.comm.CommAliasNameSmEnum;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
    @RequestMapping("api/front/wallet")
@Api(tags = "用户积分")
public class WalletController {

    @Resource
    private LoginService loginService;
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
    @Resource
    private Environment environment;

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
        String commissionScale = systemConfigService.getValueByKey("wallet_withdraw_commission");
        json.put("wallet_withdraw_commission", commissionScale);
        return CommonResult.success(json);
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "设置交易密码")
    @GetMapping("/trade/password")
    @ApiOperation("设置交易密码")
    public CommonResult tradePassword(WalletTradePasswordRequest request) {
        // 校验密码是否是6个连续的数字
        String regex1 = "^(012345|123456|234567|345678|456789|567890|654321|543210|432109|321098|210987|109876)$";
        // 校验密码是否是6个相同的数字
        String regex2 = "^(\\d)\\1{5}$";

        if (request.getTradePassword().matches(regex1)) {
            throw new RuntimeException("交易密码过于简单,请重新设置");
        }
        if (request.getTradePassword().matches(regex2)) {
            throw new RuntimeException("交易密码过于简单,请重新设置");
        }
        userService.tradePassword(request.getCode(), request.getTradePassword());
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
        Wallet wallet= walletService.getByUser(info.getId(), type);

        if(wallet == null){
            return CommonResult.success();
        }
        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));
        wallet.setBalance(wallet.getBalance().multiply(wallet_pay_integral));
        return CommonResult.success(wallet);
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

    @GetMapping("/flowpage")
    @ApiOperation("用户积分列表")
    public CommonResult<CommonPage<WalletFlow>> getList(WalletFlowListRequest request) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(request.getLimit());
        pageParamRequest.setPage(request.getPage());

        PageInfo<WalletFlow> pageInfo = walletFlowService.pageWalletList(userService.getUserId(), request.getType(), request.getAction(), pageParamRequest);

       List<WalletFlow> walletFlow =   pageInfo.getList();
        String name = environment.getProperty("spring.profiles.active");
        if(name.contains("sm") || name.contains("yk")  || name.contains("tf") ){
            walletFlow.forEach(e->{
                e.setPostscript(CommAliasNameSmEnum.getAliasNameReplaceName(e.getPostscript()));
            });
        }
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }


    @PostMapping("/withdraw")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "用户提现")
    @ApiOperation("用户提现")
    public CommonResult<WalletWithdraw> withdraw(@RequestBody @Validated WalletWithdrawRequest request) {
        User user = userService.getInfo();
        if (!user.getStatus()) {
            throw new CrmebException("账户不可用");
        }
        userService.validPayPwd(user.getId(), request.getPwd());
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = StringUtils.isEmpty(channelName) ? "平台" : channelName;
        ChannelCard channelCard = channelCardService.getByUser(user.getId(), channelName);
        if(channelCard == null){
            throw new CrmebException("未绑定银行卡，无法提现");
        }
        WalletConfig walletConfig = walletConfigService.getByType(request.getWalletType());
        if (!walletConfig.getCanWithdraw()) {
            throw new CrmebException("类型积分不可提现");
        }

        BigDecimal wallet_pay_minimum_mat = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_MINIMUM_MAT));
        if(request.getAmt().compareTo(wallet_pay_minimum_mat) == -1){
            throw new CrmebException("提现金额需要大于"+ wallet_pay_minimum_mat+"元");
        }

        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));
        WalletWithdraw walletWithdraw = walletWithdrawService.create(user.getId(), user.getAccount(), walletConfig.getType(),
                walletConfig.getName(), request.getAmt().divide(wallet_pay_integral), request.getPostscript());
        return CommonResult.success(walletWithdraw);
    }

    @PostMapping("/change/score")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "兑换积分")
    @ApiOperation("兑换积分获取")
    public CommonResult<JSONObject> changeScore(Integer walletType, BigDecimal amt) {
        User user = userService.getInfo();
        if (!user.getStatus()) {
            throw new CrmebException("账户不可用");
        }
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
        if (!user.getStatus()) {
            throw new CrmebException("账户不可用");
        }
        userService.validPayPwd(user.getId(), request.getPwd());
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
        if (!user.getStatus()) {
            throw new CrmebException("账户不可用");
        }
        String walletPayOpenPassword = systemConfigService.getValueByKey(SysConfigConstants.IPHON_CODE_CARD);
        Boolean ifOpenPwd = Constants.CONFIG_FORM_SWITCH_OPEN.equals(walletPayOpenPassword);
        if (userService.ifOpenSecurityPhone()) {
            if (StringUtils.isBlank(request.getCode())) {
                throw new CrmebException("验证码不能为空");
            }
            if (StringUtils.isBlank(user.getSecurityPhone())) {
                throw new CrmebException("请先设置安全手机号");
            }
            loginService.checkValidateCode(user.getSecurityPhone(), request.getCode());
        }
        if(walletConfigService.hasPwd()){
            userService.validPayPwd(user.getId(), request.getPwd());
        }else if(ifOpenPwd){
            userService.checkValidateCode(user.getPhone(), request.getPwd());
        }

        WalletConfig walletConfig = walletConfigService.getByType(request.getType());
        if (!walletConfig.getCanTransfer()) {
            throw new CrmebException("类型积分不可转账");
        }
        // 转账用户 账号手机号兼容
        List<User> phoneList  = userService.getByPhone(request.getAccount());
        if(phoneList.size()>1){
            throw new CrmebException("手机号重复，请输入账号");
        }
        User receiveUser ;
        if(!phoneList.isEmpty()){
            receiveUser = phoneList.get(0);
        }else{
            receiveUser = userService.getByAccount(request.getAccount());
        }

        if (ObjectUtil.isNull(receiveUser)) {
            throw new CrmebException("账号/手机号不存在");
        }

        String verifyTransferAcme = systemConfigService.getValueByKey(SysConfigConstants.VERIFY_TRANSFER_ACME);
        Boolean ifAcme = Constants.CONFIG_FORM_SWITCH_OPEN.equals(verifyTransferAcme);
        //验证转账顶点账号
        if(ifAcme){
            userService.checkAccountTeamCode(user.getId(),receiveUser.getId());
        }

        //跨团队互转
        String verifyTeamTransferAcme = systemConfigService.getValueByKey(SysConfigConstants.VERIFY_TEAM_TRANSFER_ACME);
        Boolean ifTeamAcme = Constants.CONFIG_FORM_SWITCH_OPEN.equals(verifyTeamTransferAcme);

        if(ifTeamAcme){
            userService.checkTeamAccountTeamCode(user.getId(),receiveUser.getId());
        }

        BigDecimal wallet_pay_integral = new BigDecimal(systemConfigService.getValueByKey(SysConfigConstants.WALLET_PAY_INTEGRAl));
        walletService.transfer(user.getId(), receiveUser.getId(), request.getAmt().divide(wallet_pay_integral), request.getType(), request.getPostscript());
        return CommonResult.success();
}


    @ApiOperation(value = "钱包配置余额明细")
    @RequestMapping(value = "/walletConfigList", method = RequestMethod.GET)
    public CommonResult<List<UserWalletInfoResponse>> walletConfigList() {
        return CommonResult.success(walletConfigService.getUserWalletInfo());

    }


}
