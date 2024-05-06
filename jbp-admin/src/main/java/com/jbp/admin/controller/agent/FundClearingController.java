package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.*;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.FundClearingVo;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/fund/clearing")
@Api(tags = "佣金发放记录")
public class FundClearingController {
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private Environment environment;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserCapaXsService userCapaXsService;




    @PreAuthorize("hasAuthority('agent:fund:clearing:page')")
    @GetMapping("/page")
    @ApiOperation("佣金发放记录列表")
    public CommonResult<CommonPage<FundClearing>> getList(FundClearingRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(fundClearingService.pageList(request.getUniqueNo(), request.getExternalNo(), request.getStartClearingTime(), request.getEndClearingTime(), request.getStartCreateTime(), request.getEndCreateTime(), request.getStatus(),
                uid, request.getTeamName(), request.getDescription(), request.getCommName(), request.getIfRefund(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:excel')")
    @ApiOperation(value = "佣金发放记录导出Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<List<FundClearingVo>> exportOrder(FundClearingRequest request) {
        if (StringUtils.isEmpty(request.getUniqueNo()) && StringUtils.isEmpty(request.getExternalNo()) &&
                request.getStartClearingTime() == null && request.getEndClearingTime() == null && request.getStartCreateTime() == null && request.getEndCreateTime() == null
                && StringUtils.isEmpty(request.getStatus()) && StringUtils.isEmpty(request.getAccount()) && StringUtils.isEmpty(request.getTeamName())
                && StringUtils.isEmpty(request.getDescription())&& ObjectUtils.isEmpty(request.getIfRefund())&&StringUtils.isEmpty(request.getCommName())) {
            throw new CrmebException("请填写一个过滤信息");
        }
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(fundClearingService.exportFundClearing(request.getUniqueNo(), request.getExternalNo(), request.getStartClearingTime(), request.getEndClearingTime(), request.getStartCreateTime(), request.getEndCreateTime(), request.getStatus(),
                uid, request.getTeamName(), request.getDescription(), request.getCommName(), request.getIfRefund()));
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

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:send:amt')")
    @PostMapping("/update/send/amt")
    @ApiOperation("修改发放金额")
    public CommonResult updateSendAmt(@RequestBody @Validated FundClearingUpdateSendAmtRequest request) {
        fundClearingService.updateSendAmt(request.getId(), request.getSendAmt(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:fund:clearing:update:if:refund')")
    @PostMapping("/update/if/refund")
    @ApiOperation("标记退回")
    public CommonResult updateIfRefund(@RequestBody @Validated FundClearingUpdateRequest request) {
        fundClearingService.updateIfRefund(request.getIds(), request.getRemark());
        return CommonResult.success();
    }




    @PreAuthorize("hasAuthority('agent:fund:clearing:save')")
    @PostMapping("/save")
    @ApiOperation("增加自定义佣金")
    public CommonResult save(@RequestBody @Validated FundClearingSaveRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (user == null) {
            throw new CrmebException("账号信息错误");
        }
        fundClearingService.create(user.getId(), request.getOrderNo(), request.getType(), request.getClearingFee(),
                null, request.getDescription(), request.getRemark());
        return CommonResult.success();
    }





    @PreAuthorize("hasAuthority('agent:fund:clearing:resellsave')")
    @PostMapping("/resellSave")
    @ApiOperation("增加复销奖佣金")
    public CommonResult resellSave(@RequestBody FundClearingMonthRequest request) {

        if (StringUtils.isBlank(request.getMonth())) {
            throw new RuntimeException("请选择月份");
        }
        fundClearingService.addFgComm(request.getMonth());

        return CommonResult.success();
    }



}
