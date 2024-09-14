package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.RefundOrderSearchRequest;
import com.jbp.common.request.agent.LotteryRecordEditRequest;
import com.jbp.common.request.agent.LotteryRecordSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.LotteryRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/lottery/record")
@Api(tags = "抽奖活动管理")
public class LotteryRecordController {

    @Autowired
    private LotteryRecordService lotteryRecordService;
    @Autowired
    private UserService userService;

//    @PreAuthorize("hasAuthority('agent:lottery:record:page')")
    @ApiOperation(value = "中奖记录分页列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<LotteryRecord>> pageList(LotteryRecordSearchRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(lotteryRecordService.pageList(uid,request.getPrizeType(),request.getStartTime(),request.getEndTime(), pageParamRequest)));
    }


//    @PreAuthorize("hasAuthority('agent:lottery:record:remark')")
    @ApiOperation(value = "中奖记录添加备注")
    @PostMapping(value = "/remark")
    public CommonResult<String> remark(@RequestBody @Validated LotteryRecordEditRequest request) {
        if (lotteryRecordService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

//    @PreAuthorize("hasAuthority('agent:lottery:record:excel')")
    @ApiOperation(value = "导出中奖记录列表Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<String> exportRefund(@Validated LotteryRecordSearchRequest request) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(lotteryRecordService.export(uid,request.getPrizeType(),request.getStartTime(),request.getEndTime()));

    }

}
