package com.jbp.admin.controller.agent;

import cn.hutool.core.io.unit.DataUnit;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRequest;
import com.jbp.common.request.agent.FundClearingUpdateRemarkRequest;
import com.jbp.common.request.agent.FundClearingUpdateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.FundClearingVo;
import com.jbp.service.service.agent.FundClearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.DataUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/fund/clearing")
@Api(tags = "佣金发放记录")
public class FundClearingController {
    @Resource
    private FundClearingService fundClearingService;

    @PreAuthorize("hasAuthority('agent:fund:clearing:page')")
    @GetMapping("/page")
    @ApiOperation("佣金发放记录列表")
    public CommonResult<CommonPage<FundClearing>> getList(FundClearingRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(fundClearingService.pageList(request.getUniqueNo(), request.getExternalNo(), request.getStartClearingTime(), request.getEndClearingTime(), request.getStartCreateTime(), request.getEndCreateTime(), request.getStatus(), pageParamRequest)));
    }

    @GetMapping("/status/list")
    @ApiOperation("结算状态列表")
    public CommonResult<Object> statusList() {
        return CommonResult.success(FundClearing.Constants.values());
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:wait:audit')")
    @PostMapping("/update/wait/audit")
    @ApiOperation("更新待审核")
    public CommonResult updateWaitAudit(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateWaitAudit(request.getIds(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:wait:send')")
    @PostMapping("/update/wait/send")
    @ApiOperation("更新待出款")
    public CommonResult updateWaitSend(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateWaitSend(request.getIds(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:send')")
    @PostMapping("/update/send")
    @ApiOperation("更新已出款")
    public CommonResult updateSend(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateSend(request.getIds(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:cancel')")
    @PostMapping("/update/cancel")
    @ApiOperation("更新已取消")
    public CommonResult updateCancel(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateCancel(request.getIds(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:intercept')")
    @PostMapping("/update/intercept")
    @ApiOperation("更新已拦截")
    public CommonResult updateIntercept(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateIntercept(request.getIds(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:remark')")
    @PostMapping("/update/remark")
    @ApiOperation("修改备注")
    public CommonResult updateRemark(@RequestBody @Validated FundClearingUpdateRemarkRequest request) {
        fundClearingService.updateRemark(request.getId(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:excel')")
    @ApiOperation(value = "佣金发放记录导出Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<List<FundClearingVo>> exportOrder(FundClearingRequest request) {
        if (StringUtils.isEmpty(request.getUniqueNo())&&StringUtils.isEmpty(request.getExternalNo())&& request.getStartClearingTime()==null&&request.getEndClearingTime()==null&&request.getStartCreateTime()==null&&request.getEndCreateTime()==null&&request.getStatus()==null){
            throw new CrmebException("请填写一个过滤信息");
        }
        return CommonResult.success(fundClearingService.exportFundClearing(request.getUniqueNo(), request.getExternalNo(), request.getStartClearingTime(), request.getEndClearingTime(), request.getStartCreateTime(), request.getEndCreateTime(), request.getStatus()));
    }
}
