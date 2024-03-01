package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.agent.LztAcctOpenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/lzt/acct/open")
@Api(tags = "来账通-开户申请")
public class LztAcctOpenController {

    @Resource
    private LztAcctOpenService lztAcctOpenService;

    @ApiOperation(value = "开户申请")
    @GetMapping(value = "/apply")
    public CommonResult<LztAcctOpen> apply(Integer merId, String userId, String userType, String returnUrl, String businessScope) {
        LztAcctOpen lztAcctOpen = lztAcctOpenService.apply(merId, userId, userType, returnUrl, businessScope);
        return CommonResult.success(lztAcctOpen);
    }

    @ApiOperation(value = "来账通账户刷新")
    @GetMapping(value = "/refresh")
    public CommonResult refresh(String accpTxno) {
        lztAcctOpenService.refresh(accpTxno);
        return CommonResult.success();
    }


    @ApiOperation(value = "开户记录列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<LztAcctOpen>> page(String userId, String status, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        return CommonResult.success(CommonPage.restPage(lztAcctOpenService.pageList(merId, userId, status, pageParamRequest)));
    }
}
