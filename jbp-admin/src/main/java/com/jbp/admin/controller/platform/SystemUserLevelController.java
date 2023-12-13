package com.jbp.admin.controller.platform;

import com.jbp.common.model.system.SystemUserLevel;
import com.jbp.common.request.SystemUserLevelRequest;
import com.jbp.common.request.SystemUserLevelUpdateShowRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemUserLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 系统会员等级控制器
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
@RequestMapping("api/admin/platform/system/user/level")
@Api(tags = "系统会员等级控制器")
public class SystemUserLevelController {

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @PreAuthorize("hasAuthority('platform:system:user:level:list')")
    @ApiOperation(value = "系统用户等级分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getList() {
        return CommonResult.success(systemUserLevelService.getList());
    }

    @PreAuthorize("hasAuthority('platform:system:user:level:save')")
    @ApiOperation(value = "新增系统用户等级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<Object> save(@RequestBody @Validated SystemUserLevelRequest request) {
        if (systemUserLevelService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:system:user:level:delete')")
    @ApiOperation(value = "删除系统用户等级")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (systemUserLevelService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:system:user:level:update')")
    @ApiOperation(value = "更新系统用户等级")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated SystemUserLevelRequest request) {
        if (systemUserLevelService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:system:user:level:use')")
    @ApiOperation(value = "使用/禁用系统用户等级")
    @RequestMapping(value = "/use", method = RequestMethod.POST)
    public CommonResult<Object> use(@RequestBody @Validated SystemUserLevelUpdateShowRequest request) {
        if (systemUserLevelService.updateShow(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



