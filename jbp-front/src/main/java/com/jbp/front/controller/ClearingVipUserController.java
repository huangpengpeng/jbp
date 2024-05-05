package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingBonusService;
import com.jbp.service.service.agent.ClearingVipUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 文章控制器
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
@RequestMapping("api/front/clearingVipUser")
@Api(tags = "结算vip用户")
public class ClearingVipUserController {

    @Autowired
    private ClearingBonusService clearingBonusService;
    @Autowired
    private ClearingVipUserService clearingVipUserService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "收入汇总")
    @RequestMapping(value = "/getTotal", method = RequestMethod.GET)
    public CommonResult<Map<String,Object>> getTotal() {
        Map<String,Object> map =new HashMap<>();
        ClearingBonus clearingBonus =  clearingBonusService.getOne(new QueryWrapper<ClearingBonus>().select(" sum(commAmt) as commAmt ").lambda().eq(ClearingBonus ::getUid,userService.getUserId()));

      List<ClearingVipUser> list =  clearingVipUserService.list(new QueryWrapper<ClearingVipUser>().lambda().eq(ClearingVipUser::getUid,userService.getUserId()).orderByAsc(ClearingVipUser::getLevel));
        map.put("total",clearingBonus);
        map.put("income",list);
        return CommonResult.success(map);
    }


    @ApiOperation(value = "收入每日汇总")
    @RequestMapping(value = "/getTotalDay", method = RequestMethod.GET)
    public CommonPage<ClearingBonusListResponse> getTotalDay(PageParamRequest pageParamRequest) {
        PageInfo<ClearingBonusListResponse> listResponses = clearingBonusService.getcleringList(userService.getUserId(),pageParamRequest);
        return CommonPage.restPage(listResponses);
    }



    @ApiOperation(value = "收入每日明细")
    @RequestMapping(value = "/getTotalInfoDay", method = RequestMethod.GET)
    public CommonResult<ClearingBonusListResponse> getTotalInfoDay() {
        ClearingBonusListResponse listResponses = clearingBonusService.getcleringInfoList(userService.getUserId());
        return CommonResult.success(listResponses);
    }

}



