package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RelationScoreRequest;
import com.jbp.common.request.agent.RelationScoreUpdateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
@RequestMapping("api/admin/agent/relation/score")
@Api(tags = "服务业绩汇总")
public class RelationScoreController {
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:relation:score:page')")
    @ApiOperation("服务业绩汇总列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<RelationScore>> getList(RelationScoreRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(relationScoreService.pageList(uid, pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('agent:relation:score:increase')")
    @ApiOperation("增加业绩")
    @PostMapping("/increase")
    public CommonResult increase(@Validated @RequestBody RelationScoreUpdateRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (user == null) {
            throw new CrmebException("账号信息错误");
        }
        relationScoreService.operateIncreaseUsable(user.getId(),
                request.getScore().intValue(), request.getNode(), request.getOrdersSn(), request.getPayTime(), request.getRemark());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:relation:score:reduce')")
    @ApiOperation("减少业绩")
    @PostMapping("/reduce")
    public CommonResult reduce(@Validated @RequestBody RelationScoreUpdateRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (user == null) {
            throw new CrmebException("账号信息错误");
        }
        relationScoreService.operateReduceUsable(user.getId(), request.getScore(), request.getNode(),
                request.getOrdersSn(), request.getPayTime(), request.getRemark(), request.getIfUpdateUsed());
        return CommonResult.success();
    }


    @PreAuthorize("hasAuthority('agent:relation:score:fake:set')")
    @ApiOperation("设置虚拟业绩")
    @GetMapping("/fake/edit")
    public CommonResult fakeEdit(String account, int node, int score) {
        if (StringUtils.isNotEmpty(account)) {
            throw new CrmebException("账号信息不能为空");
        }
        if (0 != node && 1 != node) {
            throw new CrmebException("节点信息错误");
        }
        if (0 == score) {
            throw new CrmebException("积分信息错误");
        }
        User user = userService.getByAccount(account);
        if (user == null) {
            throw new CrmebException("账号信息错误");
        }
        RelationScore relationScore = relationScoreService.getByUser(user.getId(), node);
        if (relationScore == null) {
            throw new CrmebException("业绩信息不存在");
        }
        relationScore.setFakeScore(BigDecimal.valueOf(score));
        relationScoreService.updateById(relationScore);
        return CommonResult.success();
    }

}
