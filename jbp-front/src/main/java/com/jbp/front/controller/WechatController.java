package com.jbp.front.controller;

import com.jbp.common.model.template.TemplateMessage;
import com.jbp.common.model.wechat.live.WechatLiveRoom;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.WeChatJsSdkConfigResponse;
import com.jbp.common.response.WechatPublicShareResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemNotificationService;
import com.jbp.service.service.WechatLiveRoomService;
import com.jbp.service.service.WechatService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 文章控制器
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
@RequestMapping("api/front/wechat")
@Api(tags = "微信控制器")
public class WechatController {

    @Autowired
    private WechatService wechatService;

    @Autowired
    private WechatLiveRoomService weChatLiveRoomService;

    @Autowired
    private SystemNotificationService systemNotificationService;

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

    /** 仅仅在直播间有待直播和正在直播的情况下才会有数据，所有数据都过期 小程序端不予暂时，此直播列表仅仅出现在小程序端
     * 直播间状态。101：直播中，102：未开始，103已结束，104禁播，105：暂停，106：异常，107：已过期
     * */
    @ApiOperation(value = "微信直播间列表")
    @RequestMapping(value = "/liveroom", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatLiveRoom>> getWechatLiveRoomForFront(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(weChatLiveRoomService.getLiveListForFront(pageParamRequest)));
    }

    @ApiOperation(value = "订阅消息模板列表")
    @RequestMapping(value = "/program/my/temp/list", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "支付之前：beforePay|支付成功：afterPay|申请退款：refundApply|充值之前：beforeRecharge|创建砍价：createBargain|参与拼团：pink|取消拼团：cancelPink")
    public CommonResult<List<TemplateMessage>> programMyTempList(@RequestParam(name = "type") String type){
        return CommonResult.success(systemNotificationService.getMiniTempList(type));
    }
}



