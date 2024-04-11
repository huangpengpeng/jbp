package com.jbp.admin.controller.tank;

import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentNumberAdminListResponse;
import com.jbp.common.response.EquipmentNumberInfoAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankEquipmentNumberInfoService;
import com.jbp.service.service.TankEquipmentNumberService;
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

@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankEquipmentNumber")
@Api(tags = "设备次数管理")
public class TankEquipmentNumberAct {



    @Resource
    private TankEquipmentNumberService tankEquipmentNumberService;
    @Resource
    private TankEquipmentNumberInfoService tankEquipmentNumberInfoService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('tank:tankEquipmentNumber:list')")
    @ApiOperation(value = "共享仓次数列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<EquipmentNumberAdminListResponse>> list(String username, PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(tankEquipmentNumberService.getAdminPageList(username, pageParamRequest)));
    }



    @PreAuthorize("hasAuthority('tank:tankEquipmentNumber:infoList')")
    @ApiOperation(value = "共享仓次数明细列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/infoList", produces = MediaType.APPLICATION_JSON_VALUE)
    public  CommonResult<CommonPage<EquipmentNumberInfoAdminListResponse>> infoList(Integer id, PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(tankEquipmentNumberInfoService.getAdminPageList(id, pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('tank:tankEquipmentNumber:save')")
    @ApiOperation(value = "共享仓次数增加", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult save(HttpServletRequest request, Long userId , Integer number ,String remark) {

      User user =  userService.getById(userId);
      if(user == null){
          throw new RuntimeException("店主用户id不存在");
      }
        tankEquipmentNumberService.increase(userId,number,"",remark);

        return CommonResult.success();
    }






}
