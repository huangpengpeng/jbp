package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ProductDayRecordRequest;
import com.jbp.common.request.ProductRepertoryRequest;
import com.jbp.common.request.agent.*;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ProductRepertoryService;
import com.jbp.service.service.UserService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/platform/product/repertory")
@Api(tags = "库存管理")
public class ProductRepertoryController {

    @Autowired
    private ProductRepertoryService productRepertoryService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:platform:product:repertory:page')")
    @GetMapping("/list")
    @ApiOperation("库存管理分页列表")
    public CommonResult<CommonPage<ProductRepertory>> page(ProductRepertorySearchRequest request, PageParamRequest pageParamRequest){
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(productRepertoryService.getList(uid,request.getNickname(),request.getProductNameOrCode(),pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:repertory:allot')")
    @PostMapping("/allot")
    @ApiOperation("调拨库存")
    public CommonResult<Boolean> allot(@RequestBody ProductRepertoryAllotRequest request){
        Integer fUid = null;
        if (StringUtils.isNotEmpty(request.getFromAccount())) {
            User user = userService.getByAccount(request.getFromAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("调拨账号信息错误");
            }
            fUid = user.getId();
        }
        Integer tUid = null;
        if (StringUtils.isNotEmpty(request.getToAccount())) {
            User user = userService.getByAccount(request.getToAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("接收账号信息错误");
            }
            tUid = user.getId();
        }
        return CommonResult.success(productRepertoryService.allot(fUid,tUid,request.getProductId(),request.getCount()));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:repertory:company')")
    @PostMapping("/company")
    @ApiOperation("公司调拨库存")
    public CommonResult<Boolean> company(@RequestBody @Validated ProductRepertoryCompanyRequest request){
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("调拨账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(productRepertoryService.company(uid,request.getProductId(),request.getCount(),request.getDescription()));
    }

    @GetMapping("/user/repertory")
    @ApiOperation("获取用户商品库存")
    public CommonResult<List<ProductRepertory>> getProduct(ProductRepertoryAllotSearchRequest request){
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(productRepertoryService.getUserRepertory(uid));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:repertory:export')")
    @ApiOperation(value = "导出库存管理列表Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<String> exportProductStatement(ProductRepertorySearchRequest request) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(productRepertoryService.export(uid,request.getNickname(),request.getProductNameOrCode()));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:repertory:edit')")
    @PostMapping("/edit")
    @ApiOperation("库存编辑")
    public CommonResult<Boolean> edit(@RequestBody @Validated ProductRepertoryEditRequest request){
        return CommonResult.success(productRepertoryService.edit(request.getId(),request.getCount(),request.getKind(),request.getDescription()));
    }












}
