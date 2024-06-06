package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LztAcctApplyRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.agent.LztAcctApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/lzt/acct/apply")
@Api(tags = "来账通账户申请")
public class LztAcctApplyController {
    @Resource
    private LztAcctApplyService lztAcctApplyService;
    @PreAuthorize("hasAuthority('agent:lzt:acct:apply:page')")
    @GetMapping("/page")
    @ApiOperation("来账通账户申请列表")
    public CommonResult<CommonPage<LztAcctApply>> getList(LztAcctApplyRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        return CommonResult.success(CommonPage.restPage(lztAcctApplyService.pageList(merId,request.getUserId(),request.getUsername(),request.getStatus(),pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('agent:lzt:acct:apply:del')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "来账通银行开户记录删除")
    @ApiOperation(value = "删除")
    @GetMapping(value = "/del")
    public CommonResult del(Long id) {
        lztAcctApplyService.del(id);
        return CommonResult.success();
    }
}
