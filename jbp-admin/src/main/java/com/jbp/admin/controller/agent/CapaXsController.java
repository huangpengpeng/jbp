package com.jbp.admin.controller.agent;

import cn.hutool.core.util.NumberUtil;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaXsRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.CapaXsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/capa/xs")
@Api(tags = "星级")
public class CapaXsController {

    @Resource
    private CapaXsService capaXsService;
    @Resource
    private SystemAttachmentService systemAttachmentService;


    @PreAuthorize("hasAuthority('capa:xs:list')")
    @ApiOperation(value = "用户等级列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<CapaXs>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<CapaXs> result = CommonPage.restPage(capaXsService.page(pageParamRequest));
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('capa:xs:add')")
    @ApiOperation(value = "用户等级新增")
    @PostMapping(value = "/add")
    public CommonResult<Object> add(@RequestBody @Validated CapaXsRequest capaRequest) {
        capaXsService.save(capaRequest.getName(), capaRequest.getPCapaId(),
                capaRequest.getRankNum(), capaRequest.getIconUrl(),
                capaRequest.getRiseImgUrl(), capaRequest.getShareImgUrl());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:xs:edit')")
    @ApiOperation(value = "用户等级编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Object> edit(@RequestBody @Validated CapaXsRequest capaRequest) {
        if (capaRequest.getId() == null) {
            return CommonResult.failed("等级ID不能为空");
        }
        CapaXs capa = capaXsService.getById(capaRequest.getId());
        CapaXs capaToRankNum = capaXsService.getByRankNum(capaRequest.getRankNum());
        if (capaToRankNum != null && NumberUtil.compare(capaToRankNum.getId(), capa.getId()) != 0) {
            return CommonResult.failed("等级编号不能重复");
        }
        CapaXs capaToName = capaXsService.getByName(capaRequest.getName());
        if (capaToName != null && NumberUtil.compare(capaToName.getId(), capa.getId()) != 0) {
            return CommonResult.failed("等级名称不能重复");
        }
        String cdnUrl = systemAttachmentService.getCdnUrl();
        capa.setName(capaRequest.getName());
        capa.setPCapaId(capaRequest.getPCapaId());
        capa.setRankNum(capaRequest.getRankNum());
        capa.setIconUrl(systemAttachmentService.clearPrefix(capaRequest.getIconUrl(), cdnUrl));
        capa.setRiseImgUrl(systemAttachmentService.clearPrefix(capaRequest.getRiseImgUrl(), cdnUrl));
        capa.setShareImgUrl(systemAttachmentService.clearPrefix(capaRequest.getShareImgUrl(), cdnUrl));
        capaXsService.updateById(capa);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:xs:detail')")
    @ApiOperation(value = "用户等级编辑")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<CapaXs> detail(@PathVariable(value = "id") Long id) {
        return CommonResult.success(capaXsService.getById(id));
    }

    @ApiOperation(value = "等级列表")
    @GetMapping(value = "/list")
    public CommonResult<List<CapaXs>> list() {
        return CommonResult.success(capaXsService.list());
    }
}