package com.jbp.front.controller;

import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Slf4j
@RestController
@RequestMapping("api/front/self/score")
@Api(tags = "个人业绩汇总")
public class SelfScoreController {
    @Resource
    private SelfScoreService selfScoreService;
    @Resource
    private UserService userService;


    @ApiOperation("个人团队业绩")
    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public CommonResult<BigDecimal> getTeamList() {




        Integer uid = userService.getUserId();
        return CommonResult.success(selfScoreService.getUserNext(uid ,true));
    }



    @ApiOperation("个人当月团队业绩")
    @RequestMapping(value = "/teamMonth", method = RequestMethod.GET)
    public CommonResult<BigDecimal> getMonthTeamList() {
        Integer uid = userService.getUserId();
        return CommonResult.success(selfScoreService.getUserMonthNext(uid ,true));
    }

}
