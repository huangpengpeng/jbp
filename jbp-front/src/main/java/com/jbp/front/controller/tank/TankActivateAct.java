package com.jbp.front.controller.tank;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateInfoResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TankActivateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/front/tankActivate")
@Api(tags = "共享仓启动详情控制器")
public class TankActivateAct {

    @Resource
    private TankActivateService tankActivateService;


    @ApiOperation(value = "启动报表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Map<String,Object>> getReport(Long storeId) {

        Map<String,Object> map = new HashMap<>();
        map.put("day",tankActivateService.activateDay(storeId));
        map.put("weeks",tankActivateService.activateWeeks(storeId));
        map.put("month",tankActivateService.activateMonth(storeId));
        map.put("total",tankActivateService.activateTotal(storeId));
        map.put("list",tankActivateService.activateRecent(storeId));
        return CommonResult.success(map);
    }


    @ApiOperation(value = "启动管理", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<ActivateInfoResponse>> getRepogetListrt(@Validated PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(tankActivateService.getactivateList(pageParamRequest)));
    }

}
