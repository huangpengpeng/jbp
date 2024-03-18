package com.jbp.admin.controller.agent;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaXsRequest;
import com.jbp.common.request.agent.RiseConditionRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.condition.ConditionEnum;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.CapaXsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    @ApiOperation(value = "星级分页")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<CapaXs>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<CapaXs> result = CommonPage.restPage(capaXsService.page(pageParamRequest));
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('capa:xs:add')")
    @ApiOperation(value = "星级新增")
    @PostMapping(value = "/add")
    public CommonResult<Object> add(@RequestBody @Validated CapaXsRequest capaRequest) {
        capaXsService.save(capaRequest.getName(), capaRequest.getPCapaId(),
                capaRequest.getRankNum(), capaRequest.getIconUrl(),
                capaRequest.getRiseImgUrl(), capaRequest.getShareImgUrl());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:xs:edit')")
    @ApiOperation(value = "星级编辑")
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
    @ApiOperation(value = "星级详情")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<CapaXs> detail(@PathVariable(value = "id") Long id) {
        return CommonResult.success(capaXsService.getById(id));
    }

    @ApiOperation(value = "星级列表")
    @GetMapping(value = "/list")
    public CommonResult<List<CapaXs>> list() {
        return CommonResult.success(capaXsService.list());
    }

    @ApiOperation(value = "星级升级条件")
    @GetMapping(value = "/condition/list")
    public CommonResult<JSONObject> conditionList() {
        JSONObject json = new JSONObject();
        List<ConditionEnum> list = ConditionEnum.getCapaList().stream().filter(s -> s.getType().equals("星级")).collect(Collectors.toList());
        List<String> nameList = Lists.newArrayList();
        List<String> desList = Lists.newArrayList();
        for (ConditionEnum conditionEnum : list) {
            nameList.add(conditionEnum.getName());
            desList.add(conditionEnum.getDescription());
        }
        json.put("names", nameList);
        json.put("dess", desList);
        return CommonResult.success(json);
    }

    @PreAuthorize("hasAuthority('capa:xs:condition:save')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "星级升级条件保存")
    @ApiOperation(value = "星级升级条件保存")
    @PostMapping(value = "/condition/save")
    public CommonResult conditionSave(@RequestBody @Validated RiseConditionRequest request) {
        capaXsService.saveRiseCondition(request);
        return CommonResult.success();
    }
}
