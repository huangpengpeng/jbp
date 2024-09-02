package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.user.User;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
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


@Slf4j
@RestController
@RequestMapping("api/front/capa/order")
@Api(tags = "等级订货管理控制器")
public class CapaOrderController {

    @Autowired
    private CapaOrderService capaOrderService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInvitationService userInvitationService;

    @ApiOperation(value = "获取当前用户等级订货信息")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public CommonResult<CapaOrder> getList() {
        Integer uid = userService.getUserId();
        if (ObjectUtil.isNull(uid)) {
            return CommonResult.failed("获取当前用户信息失败！");
        }
        UserCapa userCapa = userCapaService.getByUser(uid);
        if (ObjectUtil.isNull(userCapa)) {
            return CommonResult.failed("当前用户没有等级！");
        }
        CapaOrder capaOrder = capaOrderService.getCapaOrderByUser(userCapa.getCapaId().intValue());
        return CommonResult.success(capaOrder);
    }


    @ApiOperation(value = "获取当前用户等级订单信息")
    @RequestMapping(value = "/pSupplyUser", method = RequestMethod.GET)
    public CommonResult<Map<String, String>> pSupplyUser() {
        Integer uid = userService.getUserId();
        if (ObjectUtil.isNull(uid)) {
            return CommonResult.failed("获取当前用户信息失败！");
        }
        UserCapa userCapa = userCapaService.getByUser(uid);
        if (ObjectUtil.isNull(userCapa)) {
            return CommonResult.failed("当前用户没有等级！");
        }
        Map<String, String> map = new HashMap<>();

        Integer pId = userInvitationService.getPid(uid);

        do {
            UserCapa pCapa = userCapaService.getByUser(pId);
            CapaOrder capaOrder = capaOrderService.getCapaOrderByUser(userCapa.getCapaId().intValue());
            if (pCapa.getCapaId() > userCapa.getCapaId() && capaOrder.getIfSupply()) {
                User user = userService.getById(pId);
                map.put("nickname", user.getNickname());
                map.put("account", user.getAccount());
                map.put("phone", user.getPhone());
                break;
            }
            pId = userInvitationService.getPid(pId);
            if (pId == null) {
                break;
            }

        } while (true);

        return CommonResult.success(map);
    }

    @ApiOperation(value = "获取订货和补货金额门槛")
    @RequestMapping(value = "/getAmount", method = RequestMethod.GET)
    public CommonResult<CapaOrder> getAmount(Integer capaId) {
        if (ObjectUtil.isNull(capaId)) {
            Integer uid = userService.getUserId();
            if (ObjectUtil.isNull(uid)) {
                return CommonResult.failed("获取当前用户信息失败！");
            }
            UserCapa userCapa = userCapaService.getByUser(uid);
            if (ObjectUtil.isNull(userCapa)) {
                return CommonResult.failed("当前用户没有等级！");
            }
            capaId = userCapa.getCapaId().intValue();
        }
        CapaOrder capaOrder = capaOrderService.getCapaOrderByUser(capaId);
        return CommonResult.success(capaOrder);
    }

    @ApiOperation(value = "获取当前等级赠送分数")
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public CommonResult<List<CapaOrder>> getAllList() {
        Integer uid = userService.getUserId();
        if (ObjectUtil.isNull(uid)) {
            return CommonResult.failed("获取当前用户信息失败！");
        }
        List<CapaOrder> list = capaOrderService.getList(uid);
        return CommonResult.success(list);
    }
}
