package com.jbp.front.controller.tank;

import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankEquipmentService;
import com.jbp.service.service.TankStoreClerkRelationService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController("tank")
@RequestMapping("api/front/tankEquipment")
@Api(tags = "共享仓设备控制器")
public class TankEquipmentAct {


    @Resource
    private TankEquipmentService tankEquipmentService;
    @Resource
    private UserService userService;
    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;

    @ApiOperation(value = "设备管理", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<EquipmentListResponse>> getRepogetListrt(String type, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankEquipmentService.getPageList(type, pageParamRequest)));
    }


    @ApiOperation(value = "设备关联机器", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/addEquipmentSn", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> getInfo(String equipmentSn, Long id) {

        TankEquipment tankEquipment = tankEquipmentService.getById(id);
        if (StringUtils.isBlank(tankEquipment.getEquipmentSn())) {
            tankEquipment.setEquipmentSn(equipmentSn);
            tankEquipmentService.updateById(tankEquipment);
        }

        return CommonResult.success();
    }


    @ApiOperation(value = "设备详情", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Map<String, Object>> getInfo(String equipmentSn) {
        Integer userId = userService.getInfo().getId();
        if (StringUtils.isBlank(equipmentSn)) {
            throw new IllegalStateException("设备编号不存在");
        }
        TankEquipment tankEquipment = tankEquipmentService.getEquipmentSn(Long.parseLong(equipmentSn));

        if (tankEquipment.getProhibitStatus() != null && tankEquipment.getProhibitStatus().equals("1")) {
            throw new IllegalStateException("设备已禁用，无法使用");
        }

        Boolean ifRole = false;
        if (!userId.equals(tankEquipment.getStoreUserId())) {
            List<TankStoreClerkRelation> list = tankStoreClerkRelationService.getStoreId(tankEquipment.getStoreId());
            for (TankStoreClerkRelation tankStoreClerkRelation : list) {
                if (tankStoreClerkRelation.getClerkUserId().equals(userId)) {
                    ifRole = true;
                }
            }
        } else {
            ifRole = true;
        }

        if (!ifRole) {
            throw new IllegalStateException("不是店员或店主，无权限扫码开舱");
        }
        Map<String, Object> map = tankEquipmentService.getInfo(equipmentSn);

        return CommonResult.success(map);
    }


    @ApiOperation(value = "设备数量", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getInfoNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Map<String, Object>> getInfoNumber() {


        Map<String, Object> map = new HashMap<>();

        Integer total = tankEquipmentService.equipmentNumber();
        Integer use = tankEquipmentService.equipmentUseNumber();
        Integer online = tankEquipmentService.equipmentOnlineUnusedNumber();
        Integer offline = tankEquipmentService.equipmentOfflinedNumber();

        map.put("total", total);
        map.put("use", use);
        map.put("online", online);
        map.put("offline", offline);

        return CommonResult.success(map);
    }

}
