package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreAddRequest;
import com.jbp.common.request.agent.ActivityScoreEditRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ActivityScoreGoodsService;
import com.jbp.service.service.agent.ActivityScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score")
@Api(tags = "积分活动管理")
public class ActivityScoreController {

    @Autowired
    private ActivityScoreService activityScoreService;
    @Autowired
    private ActivityScoreGoodsService activityScoreGoodsService;

    @PreAuthorize("hasAuthority('activity:score:list')")
    @ApiOperation(value = "积分活动管理分页列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<ActivityScore>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<ActivityScore> result = CommonPage.restPage(activityScoreService.getList(pageParamRequest));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "积分活动管理详情")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<ActivityScore> detail(@PathVariable(value = "id") Long id) {
        ActivityScore activityScore = activityScoreService.getById(id);
        if (activityScore == null) {
            throw new CrmebException("积分活动不存在");
        }
        return CommonResult.success(activityScore);
    }


    @PreAuthorize("hasAuthority('activity:score:add')")
    @ApiOperation(value = "积分活动增加")
    @PostMapping(value = "/add")
    public CommonResult<Integer> add(@RequestBody @Validated ActivityScoreAddRequest request) {
        return CommonResult.success(activityScoreService.add(request));
    }

    @PreAuthorize("hasAuthority('activity:score:edit')")
    @ApiOperation(value = "积分活动编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Boolean> edit(@RequestBody @Validated ActivityScoreEditRequest request) {
        return CommonResult.success(activityScoreService.edit(request));
    }

    @PreAuthorize("hasAuthority('activity:score:delete')")
    @ApiOperation(value = "积分活动管理删除")
    @PostMapping(value = "/delete/{id}")
    public CommonResult<Boolean> delete(@PathVariable(value = "id") Long id) {
        ActivityScore activityScore = activityScoreService.getById(id);
        if (activityScore == null) {
            throw new CrmebException("积分活动不存在");
        }
        activityScoreService.removeById(id);
        activityScoreGoodsService.remove(new QueryWrapper<ActivityScoreGoods>().lambda().eq(ActivityScoreGoods::getActivityScoreId, id));
        return CommonResult.success(true);
    }

    @ApiOperation(value = "待结算积分活动下拉选")
    @GetMapping(value = "/tree")
    public CommonResult<List<ActivityScore>> tree() {
        List<ActivityScore> result = activityScoreService.tree();
        return CommonResult.success(result);
    }
}
