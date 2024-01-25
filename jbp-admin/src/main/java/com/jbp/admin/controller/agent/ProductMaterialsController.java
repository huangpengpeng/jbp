package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.constants.SysConfigConstants;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
        Merchant merchant = merchantService.getByName(request.getMerName());
        if (ObjectUtil.isNull(merchant)) {
            if (ObjectUtil.isNull(request.getMerName()) || request.getMerName().equals("")) {
                merchant = new Merchant();
            } else {
                return CommonResult.failed("商户信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(productMaterialsService.pageList(merchant.getId(), request.getMaterialsName(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:product:materials:add')")
    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult add(@RequestBody ProductMaterialsAddRequest request) {
        SystemAdmin user = SecurityUtil.getLoginUserVo().getUser();
        Boolean ifPlatformAdd = user.getMerId()==0;
        Merchant merchant ;
        if(!ifPlatformAdd){
            merchant = merchantService.getByIdException(user.getMerId());
        } else {
            merchant = merchantService.getByIdException(Integer.valueOf(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_PLAT_DEFAULT_MER_ID)));
        }
        productMaterialsService.add(merchant.getId(),request.getBarCode(),request.getMaterialsName(),request.getMaterialsQuantity(),request.getMaterialsPrice(),request.getMaterialsCode());
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:product:materials:delete')")
    @GetMapping("/delete")
    @ApiOperation("删除")
    public CommonResult delete(Integer id) {
        productMaterialsService.removeById(id);
        return CommonResult.success();
    }
}
