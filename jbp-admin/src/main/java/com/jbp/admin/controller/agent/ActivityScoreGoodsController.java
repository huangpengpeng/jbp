package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.*;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ActivityScoreGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score/goods")
@Api(tags = "积分活动商品管理")
public class ActivityScoreGoodsController {

    @Autowired
    private ActivityScoreGoodsService activityScoreGoodsService;

    @ApiOperation(value = "积分活动商品管理分页列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<ActivityScoreGoods>> page(@Validated ActivityScoreGoodsSearchRequest request, PageParamRequest pageParamRequest) {
        CommonPage<ActivityScoreGoods> result = CommonPage.restPage(activityScoreGoodsService.getList(request,pageParamRequest));
        return CommonResult.success(result);
    }

    @ApiOperation(value = "积分活动商品增加")
    @PostMapping(value = "/add")
    public CommonResult<Boolean> add(@RequestBody @Validated ActivityScoreGoodsAddRequest request) {
        return CommonResult.success(activityScoreGoodsService.add(request));
    }

    @ApiOperation(value = "积分活动商品编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Boolean> add(@RequestBody @Validated ActivityScoreGoodsEditRequest request) {
        return CommonResult.success(activityScoreGoodsService.edit(request));
    }

    @ApiOperation(value = "积分活动商品管理详情")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<ActivityScoreGoods> detail(@PathVariable(value = "id") Long id) {
        ActivityScoreGoods activityScoreGoods = activityScoreGoodsService.getById(id);
        if (activityScoreGoods == null) {
            throw new CrmebException("积分活动商品不存在");
        }
        return CommonResult.success(activityScoreGoods);
    }


    @ApiOperation(value = "积分活动商品管理删除")
    @PostMapping(value = "/delete/{id}")
    public CommonResult<Boolean> delete(@PathVariable(value = "id") Long id) {
        ActivityScoreGoods activityScoreGoods = activityScoreGoodsService.getById(id);
        if (activityScoreGoods == null) {
            throw new CrmebException("积分活动商品不存在");
        }
        return CommonResult.success(activityScoreGoodsService.removeById(id));
    }







}
