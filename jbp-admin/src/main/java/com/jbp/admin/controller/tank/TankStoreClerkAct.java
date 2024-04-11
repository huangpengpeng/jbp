package com.jbp.admin.controller.tank;

import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankStoreClerkAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankStoreClerkRelationService;
import com.jbp.service.service.TankStoreService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankStoreClerk")
@Api(tags = "设备管理")
public class TankStoreClerkAct {


    @Resource
    private TankStoreService tankStoreService;
    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('tank:tankStoreClerk:list')")
    @ApiOperation(value = "人员列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<TankStoreClerkAdminListResponse>> list(String username,String name, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankStoreClerkRelationService.getAdminPageList(username,name,pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('tank:tankStoreClerk:delete')")
    @ApiOperation(value = "删除店员", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request,Long id) {

        tankStoreClerkRelationService.removeById(id);
        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('tank:tankStoreClerk:save')")
    @ApiOperation(value = "增加店员", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, Long storeUserId,Long clerkUserId,Long storeId ) {


        User user = userService.getById(storeUserId);
        if(user == null){
            throw new RuntimeException("店主id不存在");
        }
        User clerkUser = userService.getById(clerkUserId);
        if(clerkUser == null){
            throw new RuntimeException("店员id不存在");
        }

        TankStore tankStore =  tankStoreService.getById(storeId);
        if(tankStore == null){
            throw new RuntimeException("门店id不存在");
        }

        TankStoreClerkRelation tankStoreClerkRelation = new TankStoreClerkRelation();
        tankStoreClerkRelation.setStoreUserId(storeUserId);
        tankStoreClerkRelation.setClerkUserId(clerkUserId);
        tankStoreClerkRelation.setCreatedTime(new Date());
        tankStoreClerkRelation.setStoreId(storeId);

        tankStoreClerkRelationService.save(tankStoreClerkRelation);
        return CommonResult.success();
    }




}
