package com.jbp.admin.controller.merchant;


import com.jbp.common.model.merchant.MerchantPrint;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.merchant.MerchantPrintRequest;
import com.jbp.common.request.merchant.MerchantPrintUpdateStatusRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.MerchantPrintService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/print")
@Api(tags = "商户端 - 小票打印") //配合swagger使用

public class MerchantPrintController {

    @Autowired
    private MerchantPrintService merchantPrintService;

    /**
     * 分页显示
     *
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-09-20
     */
    @PreAuthorize("hasAuthority('merchant:admin:print:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantPrint>> getList(@Validated PageParamRequest pageParamRequest) {
        CommonPage<MerchantPrint> merchantPrintCommonPage = CommonPage.restPage(merchantPrintService.getList(pageParamRequest));
        return CommonResult.success(merchantPrintCommonPage);
    }

    /**
     * 新增
     *
     * @param merchantPrintRequest 新增参数
     * @author dazongzi
     * @since 2023-09-20
     */
    @PreAuthorize("hasAuthority('merchant:admin:print:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated MerchantPrintRequest merchantPrintRequest) {
        MerchantPrint merchantPrint = new MerchantPrint();
        BeanUtils.copyProperties(merchantPrintRequest, merchantPrint);
        if (merchantPrintService.savePrintConfig(merchantPrint)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除打印机方法
     *
     * @param id Integer
     * @author dazongzi
     * @since 2023-09-20
     */
    @PreAuthorize("hasAuthority('merchant:admin:print:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (merchantPrintService.deleteById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改
     *
     * @param merchantPrintRequest 修改参数
     * @author dazongzi
     * @since 2023-09-20
     */
    @PreAuthorize("hasAuthority('merchant:admin:print:edit')")
    @ApiOperation(value = "编辑小票打印机配置")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantPrintRequest merchantPrintRequest) {
        MerchantPrint merchantPrint = new MerchantPrint();
        BeanUtils.copyProperties(merchantPrintRequest, merchantPrint);
        if (merchantPrintService.updatePrintConfig(merchantPrint)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     *
     * @param id Integer
     * @author dazongzi
     * @since 2023-09-20
     */
    @PreAuthorize("hasAuthority('merchant:admin:print:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<MerchantPrint> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(merchantPrintService.getPrintInfo(id));
    }

    @PreAuthorize("hasAuthority('merchant:admin:print:update:status')")
    @ApiOperation(value = "更新状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestBody @Validated MerchantPrintUpdateStatusRequest merchantPrintUpdateStatusRequest) {
        boolean updateStatusResult = merchantPrintService.updateStatus(merchantPrintUpdateStatusRequest.getId(),
                merchantPrintUpdateStatusRequest.getStatus());
        if (updateStatusResult) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



