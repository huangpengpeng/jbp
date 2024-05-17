package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ProductMaterialsAddRequest;
import com.jbp.common.request.agent.ProductMaterialsRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.agent.ProductMaterialsService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/product/materials")
@Api(tags = "产品物料对应仓库的编码")
public class ProductMaterialsController {
    @Resource
    private ProductMaterialsService productMaterialsService;
    @Resource
    private MerchantService merchantService;
    @Autowired
    private SystemConfigService systemConfigService;

    @PreAuthorize("hasAuthority('agent:product:materials:page')")
    @GetMapping("/page")
    @ApiOperation("产品物料对应仓库列表")
    public CommonResult<CommonPage<ProductMaterials>> getList(ProductMaterialsRequest request, PageParamRequest pageParamRequest) {
        Integer merchantId = null;
        if (StringUtils.isNotEmpty(request.getMerName())) {
            Merchant merchant = merchantService.getByName(request.getMerName());
            if (merchant == null) {
                throw new CrmebException("商户名称信息错误");
            }
            merchantId = merchant.getId();
        }
        return CommonResult.success(CommonPage.restPage(productMaterialsService.pageList(merchantId, request.getMaterialsName(), request.getBarCode(), request.getSupplyName(), pageParamRequest)));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "物料新增")
    @PreAuthorize("hasAuthority('agent:product:materials:add')")
    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult add(@RequestBody @Validated ProductMaterialsAddRequest request) {
        SystemAdmin user = SecurityUtil.getLoginUserVo().getUser();
        Boolean ifPlatformAdd = user.getMerId() == 0;
        Merchant merchant;
        if (!ifPlatformAdd) {
            merchant = merchantService.getByIdException(user.getMerId());
        } else {
            merchant = merchantService.getByIdException(Integer.valueOf(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_PLAT_DEFAULT_MER_ID)));
        }
        productMaterialsService.add(merchant.getId(), request.getBarCode(), request.getMaterialsName(), request.getMaterialsQuantity(), request.getMaterialsPrice(), request.getMaterialsCode(), request.getSupplyName());
        return CommonResult.success();
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "物料删除")
    @PreAuthorize("hasAuthority('agent:product:materials:delete')")
    @GetMapping("/delete")
    @ApiOperation("删除")
    public CommonResult delete(Integer id) {
        productMaterialsService.removeById(id);
        return CommonResult.success();
    }
}
