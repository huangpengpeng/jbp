package com.jbp.front.controller;

import com.jbp.common.response.WeChatJsSdkConfigResponse;
import com.jbp.common.response.WechatPublicShareResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 文章控制器
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
@RequestMapping("api/front/wechat")
@Api(tags = "微信控制器")
public class WechatController {

    @Autowired
    private WechatService wechatService;

    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/get/public/js/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<WeChatJsSdkConfigResponse> getPublicJsConfig(@RequestParam(value = "url") String url){
        return CommonResult.success(wechatService.getPublicJsConfig(url));
    }

    @ApiOperation(value = "微信公众号分享配置")
    @RequestMapping(value = "/get/public/share", method = RequestMethod.GET)
    public CommonResult<WechatPublicShareResponse> getPublicShare() {
        return CommonResult.success(wechatService.getPublicShare());
    }
}



