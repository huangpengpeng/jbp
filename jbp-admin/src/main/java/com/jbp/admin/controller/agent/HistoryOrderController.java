package com.jbp.admin.controller.agent;

import com.beust.jcommander.internal.Maps;
import com.github.pagehelper.PageInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.HistoryOrderEditRequest;
import com.jbp.common.request.HistoryOrderRequest;
import com.jbp.common.request.HistoryOrderShipRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.HistoryOrderResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.erp.tools.Constants;
import com.jbp.service.erp.tools.SignUtil;
import com.jbp.service.service.agent.HistoryOrderService;
import com.jbp.service.service.agent.impl.HistoryOrderServiceImpl;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/admin/agent/history/order")
@Api(tags = "历史订单列表")
public class HistoryOrderController {

    @Resource
    private HistoryOrderService historyOrderService;
    @Autowired
    private Environment environment;

    @PreAuthorize("hasAuthority('agent:history:order:page')")
    @GetMapping("/page")
    @ApiOperation("列表")
    public CommonResult<CommonPage<HistoryOrderResponse>> page(HistoryOrderRequest request, PageParamRequest pageParamRequest) {
        if (StringUtils.isEmpty(request.getDbName())) {
            return CommonResult.success();
        }
        PageInfo<HistoryOrderResponse> historyOrderResponsePageInfo = historyOrderService.pageList(request, pageParamRequest);
        return CommonResult.success(CommonPage.restPage(historyOrderResponsePageInfo));
    }


    @PostMapping("/edit")
    @ApiOperation("编辑订单")
    public CommonResult edit(@RequestBody HistoryOrderEditRequest request) {
        if (StringUtils.isEmpty(request.getDbName())) {
            return CommonResult.success();
        }
        request.setShopId(HistoryOrderServiceImpl.DB_NAME_MAP.get(request.getDbName()));
        historyOrderService.edit(request);
        return CommonResult.success();
    }



    @PostMapping("/ship")
    @ApiOperation("人工发货")
    public CommonResult edit(@RequestBody HistoryOrderShipRequest request) {
        if (StringUtils.isEmpty(request.getDbName())) {
            return CommonResult.success();
        }
        historyOrderService.ship(request);
        return CommonResult.success();
    }




//    @ApiOperation(value = "授权订单", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    @RequestMapping(value = {"/oldsync2"}, produces = MediaType.APPLICATION_JSON_VALUE)
//    public CommonResult<String> sync2(String dbName) {
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("app_key", environment.getProperty("jushuitan.appKey"));
//        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
//        params.put("charset", Constants.CHARSET);
//        String state = environment.getProperty("jushuitan.url");
//        params.put("state", state);
//        String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
//        /**
//         * 拼接跳转地址
//         */
//        String url = Constants.AUTH_URL.replace("[app_key]", (String) params.get("app_key"))
//                .replace("[timestamp]", (String) params.get("timestamp")).replace("[charset]", (String) params.get("charset"))
//                .replace("[sign]", sign).replace("[state]", state);
//        //	return "redirect:"+url;
//        return CommonResult.success(url);
//
//    }


}
