package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RelationScoreAddRequest;
import com.jbp.common.request.agent.RelationScoreEditRequest;
import com.jbp.common.request.agent.RelationScoreRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/relation/score")
@Api(tags = "服务业绩汇总")
public class RelationScoreController {
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private UserService userService;

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

    @ApiOperation("新增")
    @PostMapping("/add")
    public CommonResult add(RelationScoreAddRequest request) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        relationScoreService.save(uid,request.getNode());
        return CommonResult.success();
    }

    @ApiOperation("编辑")
    @PostMapping("/edit")
    public CommonResult edit(RelationScoreEditRequest request) {
        relationScoreService.edit(request.getId(),request.getUsableScore(),request.getUsedScore(),request.getNode());
        return CommonResult.success();
    }
    @ApiOperation("删除")
    @GetMapping("/delete/{id}")
    public CommonResult delete(@PathVariable("id")Integer id) {
        relationScoreService.removeById(id);
        return CommonResult.success();
    }
}