package com.jbp.admin.controller.tank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentAdminListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankEquipmentService;
import com.jbp.service.service.TankStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


@Slf4j
@RestController
@RequestMapping("api/admin/tank/tankEquipment")
@Api(tags = "设备管理")
public class TankEquipmentAct {


    @Resource
    private TankEquipmentService tankEquipmentService;
    @Resource
    private TankStoreService tankStoreService;

    @PreAuthorize("hasAuthority('tank:tankEquipment:list')")
    @ApiOperation(value = "设备列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<EquipmentAdminListResponse>> list(String username, String name, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(tankEquipmentService.getAdminPageList(username, name, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('tank:tankEquipment:save')")
    @ApiOperation(value = "增加设备", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult delete(HttpServletRequest request, Long orderbondactivateId, String imei, String name, String equipmentSn, Long storeId) {

        TankStore tankStore = tankStoreService.getById(storeId);
        if (tankStore == null) {
            throw new RuntimeException("门店id不存在");
        }
        TankEquipment tankEquipment = new TankEquipment();
        tankEquipment.setOrderbondactivateId(orderbondactivateId);
        tankEquipment.setStoreUserId(tankStore.getUserId());
        tankEquipment.setStoreId(tankStore.getId());
        tankEquipment.setImei(imei);
        tankEquipment.setType("共享仓");
        tankEquipment.setName(name);
        tankEquipment.setCreatedTime(new Date());
        tankEquipment.setEquipmentSn(equipmentSn);

        tankEquipmentService.save(tankEquipment);

        JSONObject jsonObject = uploadequipment(tankEquipment.getEquipmentSn());

        if (jsonObject != null) {
            tankEquipment.setActivateStatus(jsonObject.getString("start_status"));
            tankEquipment.setOnlineStatus(jsonObject.getString("online_status"));
            tankEquipment.setUseStatus(jsonObject.getString("active_status"));
            tankEquipment.setStatus(jsonObject.getString("status"));
            tankEquipmentService.updateById(tankEquipment);
        }
        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('tank:tankEquipment:edit')")
    @ApiOperation(value = "编辑设备", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult edit(HttpServletRequest request, Long storeId, Long id, String name, String equipmentSn) {

        TankStore tankStore = tankStoreService.getById(storeId);
        if (tankStore == null) {
            throw new RuntimeException("门店id不存在");
        }
        TankEquipment tankEquipment = tankEquipmentService.getById(id);
        tankEquipment.setName(name);
        tankEquipment.setStoreId(tankStore.getId());
        tankEquipment.setEquipmentSn(equipmentSn);
        tankEquipment.setStoreUserId(tankStore.getUserId());
        tankEquipmentService.updateById(tankEquipment);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('tank:tankEquipment:forbidden')")
    @ApiOperation(value = "禁用设备", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/forbidden", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult edit(HttpServletRequest request, Long id) {

        TankEquipment tankEquipment = tankEquipmentService.getById(id);
        tankEquipment.setProhibitStatus("1");
        tankEquipmentService.updateById(tankEquipment);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('tank:tankEquipment:recover')")
    @ApiOperation(value = "恢复设备", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/recover", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult recover(HttpServletRequest request, Long id) {

        TankEquipment tankEquipment = tankEquipmentService.getById(id);
        tankEquipment.setProhibitStatus("0");
        tankEquipmentService.updateById(tankEquipment);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('tank:tankEquipment:get')")
    @ApiOperation(value = "查询编辑设备", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<TankEquipment> get(Long id) {
        return CommonResult.success(tankEquipmentService.getById(id));
    }


    public JSONObject uploadequipment(String equipmentSn) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(equipmentSn)) {
            return jsonObject;
        }


        Map<String, String> params = new TreeMap<String, String>();

        HttpRequest request = HttpRequest.post("https://system.swgzsb.com/api/external_service/user/login");
        request.contentType("application/json");
        request.charset("utf-8");

        params.put("appid", "2t7VHlqR0vZn5x1EijFYJom8SKkIDTbc");
        params.put("secret", "uhQgSRoyM3FifxwnpDatO7TAUP2Nmd1J");
        request.body(JSON.toJSONString(params));
        HttpResponse response1 = request.send();
        String respJson = response1.bodyText();
        JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
        ;

        HttpRequest request2 = HttpRequest.get("https://system.swgzsb.com/api/external_service/equipment/getEquipmentInfo?equipment_id=" + equipmentSn);
        request2.contentType("application/json");
        request2.charset("utf-8");
        request2.header("token", goodsJson.getString("token"));
        HttpResponse response2 = request2.send();
        String respJson2 = response2.bodyText();
        jsonObject = JSONObject.parseObject(respJson2).getJSONObject("data");


        return jsonObject;

    }


}
