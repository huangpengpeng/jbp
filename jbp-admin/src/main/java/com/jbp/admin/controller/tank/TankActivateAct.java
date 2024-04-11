package com.jbp.admin.controller.tank;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankActivateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankActivate")
@Api(tags = "启动管理")
public class TankActivateAct {


    @Resource
    private TankActivateService tankActivateService;


    @PreAuthorize("hasAuthority('tank:tankActivate:list')")
    @ApiOperation(value = "启动列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<ActivateAdminListResponse>> list(String username, String name,  String status,PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankActivateService.getadminActivateList(username,name,status,pageParamRequest)));
    }


}
