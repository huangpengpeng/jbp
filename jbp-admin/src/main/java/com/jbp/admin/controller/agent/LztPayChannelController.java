package com.jbp.admin.controller.agent;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.LztPayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lzt/pay/channel")
@Api(tags = "来账通支付渠道")
public class LztPayChannelController {

    @Resource
    private LztPayChannelService lztPayChannelService;

    @GetMapping("/page")
    @ApiOperation("支付渠道列表")
    public CommonResult<CommonPage<LztPayChannel>> page(Integer merId, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lztPayChannelService.pageList(merId, pageParamRequest)));
    }

    @GetMapping("/list")
    @ApiOperation("支付渠道列表")
    public CommonResult<List<LztPayChannel>> list(Integer merId) {
        return CommonResult.success(lztPayChannelService.getByMer(merId));
    }

    @GetMapping("/list2")
    @ApiOperation("支付渠道列表")
    public CommonResult<List<LztPayChannel>> list2() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(lztPayChannelService.getByMer(systemAdmin.getMerId()));
    }

    @GetMapping("/typeList")
    @ApiOperation("渠道类型")
    public CommonResult<List<String>> typeList() {
        return CommonResult.success(Lists.newArrayList("连连", "易宝", "苏宁"));
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation("新增")
    public CommonResult<LztPayChannel> add(@RequestBody LztPayChannel request) {
        if (StringUtils.isEmpty(request.getName())) {
            throw new RuntimeException("名称必填");
        }
        if (StringUtils.isEmpty(request.getType())) {
            throw new RuntimeException("类型必填");
        }
        if (request.getMerId() == null) {
            throw new RuntimeException("商户必填");
        }
        if (request.getPartnerId() == null) {
            throw new RuntimeException("平台编号必填");
        }
        if (request.getTradeModel() == null) {
            throw new RuntimeException("模式必填");
        }
        if (request.getHandlingFee() == null) {
            throw new RuntimeException("手续费必填");
        }
        if (StringUtils.equals(request.getType(), "连连")) {
            if (StringUtils.isEmpty(request.getPriKey())) {
                throw new RuntimeException("私钥不能为空");
            }
        }
        LztPayChannel lztPayChannel = lztPayChannelService.getByMer(request.getMerId(), request.getType());
        if (request.getId() == null && lztPayChannel != null){
            throw new RuntimeException("当前商户支付渠道已经存在不允许重复添加");
        }
        if (lztPayChannel != null && request.getId() != null && lztPayChannel.getId().intValue() != request.getId().intValue()) {
            throw new RuntimeException("当前商户支付渠道已经存在不允许重复添加");
        }
        lztPayChannelService.saveOrUpdate(request);
        return CommonResult.success();
    }
}
