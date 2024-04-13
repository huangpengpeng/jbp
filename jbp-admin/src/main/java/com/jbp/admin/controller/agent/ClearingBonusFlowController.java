package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ClearingBonusFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/clearing/bonus/flow")
@Api(tags = "结算管理")
public class ClearingBonusFlowController {

    @Resource
    private ClearingBonusFlowService clearingBonusFlowService;

    @GetMapping("/page")
    @ApiOperation("结算奖金明细列表")
    public CommonResult<CommonPage<ClearingBonusFlow>> getList(Integer uid, String account, Long clearingId,
                                                               PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(clearingBonusFlowService.pageList(uid, account, clearingId, pageParamRequest)));
    }

}
