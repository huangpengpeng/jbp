package com.jbp.front.controller;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.sgin.UserSignRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.SignPageInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.condition.CapaPaymentHandler;
import com.jbp.service.service.SignService;
import com.jbp.service.service.agent.CapaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 签到控制器
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
@RequestMapping("api/front/capa")
@Api(tags = "等级控制器")
public class CapaController {

    @Autowired
    private CapaService capaService;
    @Autowired
    private CapaPaymentHandler capaPaymentHandler;


    @ApiOperation(value = "等级记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<Capa>> getList() {
        return CommonResult.success(capaService.getList());
    }



    @ApiOperation(value = "获取升级等级购买金额")
    @RequestMapping(value = "/getUpgradesPrice", method = RequestMethod.GET)
    public CommonResult<Map<String,Object>> getUpgradesPrice(Long capaId) {

        Capa capa =capaService.getById(capaId);
        CapaPaymentHandler.Rule rule = capaPaymentHandler.getRule(capa.getConditionList().stream().filter(s-> s.getName().equals(capaPaymentHandler.getName())).findFirst().get());
        Map<String,Object> map =new HashMap<>();
        map.put("name",capa.getName());
        map.put("riseOrderPrice",rule.getAmt());
        return CommonResult.success(map);
    }


}


