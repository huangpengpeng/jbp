package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.SelfScoreGroup;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.SelfScoreGroupRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreGroupService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/self/score/group")
@Api(tags = "个人业绩分组")
public class SelfScoreGroupController {
    @Resource
    private SelfScoreGroupService selfScoreGroupService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:self:score:group:page')")
    @GetMapping("/page")
    @ApiOperation("个人业绩分组列表")
    public CommonResult<CommonPage<SelfScoreGroup>> getList(SelfScoreGroupRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(selfScoreGroupService.pageList(uid, request.getGroupName(), request.getAction(), pageParamRequest)));
    }
}