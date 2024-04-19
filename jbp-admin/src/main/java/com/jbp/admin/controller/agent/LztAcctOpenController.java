package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.LztService;
import com.jbp.service.service.agent.LztAcctOpenService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztPayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Resource
    private LztPayChannelService lztPayChannelService;
    @Resource
    private LztAcctService lztAcctService;


    @PreAuthorize("hasAuthority('agent:lzt:acct:open:apply')")
    @ApiOperation(value = "开户申请")
    @GetMapping(value = "/apply")
    public CommonResult<LztAcctOpen> apply(Long payChannelId, String partnerUserId,  Integer merId, String userId, String userType, String returnUrl, String businessScope) {
        if (ObjectUtil.isEmpty(merId)) {
            SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
             merId = systemAdmin.getMerId();
        }
        if (payChannelId == null && partnerUserId == null) {
            throw new RuntimeException("请选择支付渠道");
        }
        if(partnerUserId != null){
            LztAcct lztAcct = lztAcctService.getByUserId(partnerUserId);
            payChannelId = lztAcct.getPayChannelId();
        }
        if(payChannelId != null){
            LztPayChannel lztPayChannel = lztPayChannelService.getById(payChannelId);
            if (lztPayChannel == null) {
                throw new RuntimeException("支付渠道不存在");
            }
            if (lztPayChannel.getMerId().intValue() != merId.intValue()) {
                throw new RuntimeException("只能选择当前商户的支付渠道");
            }
        }
        LztAcctOpen lztAcctOpen = lztAcctOpenService.apply(merId, userId, userType, returnUrl, businessScope, payChannelId);
        return CommonResult.success(lztAcctOpen);
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:open:page')")
    @ApiOperation(value = "开户记录列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<LztAcctOpen>> page(String userId, String status, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        return CommonResult.success(CommonPage.restPage(lztAcctOpenService.pageList(merId, userId, status, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:lzt:acct:open:del')")
    @ApiOperation(value = "开户记录删除")
    @GetMapping(value = "/del")
    public CommonResult del(Long id) {
        lztAcctOpenService.del(id);
        return CommonResult.success();
    }
}
