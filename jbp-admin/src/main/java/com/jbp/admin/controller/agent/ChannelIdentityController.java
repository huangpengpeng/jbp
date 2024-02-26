package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ChannelCardRequest;
import com.jbp.common.request.agent.ChannelIdentityPageRequest;
import com.jbp.common.request.agent.ChannelIdentityRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ChannelIdentityService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/channel/identity")
@Api(tags = "渠道身份信息")
public class ChannelIdentityController {
    @Resource
    private ChannelIdentityService channelIdentityService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:channel:identity:page')")
    @GetMapping("/page")
    @ApiOperation("渠道身份信息列表")
    public CommonResult<CommonPage<ChannelIdentity>> getList(ChannelIdentityPageRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(channelIdentityService.pageList(uid,request.getIdCardNo(),request.getRealName(),request.getChannel(),pageParamRequest)));
    }
}
