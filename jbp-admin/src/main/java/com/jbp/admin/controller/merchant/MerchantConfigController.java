package com.jbp.admin.controller.merchant;

import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 配置表 前端控制器
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
@RequestMapping("api/admin/merchant/config")
@Api(tags = "商户端设置")
public class MerchantConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @PreAuthorize("hasAuthority('merchant:config:getuniq')")
    @ApiOperation(value = "表单配置根据key获取")
    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
    public CommonResult<Object> justGetUniq(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getValueByKey(key),"success");
    }

    @PreAuthorize("hasAuthority('merchant:config:get')")
    @ApiOperation(value = "根据key获取配置")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<List<SystemConfig>> getByKey(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getListByKey(key));
    }

}



