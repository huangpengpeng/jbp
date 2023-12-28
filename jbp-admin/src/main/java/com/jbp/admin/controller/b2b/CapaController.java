package com.jbp.admin.controller.b2b;

import com.jbp.common.model.b2b.Capa;
import com.jbp.common.model.merchant.MerchantAddress;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.merchant.MerchantAddressSearchRequest;
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
    @GetMapping(value = "/list")
    public CommonResult<CommonPage<Capa>> getList(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<Capa> result = CommonPage.restPage(capaService.page(pageParamRequest));
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('capa:add')")
    @ApiOperation(value = "用户等级新增")
    @PostMapping(value = "/add")
    public CommonResult<Object> add(@RequestBody Capa capa) {



        return CommonResult.success();
    }


}
