package com.jbp.front.controller.tank;

import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.response.TankStoreManageListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("api/front/tankStore")
@Api(tags = "共享仓订单控制器")
public class TankStoreAct {

    @Resource
    private TankStoreService tankStoreService;
    @Resource
    private TankStoreRelationService tankStoreRelationService;
    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;
    @Resource
    private TankEquipmentService tankEquipmentService;
    @Resource
    private UserService userService;

    @ApiOperation(value = "舱主绑定店主", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/addStoreRelation", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult addOrder(String name, String phone) {


        List<User> users = userService.getByPhone(phone);
        User user = new User();
        if (users.isEmpty()) {
            user = userService.registerPhone(name, phone, 0);
        } else {
            user = users.get(0);
        }

        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(user.getId().longValue());
        if (tankStoreRelation != null) {
            throw new IllegalStateException("店主已被绑定，请选择其他店主");
        }

        TankStoreRelation tankStoreRelation1 = new TankStoreRelation();
        tankStoreRelation1.setCreatedTime(new Date());
        tankStoreRelation1.setTankUserId(userService.getInfo().getId().longValue());
        tankStoreRelation1.setStoreUserId(user.getId().longValue());

        tankStoreRelationService.save(tankStoreRelation1);

        return CommonResult.success();
    }


    @ApiOperation(value = "门店管理列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<TankStoreManageListResponse>> getStoreList() {

        return CommonResult.success(tankStoreService.getStoreManageList());
    }


    @ApiOperation(value = "删除门店", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(Long id) {


        TankStore tankStore = tankStoreService.getById(id);
        List<TankEquipment> tankEquipment = tankEquipmentService.getStoreId(tankStore.getId());
        if (!tankEquipment.isEmpty()) {
            throw new RuntimeException("门店存在设备，无法删除");
        }
        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(tankStore.getUserId());
        if (tankStoreRelation != null) {
            tankStoreRelationService.removeById(tankStoreRelation.getId());
        }
        List<TankStoreClerkRelation> list = tankStoreClerkRelationService.getStoreUserId(tankStore.getUserId());
        for (TankStoreClerkRelation tankStoreClerkRelation : list) {
            tankStoreClerkRelationService.removeById(tankStoreClerkRelation.getId());
        }
        tankStoreService.removeById(id);

        return CommonResult.success();
    }


    @ApiOperation(value = "用户身份", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getUserStanding", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> getUserStanding() {

        String standing = "";
        User user = userService.getInfo();
        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(user.getId().longValue());
        List<TankStoreRelation> list = tankStoreRelationService.getTankUserId(user.getId());
        if (tankStoreRelation != null) {
            standing = "店主";
        }
        if (!list.isEmpty()) {
            standing = standing + ",舱主";
        }

        TankStoreClerkRelation tankStoreClerkRelation = tankStoreClerkRelationService.getClerkUserId(user.getId());
        if (tankStoreClerkRelation != null) {
            standing = standing + ",店员";
        }

        return CommonResult.success(standing);
    }

}
