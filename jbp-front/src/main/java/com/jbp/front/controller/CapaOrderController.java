package com.jbp.front.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.UserCapaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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

    @ApiOperation(value = "获取当前用户等级订单信息")
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
}
