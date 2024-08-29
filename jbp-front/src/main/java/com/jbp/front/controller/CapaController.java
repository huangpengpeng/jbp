package com.jbp.front.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.condition.CapaPaymentHandler;
import com.jbp.service.condition.CapaRepairDifferenceHandler;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


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
    @Autowired
    private CapaRepairDifferenceHandler capaRepairDifferenceHandler;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CapaOrderService capaOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCapaService userCapaService;

    @ApiOperation(value = "等级记录列表[报单专用]")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<Capa>> getList() {

        List<Capa> capas = capaService.getList();
        String value =  systemConfigService.getValueByKey("system_register_capa");
        if(!value.isEmpty() && value.equals("1")){
            capas.remove(0);
        }
        return CommonResult.success(capas);
    }


     @ApiOperation(value = "获取升级等级购买金额")
    @RequestMapping(value = "/getUpgradesPrice", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getUpgradesPrice(Long capaId,Boolean rise,Long defaultCapa) {

        Capa capa = capaService.getById(capaId);
         Map<String, Object> map = new HashMap<>();
        //补差金额
        if(rise){
            List<CapaRepairDifferenceHandler.Rule> ruleList= capaRepairDifferenceHandler.getRule(capa.getConditionList().stream().filter(s -> s.getName().equals(capaRepairDifferenceHandler.getName())).findFirst().get());
             Map<Long, CapaRepairDifferenceHandler.Rule> ruleMap = FunctionUtil.keyValueMap(ruleList, CapaRepairDifferenceHandler.Rule::getOrgCapaId);
            CapaRepairDifferenceHandler.Rule rule = ruleMap.get(defaultCapa);
            map.put("riseOrderPrice", rule.getRepairDifference());

        }else{
            //单次升级金额
            CapaPaymentHandler.Rule rule = capaPaymentHandler.getRule(capa.getConditionList().stream().filter(s -> s.getName().equals(capaPaymentHandler.getName())).findFirst().get());
            map.put("riseOrderPrice", rule.getAmt());
        }
         map.put("name", capa.getName());

        return CommonResult.success(map);
    }


    @ApiOperation(value = "默认等级")
    @RequestMapping(value = "/defaultCapa", method = RequestMethod.GET)
    public CommonResult<Capa> defaultCapa() {
        return CommonResult.success(capaService.getMinCapa());
    }


    @ApiOperation(value = "获取大于当前等级的等级")
    @RequestMapping(value = "/getLevellist", method = RequestMethod.GET)
    public CommonResult<List<Capa>> getLevellist(Long capaId) {
        return CommonResult.success(capaService.getMaxCapaList(capaId));
    }

    @ApiOperation(value = "获取等级升级图片")
    @RequestMapping(value = "/getPicture", method = RequestMethod.GET)
    public CommonResult<List<Capa>> getPicture() {
        Integer uid = userService.getUserId();
        if (uid == null) {
            return CommonResult.success(CollUtil.newArrayList(new Capa()));
        }
        UserCapa userCapa = userCapaService.getByUser(uid);
        if (userCapa == null) {
            return CommonResult.success(CollUtil.newArrayList(new Capa()));
        }
        List<CapaOrder> showList = capaOrderService.list(new QueryWrapper<CapaOrder>().lambda().apply("if_show is true"));
        List<Long> capaIds = showList.stream().map(CapaOrder::getCapaId).filter(e -> e > userCapa.getCapaId()).collect(Collectors.toList());
        if (capaIds.isEmpty()) {
            capaIds.add(0L);
        }
        List<Capa> list = capaService.list(new QueryWrapper<Capa>().lambda().in(Capa::getId, capaIds));
        return CommonResult.success(list);
    }


}



