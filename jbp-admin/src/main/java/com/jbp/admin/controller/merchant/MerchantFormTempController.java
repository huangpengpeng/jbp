package com.jbp.admin.controller.merchant;

import cn.hutool.core.util.ObjectUtil;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.system.SystemFormTemp;
import com.jbp.common.result.CommonResult;
import com.jbp.common.result.SystemFormResultCode;
import com.jbp.service.service.SystemFormTempService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;


/**
 * 表单模板 前端控制器 商户端
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/form/temp")
@Api(tags = "商户端设置表单模板")
public class MerchantFormTempController {

    @Autowired
    private SystemFormTempService systemFormTempService;

    @PreAuthorize("hasAuthority('merchant:config:form:info')")
    @ApiOperation(value = "表单组件详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemFormTemp> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(systemFormTempService.getByIdException(id));
   }

    @PreAuthorize("hasAuthority('merchant:system:form:name:info')")
    @ApiOperation(value = "通过名称查询详情")
    @RequestMapping(value = "/name/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="name", value="表单模板Name", required = true)
    public SystemFormTemp nameInfo(@NotBlank(message = "name不能为空") @RequestParam(value = "name") String name) {
        SystemFormTemp temp = systemFormTempService.getOneByName(name);
        if (ObjectUtil.isNull(temp)) {
            throw new CrmebException(SystemFormResultCode.FORM_TEMP_NOT_EXIST);
        }
        return temp;
    }
}



