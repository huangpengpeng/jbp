package com.jbp.admin.controller.merchant;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.express.Express;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ExpressService;

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

import java.util.List;


/**
 * 商户端物流公司控制器
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
@RequestMapping("api/admin/merchant/express")
@Api(tags = "商户端物流公司控制器")
public class MerchantExpressController {

    @Autowired
    private ExpressService expressService;

    @ApiOperation(value = "查询全部物流公司")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型：normal-普通，elec-电子面单", required = true)
    public CommonResult<List<Express>> all(@RequestParam(value = "type") String type) {
        return CommonResult.success(expressService.findAll(type));
    }

    @ApiOperation(value = "查询物流公司面单模板")
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiImplicitParam(name = "com", value = "快递公司编号", required = true)
    public CommonResult<JSONObject> template(@RequestParam(value = "com") String com) {
        return CommonResult.success(expressService.template(com));
    }
}



