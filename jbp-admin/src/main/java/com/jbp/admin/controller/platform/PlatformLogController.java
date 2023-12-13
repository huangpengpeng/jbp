package com.jbp.admin.controller.platform;

import com.jbp.common.model.record.SensitiveMethodLog;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SensitiveMethodLogService;
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
 * 日志控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/log")
@Api(tags = "平台端敏感日志控制器")
public class PlatformLogController {

    @Autowired
    private SensitiveMethodLogService sensitiveMethodLogService;

    /**
     * 敏感操作日志列表
     */
    @PreAuthorize("hasAuthority('platform:log:sensitive:list')")
    @ApiOperation(value = "敏感操作日志分页列表")
    @RequestMapping(value = "/sensitive/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SensitiveMethodLog>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(sensitiveMethodLogService.getPlatformPageList(pageParamRequest)));
    }

}
