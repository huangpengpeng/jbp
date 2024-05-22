package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.InvitationScoreRequest;
import com.jbp.common.request.agent.ScoreDownloadRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/invitation/score")
@Api(tags = "销售业绩汇总")
public class InvitationScoreController {
    @Resource
    private InvitationScoreService invitationScoreService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:invitation:score:page')")
    @GetMapping("/page")
    @ApiOperation("销售业绩汇总列表")
    public CommonResult<CommonPage<InvitationScore>> getList(InvitationScoreRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(invitationScoreService.pageList(uid,request.getNickname(),pageParamRequest)));
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.EXPORT, description = "团队业绩下载")
    @PreAuthorize("hasAuthority('agent:invitation:score:download')")
    @PostMapping("/download")
    @ApiOperation("业绩下载")
    public CommonResult<String> download(@Validated @RequestBody ScoreDownloadRequest request) {
        return CommonResult.success(invitationScoreService.download(request));
    }
}
