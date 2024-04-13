package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ClearingBonusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/clearing/bonus")
@Api(tags = "结算管理")
public class ClearingBonusController {

    @Resource
    private ClearingBonusService clearingBonusService;

    @GetMapping("/page")
    @ApiOperation("结算奖金列表")
    public CommonResult<CommonPage<ClearingBonus>> getList(Integer uid, String account, String uniqueNo, Long clearingId,
                                                           PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(clearingBonusService.pageList(uid, account, uniqueNo, clearingId, pageParamRequest)));
    }


}
