package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RelationScoreFlowRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.RelationScoreFlowVo;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreFlowService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/relation/score/flow")
@Api(tags = "服务业绩明细")
public class RelationScoreFlowController {
    @Resource
    private RelationScoreFlowService relationScoreFlowService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:relation:score:flow:page')")
    @ApiOperation("服务业绩明细列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<RelationScoreFlow>> getList(RelationScoreFlowRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        Integer orderuid = null;
        if (StringUtils.isNotEmpty(request.getOrderAccount())) {
            User user = userService.getByAccount(request.getOrderAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            orderuid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(relationScoreFlowService.pageList(uid, orderuid, request.getOrdersSn(), request.getDateLimit(),request.getNode(),request.getAction(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:relation:score:flow:excel')")
    @GetMapping("/excel")
    @ApiOperation("服务业绩明细导出")
    public CommonResult<List<RelationScoreFlowVo>> excel(RelationScoreFlowRequest request) {
        if (StringUtils.isEmpty(request.getAccount()) && StringUtils.isEmpty(request.getOrderAccount()) && StringUtils.isEmpty(request.getOrdersSn())&& ObjectUtils.isEmpty(request.getNode())&&StringUtils.isEmpty(request.getAction())&&StringUtils.isEmpty(request.getDateLimit()))
        {
            throw new CrmebException("请选择一个过滤条件");
        }
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        Integer orderuid = null;
        if (StringUtils.isNotEmpty(request.getOrderAccount())) {
            User user = userService.getByAccount(request.getOrderAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            orderuid = user.getId();
        }
        return CommonResult.success(relationScoreFlowService.excel(uid, orderuid, request.getOrdersSn(), request.getDateLimit(),request.getNode(),request.getAction()));
    }
}
