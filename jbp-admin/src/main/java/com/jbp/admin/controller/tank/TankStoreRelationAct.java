package com.jbp.admin.controller.tank;

import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankStoreRelationAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankStoreRelationService;
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
@RequestMapping("api/admin/tank/tankStoreRelation")
@Api(tags = "店主管理")
public class TankStoreRelationAct {


    @Resource
    private TankStoreRelationService tankStoreRelationService;


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:list')")
    @ApiOperation(value = "店主管理", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<TankStoreRelationAdminListResponse>> list(String username, String storeusername, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankStoreRelationService.getAdminPageList(username, storeusername, pageParamRequest)));

    }


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:save')")
    @ApiOperation(value = "增加舱主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, Long userId, Long storeUserId) {


        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(storeUserId);
        if (tankStoreRelation != null) {
            throw new RuntimeException("店主已经被绑定，增加失败");
        }
        TankStoreRelation tankStoreRelation2 = new TankStoreRelation();
        tankStoreRelation2.setTankUserId(userId);
        tankStoreRelation2.setStoreUserId(storeUserId);
        tankStoreRelation2.setCreatedTime(new Date());

        tankStoreRelationService.save(tankStoreRelation2);

        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('tank:tankStoreRelation:delete')")
    @ApiOperation(value = "删除舱主店主", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, Long id) {

        tankStoreRelationService.removeById(id);

        return CommonResult.success();
    }


}
