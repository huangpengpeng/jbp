package com.jbp.front.controller;


import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.service.UserService;

import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.RelationScoreService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.service.agent.UserRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户 -- 用户中心
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
@RequestMapping("api/front/user")
@Api(tags = "用户控制器")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRelationService relationService;
    @Autowired
    private UserInvitationService invitationService;
    @Autowired
    private CapaService capaService;
    @Autowired
    private RelationScoreService relationScoreService;

    @ApiOperation(value = "登录密码修改")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request) {
        return CommonResult.success(userService.password(request));
    }

    @ApiOperation(value = "修改个人信息")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<String> personInfo(@RequestBody @Validated UserEditInfoRequest request) {
        if (userService.editUser(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserInfoResponse> getUserCenter() {
        UserInfoResponse userInfo = userService.getUserInfo();
        return CommonResult.success(userInfo);
    }

    @ApiOperation(value = "获取用户手机号验证码")
    @RequestMapping(value = "/phone/code", method = RequestMethod.POST)
    public CommonResult<String> getCurrentPhoneCode() {
        if (userService.getCurrentPhoneCode()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "换绑手机号老手机校验验证码 新手机发送验证码")
    @RequestMapping(value = "/update/binding/phone/code", method = RequestMethod.POST)
    public CommonResult<String> updatePhoneCode(@RequestBody @Validated UserBindingPhoneUpdateRequest request) {
        if (userService.updatePhoneCode(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "换绑手机号  新手号+新手机号验证码校验")
    @RequestMapping(value = "/update/binding", method = RequestMethod.POST)
    public CommonResult<String> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request) {
        if (userService.updatePhone(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户注销数据前置")
    @RequestMapping(value = "/logoff/before", method = RequestMethod.GET)
    public CommonResult<UserLogoffBeforeResponse> logoffBefore() {
        return CommonResult.success(userService.logoffBefore());
    }

    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logoff", method = RequestMethod.POST)
    public CommonResult<String> logoff() {
        if (userService.logoff()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    @ApiOperation(value = "获取默认节点")
    @RequestMapping(value = "/relation/default/get", method = RequestMethod.GET)
    public CommonResult<UserRelationInfoResponse> nodeGet(String pAccount) {
        if (StringUtils.isBlank(pAccount)) {
            throw new RuntimeException("账户为空");
        }
        User user = userService.getByAccount(pAccount);
        if (user == null) {
            throw new RuntimeException("账户错误");
        }
        // 邀请过其他人返回null
        List<UserInvitation> nextList = invitationService.getNextList(user.getId());
        if (CollectionUtils.isNotEmpty(nextList)) {
            return CommonResult.success();
        }
        UserRelation userRelation = relationService.getLeftMost(user.getId());
        if (userRelation == null) {
            return CommonResult.success();
        }
        user = userService.getById(userRelation.getPId());
        UserRelationInfoResponse response = new UserRelationInfoResponse(userRelation.getPId(), user.getAccount(), userRelation.getNode());
        return CommonResult.success(response);
    }

    @ApiOperation(value = "获取滑落节点")
    @RequestMapping(value = "/relation/slip/get", method = RequestMethod.GET)
    public CommonResult<UserRelationInfoResponse> slipGet(String pAccount, Integer node) {
        if (StringUtils.isBlank(pAccount)) {
            throw new RuntimeException("账户为空");
        }
        User user = userService.getByAccount(pAccount);
        if (user == null) {
            throw new RuntimeException("账户错误");
        }
        if(node == null || node > 1 || node < 0){
            throw new RuntimeException("节点错误");
        }
        // 当前安置节点没有被占用不返回滑落
        UserRelation userRelation = relationService.getByPid(user.getId(), node);
        if (!invitationService.getNextList(user.getId()).isEmpty()) {
            if (Objects.isNull(userRelation)) {
                return CommonResult.success();
            }
        }
   
        userRelation = relationService.getLeftMost(user.getId());
        if (userRelation == null) {
            return CommonResult.success();
        }
        user = userService.getById(userRelation.getPId());
        UserRelationInfoResponse response = new UserRelationInfoResponse(userRelation.getPId(), user.getAccount(), userRelation.getNode());
        return CommonResult.success(response);
    }

    @ApiOperation(value = "注册校验")
    @RequestMapping(value = "/register/valid", method = RequestMethod.POST)
    public CommonResult<Boolean> valid(@RequestBody @Validated UserHelpRegisterRequest request) {
        User pUser = userService.getByAccount(request.getPaccount());
        if (pUser == null) {
            throw new CrmebException("邀请账号错误");
        }
        Integer pId = pUser.getId();
        User rUser = userService.getByAccount(request.getRaccount());
        if (rUser == null) {
            throw new CrmebException("服务账号错误");
        }
        Integer rId = rUser.getId();
        // 检查操作一张网
        User loginUser = userService.getInfo();
        if (loginUser.getId() != pId && !invitationService.hasChild(pId, loginUser.getId())) {
            throw new CrmebException("只能帮自己或者下级注册");
        }
        if (loginUser.getId() != rId && !invitationService.hasChild(rId, loginUser.getId())) {
            throw new CrmebException("只能帮自己或者下级安置");
        }
        if (relationService.getByPid(rId, request.getNode()) != null) {
            throw new CrmebException("节点已被占用");
        }
        // 安置默认节点
        if (invitationService.getNextList(rId).isEmpty()) {
            UserRelation defaultRelation = relationService.getLeftMost(rId);
            if (defaultRelation.getPId() != rId) {
                throw new CrmebException("当前用户只能注册默认安置人");
            }
        }
        return CommonResult.success(true);
    }

    @ApiOperation(value = "上级帮忙注册")
    @RequestMapping(value = "/help/register", method = RequestMethod.POST)
    public CommonResult<User> register(@RequestBody @Validated UserHelpRegisterRequest request) {
        HelpRegisterResponse response = userService.helpRegisterValid(request.getUsername(), request.getPhone(), request.getPaccount(),
                request.getRaccount(), request.getNode(), capaService.getMinCapa().getId());
        User user = userService.helpRegister(request.getUsername(), request.getPhone(), response.getPId(), response.getRId(), request.getNode());
        return CommonResult.success(user);
    }




    @ApiOperation(value = "获取用户的邀请关系")
    @RequestMapping(value = "/getInvite", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserInviteResponse>> getInvite(@ModelAttribute @Validated UserInviteRequest request) {
        return CommonResult.success(CommonPage.restPage(userService.getUserInvite(request)));
    }


    @ApiOperation(value = "校验交易密码")
    @RequestMapping(value = "/verifyPayPwd", method = RequestMethod.GET)
    public CommonResult<Boolean> verifyPayPwd(String payPwd) throws Exception {


        return CommonResult.success(userService.verifyPayPwd(payPwd));
    }


    @ApiOperation(value = "获取用户的邀请关系详细信息")
    @RequestMapping(value = "/getInviteInfo", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserInviteInfoResponse>> getInviteInfo(@ModelAttribute @Validated UserInviteRequest request) {
        return CommonResult.success(CommonPage.restPage(userService.getUserInviteInfo(request)));
    }

    @ApiOperation(value = "用户左右区业绩")
    @RequestMapping(value = "/getPerformance", method = RequestMethod.GET)
    public CommonResult<RelationScoreResponse> getPerformance() {

        return CommonResult.success(relationScoreService.getUserResult());
    }



    @ApiOperation(value = "账号获取用户信息")
    @RequestMapping(value = "/getAccountUser", method = RequestMethod.GET)
    public CommonResult<UserInviteResponse> getAccountUser(String account) {
        User user =  userService.getByAccount(account);
        if(user == null){
            throw new CrmebException("账号不存在");
        }
        UserInviteResponse userInviteResponse =new UserInviteResponse();
        BeanUtils.copyProperties(user , userInviteResponse);
        return CommonResult.success(userInviteResponse);
    }

}



