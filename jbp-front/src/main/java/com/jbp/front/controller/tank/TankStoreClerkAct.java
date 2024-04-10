package com.jbp.front.controller.tank;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.response.TankStoreClerkManagerListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankStoreClerkRelationService;
import com.jbp.service.service.TankStoreRelationService;
import com.jbp.service.service.TankStoreService;
import com.jbp.service.service.UserService;
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
@RequestMapping("api/front/tankStoreClerk")
@Api(tags = "共享仓店主店员关联控制器")
public class TankStoreClerkAct {


    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;
    @Resource
    private UserService userService;
    @Resource
    private TankStoreService tankStoreService;
    @Resource
    private TankStoreRelationService tankStoreRelationService;


    @ApiOperation(value = "店主绑定店员", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/addClerkRelation", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult addClerkRelation(String name, String phone, Long storeId) {

        List<User> users = userService.getByPhone(phone);
        User user = new User();
        if (users.isEmpty()) {
            user = userService.registerPhone(name, phone, 0);
        } else {
            user = users.get(0);
        }

        TankStoreClerkRelation tankStoreClerkRelation = tankStoreClerkRelationService.getClerkUserId(user.getId());
        if (tankStoreClerkRelation != null) {
            throw new IllegalStateException("手机号已被绑定，请输入其他");
        }

        if (!tankStoreService.getStoreUserId(user.getId()).isEmpty()) {
            throw new IllegalStateException("用户为店主，不能成为店员");
        }

        if (!tankStoreRelationService.getTankUserId(user.getId()).isEmpty()) {
            throw new IllegalStateException("用户为舱主，不能成为店员");
        }

        TankStoreClerkRelation tankStoreClerkRelation1 = new TankStoreClerkRelation();
        tankStoreClerkRelation1.setStoreUserId(userService.getInfo().getId().longValue());
        tankStoreClerkRelation1.setClerkUserId(user.getId().longValue());
        tankStoreClerkRelation1.setCreatedTime(new Date());
        tankStoreClerkRelation1.setStoreId(storeId);

        return CommonResult.success();
    }


    @ApiOperation(value = "店员管理列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getClerkList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<TankStoreClerkManagerListResponse>> getClerkList() {
        return CommonResult.success(tankStoreClerkRelationService.getClerkList());
    }

    @ApiOperation(value = "门店列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<TankStore>> getStoreList() {

        List<TankStore> list = tankStoreService.list(new QueryWrapper<TankStore>().lambda().eq(TankStore::getUserId, userService.getInfo().getId()));
        return CommonResult.success(list);
    }


    @ApiOperation(value = "删除店员", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(Long clerkUserId) {

        TankStoreClerkRelation tankStoreClerkRelation = tankStoreClerkRelationService.getClerkUserId(clerkUserId.intValue());
        tankStoreClerkRelationService.removeById(tankStoreClerkRelation.getId());

        return CommonResult.success();
    }


}
