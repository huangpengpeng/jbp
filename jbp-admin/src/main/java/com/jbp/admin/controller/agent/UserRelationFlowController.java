package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserRelationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserRelationFlowRequest;
import com.jbp.common.request.agent.UserRelationGplotRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.UserRelationGplotVo;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/relation/flow")
@Api(tags = "服务关系上下层级关系")
public class UserRelationFlowController {
    @Resource
    private UserRelationFlowService userRelationFlowService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:relation:flow:page')")
    @GetMapping("/page")
    @ApiOperation("服务关系上下层级关系列表")
    public CommonResult<CommonPage<UserRelationFlow>> pageList(UserRelationFlowRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getUAccount())) {
            User user = userService.getByAccount(request.getUAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        Integer pid = null;
        if (StringUtils.isNotEmpty(request.getPAccount())) {
            User user = userService.getByAccount(request.getPAccount());
            if (user == null) {
                throw new CrmebException("邀请上级账号错误");
            }
            pid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userRelationFlowService.pageList(uid, pid, request.getLevel(), pageParamRequest)));

    }

    @GetMapping("/gplot")
    @ApiOperation("服务关系上下层级关系拓扑图")
    public CommonResult<UserRelationGplotVo> gplot(UserRelationGplotRequest request){
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(userRelationFlowService.gplotInfo(uid,request.getLevel()));



    }



}
