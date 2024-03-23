
package com.jbp.admin.controller.merchant;



import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.merchant.MerchantAddress;
import com.jbp.common.request.merchant.MerchantAddressSaveRequest;
import com.jbp.common.request.merchant.MerchantAddressSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.MerchantAddressService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商户地址管理 前端控制器
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
@RequestMapping("api/admin/merchant/address")
@Api(tags = "商户地址管理控制器")
public class MerchantAddressController {

    @Autowired
    private MerchantAddressService addressService;

    @ApiOperation(value = "商户地址分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<MerchantAddress>> getList(@Validated MerchantAddressSearchRequest request) {
        return CommonResult.success(addressService.findList(request));
    }

    @PreAuthorize("hasAuthority('merchant:address:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商户地址")
    @ApiOperation(value = "新增商户地址")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<Object> save(@RequestBody MerchantAddressSaveRequest request) {
        if (addressService.add(request)) {
            return CommonResult.success().setMessage("新增商户地址成功");
        }
        return CommonResult.failed().setMessage("新增商户地址失败");
    }

    @PreAuthorize("hasAuthority('merchant:address:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商户地址")
    @ApiOperation(value = "删除商户地址")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<Object> delete(@PathVariable(value = "id") Integer id) {
        if (addressService.delete(id)) {
            return CommonResult.success().setMessage("删除商户地址成功");
        }
        return CommonResult.failed().setMessage("删除商户地址失败");
    }

    @PreAuthorize("hasAuthority('merchant:address:update')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商户地址")
    @ApiOperation(value = "修改商户地址")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody MerchantAddressSaveRequest request) {
        if (addressService.updateAddress(request)) {
            return CommonResult.success().setMessage("修改商户地址成功");
        }
        return CommonResult.failed().setMessage("修改商户地址失败");
    }

    @PreAuthorize("hasAuthority('merchant:address:set:default')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "设置商户默认地址")
    @ApiOperation(value = "设置商户默认地址")
    @RequestMapping(value = "/set/default/{id}", method = RequestMethod.POST)
    public CommonResult<Object> setDefault(@PathVariable(value = "id") Integer id) {
        if (addressService.setDefault(id)) {
            return CommonResult.success().setMessage("设置商户默认地址成功");
        }
        return CommonResult.failed().setMessage("设置商户默认地址失败");
    }

    @PreAuthorize("hasAuthority('merchant:address:update:show')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "设置商户地址开启状态")
    @ApiOperation(value = "设置商户地址开启状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShow(@PathVariable(value = "id") Integer id) {
        if (addressService.updateShow(id)) {
            return CommonResult.success().setMessage("设置商户地址开启状态成功");
        }
        return CommonResult.failed().setMessage("设置商户地址开启状态失败");
    }
}



