package com.jbp.admin.controller.merchant;

import cn.hutool.json.JSONException;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.AdminProductListResponse;
import com.jbp.common.response.ProductInfoResponse;
import com.jbp.common.response.ProductTabsHeaderResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商户端商品控制器
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
@RequestMapping("api/admin/merchant/product")
@Api(tags = "商户端商品控制器") //配合swagger使用
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('merchant:product:page:list')")
    @ApiOperation(value = "商品分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<AdminProductListResponse>> getList(@Validated ProductSearchRequest request,
                                                                      @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(productService.getAdminList(request, pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "新增商品")
    @PreAuthorize("hasAuthority('merchant:product:save')")
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ProductAddRequest request) {
        if (productService.save(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除商品")
    @PreAuthorize("hasAuthority('merchant:product:delete')")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated ProductDeleteRequest request) {
        if (productService.deleteProduct(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "恢复回收站商品")
    @PreAuthorize("hasAuthority('merchant:product:restore')")
    @ApiOperation(value = "恢复回收站商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.POST)
    public CommonResult<String> restore(@PathVariable Integer id) {
        if (productService.restoreProduct(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "修改商品")
    @PreAuthorize("hasAuthority('merchant:product:update')")
    @ApiOperation(value = "商品修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductAddRequest ProductRequest) {
        if (productService.update(ProductRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(productService.getInfo(id));
   }

   @PreAuthorize("hasAuthority('merchant:product:tabs:headers')")
   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<ProductTabsHeaderResponse>> getTabsHeader() {
        return CommonResult.success(productService.getTabsHeader());
   }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品提审")
    @PreAuthorize("hasAuthority('merchant:product:submit:audit')")
    @ApiOperation(value = "商品提审")
    @RequestMapping(value = "/submit/audit/{id}", method = RequestMethod.POST)
    public CommonResult<String> submitAudit(@PathVariable Integer id) {
        if (productService.submitAudit(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "上架商品")
    @PreAuthorize("hasAuthority('merchant:product:up')")
    @ApiOperation(value = "商品上架")
    @RequestMapping(value = "/up/{id}", method = RequestMethod.POST)
    public CommonResult<String> up(@PathVariable Integer id) {
        if (productService.putOnShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "下架商品")
    @PreAuthorize("hasAuthority('merchant:product:down')")
    @ApiOperation(value = "商品下架")
    @RequestMapping(value = "/down/{id}", method = RequestMethod.POST)
    public CommonResult<String> down(@PathVariable Integer id) {
        if (productService.offShelf(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "快捷添加库存")
    @PreAuthorize("hasAuthority('merchant:product:quick:stock:add')")
    @ApiOperation(value = "快捷添加库存")
    @RequestMapping(value = "/quick/stock/add", method = RequestMethod.POST)
    public CommonResult<String> quickAddStock(@RequestBody @Validated ProductAddStockRequest request) {
        if (productService.quickAddStock(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "商品免审编辑")
    @PreAuthorize("hasAuthority('merchant:product:review:free:edit')")
    @ApiOperation(value = "商品免审编辑")
    @RequestMapping(value = "/review/free/edit", method = RequestMethod.POST)
    public CommonResult<String> reviewFreeEdit(@RequestBody @Validated ProductReviewFreeEditRequest request) {
        if (productService.reviewFreeEdit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }



    @PreAuthorize("hasAuthority('merchant:product:import:product')")
    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int",  required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<ProductRequest> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        ProductRequest productRequest = productService.importProductFromUrl(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @PreAuthorize("hasAuthority('admin:product:copy:config')")
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(productService.copyConfig());
    }


    @PreAuthorize("hasAuthority('merchant:product:copy:product')")
    @ApiOperation(value = "复制商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid CopyProductRequest request) {
        return CommonResult.success(productService.copyProduct(request.getUrl()));
    }
}



