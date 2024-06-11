package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.HistoryOrderEditRequest;
import com.jbp.common.request.HistoryOrderRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.HistoryOrderResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.HistoryOrderService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
        Map<String, String> map = Maps.newConcurrentMap();
        map.put("wkp42271043176625", "");
        map.put("tf138940740527575", "");
        map.put("xcsmall", "");
        map.put("jymall", "");
        request.setShopId(map.get(request.getDbName()));
        historyOrderService.edit(request);
        return CommonResult.success();
    }

}
