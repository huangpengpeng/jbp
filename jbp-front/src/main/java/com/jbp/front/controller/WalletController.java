package com.jbp.front.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/front/wallet")
@Api(tags = "用户积分")
public class WalletController {


    // 1、设置交易密码  【手机号+验证码】->  setPayPwd();


    // 2、（配置是否启用 没启用直接报错）获取wallet 信息  当前登录用户+钱包类型


    // 3.（配置是否启用 没启用直接报错） 钱包明细 获取walletFlow   倒序   操作类型+方向+关键词：externalNo  or postscript

    // 4.提现   验证密码  验证配置
    // 4.1 直接扣用户明细
    // 4.2 增加提现记录【待定】


    // 5.兑换  原始积分 转平台   平台在新的积分给用户 自己兑换给自己【type-> type2】 自己减少   目标正价   系数  备注

    // 6.转账  自己 转 其他人   其他人的账户【自己不能转给自己】、
    // 6.1  自己转平台    平台转其他人




}
