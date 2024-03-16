package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.SelfScoreFlowRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.SelfScoreFlowVo;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreFlowService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/self/score/flow")
@Api(tags = "个人业绩明细")
public class SelfScoreFlowController {
    @Resource
    private SelfScoreFlowService selfScoreFlowService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:self:score:flow:page')")
    @GetMapping("/page")
    @ApiOperation("个人业绩明细列表")
    public CommonResult<CommonPage<SelfScoreFlow>> getList(SelfScoreFlowRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(selfScoreFlowService.pageList(uid, request.getAction(),request.getOrdersSn(),request.getDateLimit(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:self:score:flow:excel')")
    @PostMapping("/excel")
    @ApiOperation("个人业绩明细")
    public CommonResult<List<SelfScoreFlowVo>> excel(SelfScoreFlowRequest request) {
        if (ObjectUtil.isEmpty(request)) {
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
        return CommonResult.success(selfScoreFlowService.excel(uid, request.getAction(),request.getOrdersSn(),request.getDateLimit()));
    }

}
