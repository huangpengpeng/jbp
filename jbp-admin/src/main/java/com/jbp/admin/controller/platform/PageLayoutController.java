package com.jbp.admin.controller.platform;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.response.PageLayoutIndexResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.PageLayoutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 页面设计 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/page/layout")
@Api(tags = "页面布局管理")
public class PageLayoutController {

    @Autowired
    private PageLayoutService pageLayoutService;

    @PreAuthorize("hasAuthority('platform:page:layout:index')")
    @ApiOperation(value = "页面首页")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<PageLayoutIndexResponse> index() {
        return CommonResult.success(pageLayoutService.index());
    }

//    @PreAuthorize("hasAuthority('platform:page:layout:save')")
//    @ApiOperation(value = "页面首页保存(不建议调用)")
//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public CommonResult<Object> save(@RequestBody JSONObject jsonObject) {
//        if (pageLayoutService.save(jsonObject)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:banner:save')")
    @ApiOperation(value = "页面首页banner保存")
    @RequestMapping(value = "/index/banner/save", method = RequestMethod.POST)
    public CommonResult<Object> indexBannerSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.indexBannerSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:menu:save')")
    @ApiOperation(value = "页面首页menu保存")
    @RequestMapping(value = "/index/menu/save", method = RequestMethod.POST)
    public CommonResult<Object> indexMenuSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.indexMenuSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

//    @PreAuthorize("hasAuthority('platform:page:layout:index:news:save')")
//    @ApiOperation(value = "页面首页新闻保存")
//    @RequestMapping(value = "/index/news/save", method = RequestMethod.POST)
//    public CommonResult<Object> indexNewsSave(@RequestBody JSONObject jsonObject) {
//        if (pageLayoutService.indexNewsSave(jsonObject)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }

    @PreAuthorize("hasAuthority('platform:page:layout:index:banner:save')")
    @ApiOperation(value = "页面用户中心banner保存")
    @RequestMapping(value = "/user/banner/save", method = RequestMethod.POST)
    public CommonResult<Object> userBannerSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.userBannerSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:page:layout:user:menu:save')")
    @ApiOperation(value = "页面用户中心导航保存")
    @RequestMapping(value = "/user/menu/save", method = RequestMethod.POST)
    public CommonResult<Object> userMenuSave(@RequestBody JSONObject jsonObject) {
        if (pageLayoutService.userMenuSave(jsonObject)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

//    @PreAuthorize("hasAuthority('platform:page:layout:index:table:save')")
//    @ApiOperation(value = "页面用户中心商品table保存")
//    @RequestMapping(value = "/index/table/save", method = RequestMethod.POST)
//    public CommonResult<Object> indexTableSave(@RequestBody JSONObject jsonObject) {
//        if (pageLayoutService.indexTableSave(jsonObject)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }

//    @PreAuthorize("hasAuthority('platform:page:layout:category:config')")
//    @ApiOperation(value = "获取分类页配置")
//    @RequestMapping(value = "/category/config", method = RequestMethod.GET)
//    public CommonResult<Map<String, Object>> categoryConfig() {
//        return CommonResult.success(pageLayoutService.getCategoryConfig());
//    }
//
//    @PreAuthorize("hasAuthority('platform:page:layout:category:config:save')")
//    @ApiOperation(value = "分类页配置保存")
//    @RequestMapping(value = "/category/config/save", method = RequestMethod.POST)
//    public CommonResult<Object> categoryConfigSave(@RequestBody JSONObject jsonObject) {
//        if (pageLayoutService.categoryConfigSave(jsonObject)) {
//            return CommonResult.success();
//        }
//        return CommonResult.failed();
//    }
}
