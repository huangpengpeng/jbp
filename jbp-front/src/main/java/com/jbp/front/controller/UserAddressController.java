package com.jbp.front.controller;

import com.jbp.common.model.user.UserAddress;
import com.jbp.common.request.UserAddressRequest;
import com.jbp.common.request.WechatAddressImportRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户地址 前端控制器
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
@RequestMapping("api/front/address")
@Api(tags = "用户 -- 地址")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @ApiOperation(value = "用户地址分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<UserAddress>> getList() {
        return CommonResult.success(userAddressService.getAllList());
    }

    @ApiOperation(value = "用户地址添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<UserAddress> add(@RequestBody @Validated UserAddressRequest request) {
        if (userAddressService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户地址编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<UserAddress> edit(@RequestBody @Validated UserAddressRequest request) {
        if (userAddressService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "用户地址删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if (userAddressService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "地址详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAddress> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userAddressService.getDetail(id));
    }

    @ApiOperation(value = "获取默认地址")
    @RequestMapping(value = "/get/default", method = RequestMethod.GET)
    public CommonResult<UserAddress> getDefault() {
        return CommonResult.success(userAddressService.getDefault());
    }

    @ApiOperation(value = "设置默认地址")
    @RequestMapping(value = "/set/default/{id}", method = RequestMethod.POST)
    public CommonResult<UserAddress> setDefault(@PathVariable(value = "id") Integer id) {
        if (userAddressService.setDefault(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "微信地址导入")
    @RequestMapping(value = "/wechant/import", method = RequestMethod.POST)
    public CommonResult<UserAddress> wechatImport(@RequestBody @Validated WechatAddressImportRequest request) {
        if (userAddressService.wechatImport(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



