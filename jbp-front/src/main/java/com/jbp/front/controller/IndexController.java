package com.jbp.front.controller;


import com.github.pagehelper.PageInfo;
import com.jbp.front.service.IndexService;
import com.jbp.common.model.system.SystemConfig;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IndexInfoResponse;
import com.jbp.common.response.IndexMerchantResponse;
import com.jbp.common.response.ProductCommonResponse;
import com.jbp.common.result.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 首页控制器
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
@RequestMapping("api/front/index")
@Api(tags = "首页控制器")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getIndexInfo() {
        return CommonResult.success(indexService.getIndexInfo());
    }

    @ApiOperation(value = "首页商品列表")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="cid", value="一级商品分类id，全部传0", required = true)
    public CommonResult<PageInfo<ProductCommonResponse>> getProductList(@RequestParam(value = "cid") Integer cid,
                                                                        PageParamRequest pageParamRequest) {
        return CommonResult.success(indexService.findIndexProductList(cid, pageParamRequest));
    }

    @ApiOperation(value = "首页店铺列表")
    @RequestMapping(value = "/merchant/list", method = RequestMethod.GET)
    public CommonResult<List<IndexMerchantResponse>> getMerchantList() {
        return CommonResult.success(indexService.findIndexMerchantList());
    }

    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords() {
        return CommonResult.success(indexService.hotKeywords());
    }

    @ApiOperation(value = "颜色配置")
    @RequestMapping(value = "/color/config", method = RequestMethod.GET)
    public CommonResult<SystemConfig> getColorConfig() {
        return CommonResult.success(indexService.getColorConfig());
    }

    @ApiOperation(value = "全局本地图片域名")
    @RequestMapping(value = "/image/domain", method = RequestMethod.GET)
    public CommonResult<String> getImageDomain() {
        return CommonResult.success(indexService.getImageDomain(), "成功");
    }

    @ApiOperation(value = "版权图片")
    @RequestMapping(value = "/copyright/company/image", method = RequestMethod.GET)
    public CommonResult<Object> getCopyrightCompanyImage() {
        return CommonResult.success(indexService.getCopyrightCompanyImage(), "");
    }
}



