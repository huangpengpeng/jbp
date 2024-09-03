package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.model.user.UserScoreFlow;
import com.jbp.common.request.PasswordRequest;
import com.jbp.common.request.UserScoreRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserScoreFlowService;
import com.jbp.service.service.UserScoreService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/front/user/score")
@Api(tags = "用户分数控制器")
public class UserScoreController {

    @Autowired
    private UserScoreService userScoreService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserScoreFlowService userScoreFlowService;

    @ApiOperation(value = "获取当前分数")
    @RequestMapping(value = "/getUserScore", method = RequestMethod.GET)
    public CommonResult<Integer> getUserScore() {

        UserScore userScore = userScoreService.getOne(new QueryWrapper<UserScore>().lambda().eq(UserScore::getUid, userService.getUserId()));

        return CommonResult.success(userScore == null ? 0 : userScore.getScore());
    }

    @ApiOperation(value = "赠送用户等级")
    @RequestMapping(value = "/updateUserCapa", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated UserScoreRequest request) {

        userScoreService.updateUserCapa(request);

        return CommonResult.success();
    }



    @ApiOperation(value = "获取分数明细")
    @RequestMapping(value = "/getUserScoreFlow", method = RequestMethod.GET)
    public CommonResult<List<UserScoreFlow>> getUserScoreFlow() {

        List<UserScoreFlow> userScoreFlows = userScoreFlowService.list(new QueryWrapper<UserScoreFlow>().lambda().eq(UserScoreFlow::getUid, userService.getUserId()).orderByDesc(UserScoreFlow::getCreateTime));

        return CommonResult.success(userScoreFlows);
    }


}



