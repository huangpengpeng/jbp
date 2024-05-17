package com.jbp.admin.controller.agent;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaRequest;
import com.jbp.common.request.agent.RiseConditionRequest;
import com.jbp.common.request.agent.UserInvitationJumpRequest;
import com.jbp.common.response.UserInvitationJumpListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.condition.ConditionEnum;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/capa")
@Api(tags = "等级")
public class CapaController {

    @Resource
    private CapaService capaService;
    @Resource
    private SystemAttachmentService systemAttachmentService;


    @PreAuthorize("hasAuthority('capa:list')")
    @ApiOperation(value = "用户等级列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<Capa>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<Capa> result = CommonPage.restPage(capaService.page(pageParamRequest));
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('capa:add')")
    @ApiOperation(value = "用户等级新增")
    @PostMapping(value = "/add")
    public CommonResult<Object> add(@RequestBody @Validated CapaRequest capaRequest) {
        capaService.save(capaRequest.getName(), capaRequest.getPCapaId(),
                capaRequest.getRankNum(), capaRequest.getIconUrl(),
                capaRequest.getRiseImgUrl(), capaRequest.getShareImgUrl());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:edit')")
    @ApiOperation(value = "用户等级编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Object> edit(@RequestBody @Validated CapaRequest capaRequest) {
        if (capaRequest.getId() == null) {
            return CommonResult.failed("等级ID不能为空");
        }
        Capa capa = capaService.getById(capaRequest.getId());
        Capa capaToRankNum = capaService.getByRankNum(capaRequest.getRankNum());
        if (capaToRankNum != null && NumberUtil.compare(capaToRankNum.getId(), capa.getId()) != 0) {
            return CommonResult.failed("等级编号不能重复");
        }
        Capa capaToName = capaService.getByName(capaRequest.getName());
        if (capaToName != null && NumberUtil.compare(capaToName.getId(), capa.getId()) != 0) {
            return CommonResult.failed("等级名称不能重复");
        }
        if (ObjectUtil.isNotEmpty(capaRequest.getPCapaId())){
            if (capa.getId()>capaRequest.getPCapaId()){
                throw new CrmebException("请设置下个等级比本等级较大");
            }
        }
        String cdnUrl = systemAttachmentService.getCdnUrl();
        capa.setName(capaRequest.getName());
        capa.setPCapaId(capaRequest.getPCapaId());
        capa.setRankNum(capaRequest.getRankNum());
        capa.setIconUrl(systemAttachmentService.clearPrefix(capaRequest.getIconUrl(), cdnUrl));
        capa.setRiseImgUrl(systemAttachmentService.clearPrefix(capaRequest.getRiseImgUrl(), cdnUrl));
        capa.setShareImgUrl(systemAttachmentService.clearPrefix(capaRequest.getShareImgUrl(), cdnUrl));
        capaService.updateById(capa);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:detail')")
    @ApiOperation(value = "用户等级详情")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<Capa> detail(@PathVariable(value = "id") Long id) {
        return CommonResult.success(capaService.getById(id));
    }

    @ApiOperation(value = "等级列表")
    @GetMapping(value = "/list")
    public CommonResult<List<Capa>> list() {
        return CommonResult.success(capaService.list());
    }

    @ApiOperation(value = "升级条件")
    @GetMapping(value = "/condition/list")
    public CommonResult<JSONObject> conditionList() {
        JSONObject json = new JSONObject();
        List<ConditionEnum> list = ConditionEnum.getCapaList().stream().filter(s -> s.getType().equals("等级")).collect(Collectors.toList());
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

    @ApiOperation(value = "升级条件")
    @GetMapping(value = "/condition/desList")
    public CommonResult<List<String>> desList() {
        return CommonResult.success(ConditionEnum.getCapaList().stream().filter(s->s.getType().equals("等级")).map(ConditionEnum::getDescription).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('capa:condition:save')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "等级升级条件保存")
    @ApiOperation(value = "升级条件保存")
    @PostMapping(value = "/condition/save")
    public CommonResult conditionSave(@RequestBody @Validated RiseConditionRequest request) {
        capaService.saveRiseCondition(request);
        return CommonResult.success();
    }
}
