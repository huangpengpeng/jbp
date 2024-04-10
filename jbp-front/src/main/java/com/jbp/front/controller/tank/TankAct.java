package com.jbp.front.controller.tank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankActivateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.TreeMap;


@RestController
@RequestMapping("api/front/tank")
@Api(tags = "共享仓接口控制器")
public class TankAct {


    @Resource
    private TankActivateService tankActivateService;
    @Autowired
    private Environment environment;


    @ApiOperation(value = "获取共享仓token", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<JSONObject> getStoreList() {

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

        return CommonResult.success(goodsJson);

    }


    @ApiOperation(value = "启动设备", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)

    public CommonResult<String> activate(Integer worktime, Long equipment_id, String token) {

        tankActivateService.activateEquipment(worktime, equipment_id, token);

        return CommonResult.success();

    }


    @ApiOperation(value = "关闭设备", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> stop(Long equipment_id, String token) {

        Map<String, Object> params = new TreeMap<String, Object>();
        HttpRequest request = HttpRequest.post("https://system.swgzsb.com/api/external_service/equipment/stop");
        request.contentType("application/json");
        request.charset("utf-8");
        request.header("token", token);

        params.put("equipment_id", equipment_id);
        request.body(JSON.toJSONString(params));
        HttpResponse response1 = request.send();
        String respJson = response1.bodyText();
        JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
        return CommonResult.success();
    }


    @ApiOperation(value = "修改二维码规则", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/updateConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<JSONObject>  updateConfig(String token) {
        Map<String, Object> params = new TreeMap<String, Object>();
        HttpRequest request = HttpRequest.post("https://system.swgzsb.com/api/external_service/config/updateEquipmentQrcodeRule");
        request.contentType("application/json");
        request.charset("utf-8");

        String userPrefix = environment.getProperty("gxc.url");
        params.put("token", token);
        params.put("equipmentQrcodeRule", userPrefix + "/equipment_id/?equipmentSn={$equipment_id}");
        request.body(JSON.toJSONString(params));
        HttpResponse response1 = request.send();
        String respJson = response1.bodyText();
        JSONObject goodsJson = JSONObject.parseObject(respJson).getJSONObject("data");
        return CommonResult.success(goodsJson);
    }


}
