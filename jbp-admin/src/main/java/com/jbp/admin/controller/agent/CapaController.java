package com.jbp.admin.controller.agent;

import cn.hutool.core.util.NumberUtil;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.b2b.CapaRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.CapaService;
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
@RequestMapping("api/admin/capa")
@Api(tags = "用户等级")
public class CapaController {

    @Resource
    private CapaService capaService;

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
        capa.setName(capaRequest.getName());
        capa.setPCapaId(capaRequest.getPCapaId());
        capa.setRankNum(capaRequest.getRankNum());
        capa.setIconUrl(capaRequest.getIconUrl());
        capa.setRiseImgUrl(capaRequest.getRiseImgUrl());
        capa.setShareImgUrl(capaRequest.getShareImgUrl());
        capaService.updateById(capa);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('capa:detail')")
    @ApiOperation(value = "用户等级编辑")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<Capa> detail(@PathVariable(value = "id") Long id) {
        return CommonResult.success(capaService.getById(id));
    }

    @ApiOperation(value = "等级列表")
    @GetMapping(value = "/list")
    public CommonResult<List<Capa>> list() {
        return CommonResult.success(capaService.list());
    }

}
