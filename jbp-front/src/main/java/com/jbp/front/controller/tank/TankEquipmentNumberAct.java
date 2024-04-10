package com.jbp.front.controller.tank;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.tank.TankEquipmentNumber;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentNumberInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.CapaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController("tank")
@RequestMapping("api/front/tankEquipmentNumber")
@Api(tags = "共享仓次数控制器")
public class TankEquipmentNumberAct {


    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;
    @Resource
    private CapaService capaService;
    @Resource
    private TankEquipmentNumberService tankEquipmentNumberService;
    @Resource
    private UserService userService;
    @Resource
    private TankEquipmentService tankEquipmentService;
    @Resource
    private TankEquipmentNumberInfoService tankEquipmentNumberInfoService;


    @ApiOperation(value = "共享仓剩余次数", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Integer> getNumber() {

        TankEquipmentNumber tankEquipmentNumber = tankEquipmentNumberService.getOne(new QueryWrapper<TankEquipmentNumber>().lambda().eq(TankEquipmentNumber::getStoreUserId, userService.getInfo().getId()));

        return CommonResult.success(tankEquipmentNumber == null ? 0 : tankEquipmentNumber.getNumber());
    }


    @ApiOperation(value = "共享仓扣减次数", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult edit(Long equipmentId, Long userId) {

        TankEquipment tankEquipment = tankEquipmentService.getEquipmentSn(equipmentId);
        tankEquipmentNumberService.reduce(tankEquipment.getStoreUserId(), userId);

        return CommonResult.success();
    }


    @ApiOperation(value = "共享仓次数明细", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<EquipmentNumberInfoResponse>> getInfo(String type, @Validated PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(tankEquipmentNumberInfoService.getPageList(type, pageParamRequest)));
    }


}
