package com.jbp.admin.controller.platform;

import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.request.ProductDayRecordRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 平台端导出控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/export")
@Api(tags = "平台端导出控制器")
public class PlatformExportController {

    @Autowired
    private ExportService exportService;

    @PreAuthorize("hasAuthority('platform:export:order:shipment:excel')")
    @ApiOperation(value = "导出订单发货Excel")
    @RequestMapping(value = "/order/shipment/excel", method = RequestMethod.GET)
    public CommonResult<String> exportOrderShipment(@Validated OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        request.setSupplyName(systemAdmin.getSupplyName());
        return CommonResult.success(exportService.exportOrderShipment(request));
    }

    @PreAuthorize("hasAuthority('platform:export:order:excel')")
    @ApiOperation(value = "导出订单Excel")
    @RequestMapping(value = "/order/excel", method = RequestMethod.GET)
    public CommonResult<String> exportOrder(@Validated OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        request.setSupplyName(systemAdmin.getSupplyName());
        return CommonResult.success(exportService.exportOrder(request));
    }

    @PreAuthorize("hasAuthority('platform:export:product::statement')")
    @ApiOperation(value = "导出商品报表Excel")
    @RequestMapping(value = "/product/statement/excel/", method = RequestMethod.GET)
    public CommonResult<String> exportProductStatement(@Validated ProductDayRecordRequest request) {

        return CommonResult.success(exportService.exportProductStatement(request));
    }


}



