package com.jbp.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.jbp.front.service.QrCodeService;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.QrCodeVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 二维码控制器
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
@RequestMapping("api/front/qrcode")
@Api(tags = "二维码控制器")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @ApiOperation(value = "获取微信二维码")
    @RequestMapping(value = "/get/wechat", method = RequestMethod.POST)
    public CommonResult<QrCodeVo> getWecahtQrCode(@RequestBody JSONObject data) {
        return CommonResult.success(qrCodeService.getWecahtQrCode(data));
    }

    @ApiOperation(value = "远程图片转base64")
    @RequestMapping(value = "/url/to/base64", method = RequestMethod.POST)
    public CommonResult<QrCodeVo> urlToBase64(@RequestParam String url) {
        return CommonResult.success(qrCodeService.urlToBase64(url));
    }

    @ApiOperation(value = "字符串转base64")
    @RequestMapping(value = "/str/to/base64", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "text", value = "文本", dataType = "String", required = true),
            @ApiImplicitParam(name = "width", value = "宽", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "height", value = "高", dataType = "Integer", required = true)
    })
    public CommonResult<QrCodeVo> strToBase64(@RequestParam String text, @RequestParam Integer width, @RequestParam Integer height) {
        return CommonResult.success(qrCodeService.strToBase64(text, width, height));
    }
}



