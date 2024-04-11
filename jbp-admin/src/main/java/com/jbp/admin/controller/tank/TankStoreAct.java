package com.jbp.admin.controller.tank;

import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankStoreAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankEquipmentService;
import com.jbp.service.service.TankStoreClerkRelationService;
import com.jbp.service.service.TankStoreRelationService;
import com.jbp.service.service.TankStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankStore")
@Api(tags = "门店管理")
public class TankStoreAct {


    @Resource
    private TankStoreService tankStoreService;
    @Resource
    private TankEquipmentService tankEquipmentService;
    @Resource
    private TankStoreRelationService tankStoreRelationService;
    @Resource
    private TankStoreClerkRelationService tankStoreClerkRelationService;


    @PreAuthorize("hasAuthority('tank:tankStore:list')")
    @ApiOperation(value = "门店列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<TankStoreAdminListResponse>> list(String username,String name, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankStoreService.getAdminPageList(username,name,pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('tank:tankStore:save')")
    @ApiOperation(value = "增加门店", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, ModelMap model, Long userId ,String address,String name ) {

       TankStoreRelation tankStoreRelation =  tankStoreRelationService.getStoreUserId(userId);
       if(tankStoreRelation== null){
           throw new RuntimeException("店主没有绑定舱主，无法添加");
       }
       TankStore tankStore =  tankStoreService.getName(name);
       if(tankStore != null){
           throw new RuntimeException("门店名称已存在，无法添加");
       }

        TankStore tankStore2=new TankStore();
        tankStore2.setUserId(userId);
        tankStore2.setName(name);
        tankStore2.setAddress(address);
        tankStore2.setCreatedTime(new Date());
        tankStore2.setNest("30");

        tankStoreService.save(tankStore2);

        return CommonResult.success();
    }



    @PreAuthorize("hasAuthority('tank:tankStore:edit')")
    @ApiOperation(value = "编辑门店", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult edit(HttpServletRequest request, ModelMap model, Long id ,String address,String name,String nest ) {

        TankStore tankStore =  tankStoreService.getById(id);
        tankStore.setAddress(address);
        tankStore.setName(name);
        tankStore.setNest(nest);
        tankStoreService.updateById(tankStore);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('tank:tankStore:delete')")
    @ApiOperation(value = "删除门店", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, ModelMap model, Long id  ) {

        TankStore tankStore =  tankStoreService.getById(id);

        List<TankEquipment> tankEquipment =  tankEquipmentService.getStoreId(tankStore.getId());

        if(!tankEquipment.isEmpty()){
            throw new RuntimeException("门店存在设备，无法删除");
        }

       TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(tankStore.getUserId());
        if(tankStoreRelation != null) {
            tankStoreRelationService.removeById(tankStoreRelation.getId());
        }

        List<TankStoreClerkRelation>  list =  tankStoreClerkRelationService.getStoreUserId(tankStore.getUserId());
        for(TankStoreClerkRelation tankStoreClerkRelation :list){
            tankStoreClerkRelationService.removeById(tankStoreClerkRelation.getId());
        }
        tankStoreService.removeById(id);

        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('tank:tankStore:get')")
    @ApiOperation(value = "查询编辑门店", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<TankStore> get(Long id) {
        return CommonResult.success(tankStoreService.getById(id));
    }


}
