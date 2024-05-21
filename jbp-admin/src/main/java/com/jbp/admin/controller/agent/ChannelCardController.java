package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ChannelCardEditRequest;
import com.jbp.common.request.agent.ChannelCardRequest;
import com.jbp.common.response.ChannelCardExtResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/channel/card")
@Api(tags = "渠道银行卡")
public class ChannelCardController {
    @Resource
    private ChannelCardService channelCardService;

    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:channel:card:page')")
    @GetMapping("/page")
    @ApiOperation("渠道银行卡列表")
    public CommonResult<CommonPage<ChannelCardExtResponse>> getList(ChannelCardRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(channelCardService.pageList(uid,request.getBankCardNo(),request.getType(),request.getPhone(),request.getTeamId(),pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:channel:card:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改渠道银行卡")
    @PostMapping("/update")
    @ApiOperation("修改渠道银行卡")
    public CommonResult update(@RequestBody @Validated ChannelCardEditRequest request) {
        channelCardService.update(request.getId(),request.getBankName(),request.getBankCardNo(),request.getBankId(),
                request.getPhone(),request.getType(),request.getBranchId(),request.getBranchName(),request.getProvince(),request.getCity());
        return CommonResult.success();
    }


}
