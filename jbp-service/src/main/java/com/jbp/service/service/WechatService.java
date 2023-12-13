package com.jbp.service.service;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.response.WeChatJsSdkConfigResponse;
import com.jbp.common.response.WechatOpenUploadResponse;
import com.jbp.common.response.WechatPublicShareResponse;
import com.jbp.common.vo.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 微信公用服务
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
public interface WechatService {

    /**
     * 获取公众号accessToken
     *
     * @return 公众号accessToken
     */
    String getPublicAccessToken();

    /**
     * 获取小程序accessToken
     *
     * @return 小程序accessToken
     */
    String getMiniAccessToken();

    /**
     * 获取开放平台access_token
     * 通过 code 获取
     * 公众号使用
     *
     * @return 开放平台accessToken对象
     */
    WeChatOauthToken getOauth2AccessToken(String code);

    /**
     * 获取开放平台用户信息
     *
     * @param accessToken 调用凭证
     * @param openid      普通用户的标识，对当前开发者帐号唯一
     *                    公众号使用
     * @return 开放平台用户信息对象
     */
    WeChatAuthorizeLoginUserInfoVo getSnsUserInfo(String accessToken, String openid);

    /**
     * 小程序登录凭证校验
     *
     * @return 小程序登录校验对象
     */
    WeChatMiniAuthorizeVo miniAuthCode(String code);

    /**
     * 获取微信公众号js配置参数
     *
     * @return WeChatJsSdkConfigResponse
     */
    WeChatJsSdkConfigResponse getPublicJsConfig(String url);

    /**
     * 生成小程序码
     *
     * @param jsonObject
     * @return 小程序码
     */
    String createQrCode(JSONObject jsonObject);

    /**
     * 微信预下单接口(统一下单)
     *
     * @param unifiedorderVo 预下单请求对象
     * @return 微信预下单返回对象
     */
    CreateOrderResponseVo payUnifiedorder(CreateOrderRequestVo unifiedorderVo);

    /**
     * 微信支付查询订单
     *
     * @return 支付订单查询结果
     */
    MyRecord payOrderQuery(Map<String, String> payVo);

    /**
     * 微信公众号发送模板消息
     *
     * @param templateMessage 模板消息对象
     * @return 是否发送成功
     */
    Boolean sendPublicTemplateMessage(TemplateMessageVo templateMessage);

    /**
     * 微信小程序发送订阅消息
     *
     * @param templateMessage 消息对象
     * @return 是否发送成功
     */
    Boolean sendMiniSubscribeMessage(ProgramTemplateMessageVo templateMessage);

    /**
     * 获取微信公众号自定义菜单配置
     * （使用本自定义菜单查询接口可以获取默认菜单和全部个性化菜单信息）
     *
     * @return 公众号自定义菜单
     */
    JSONObject getPublicCustomMenu();

    /**
     * 保存微信自定义菜单
     *
     * @param data 菜单数据，具体json格式参考微信开放平台
     * @return 创建结果
     */
    Boolean createPublicCustomMenu(String data);

    /**
     * 删除微信自定义菜单
     *
     * @return 删除结果
     */
    Boolean deletePublicCustomMenu();

    /**
     * 企业号上传其他类型永久素材
     * 获取url
     *
     * @param type 素材类型:图片（image）、语音（voice）、视频（video），普通文件(file)
     */
    String qyapiAddMaterialUrl(String type);

    /**
     * 微信申请退款
     *
     * @param wxRefundVo 微信申请退款对象
     * @param path       商户p12证书绝对路径
     * @return 申请退款结果对象
     */
    WxRefundResponseVo payRefund(WxRefundVo wxRefundVo, String path);

    /**
     * 微信开放平台上传素材
     *
     * @param file 文件
     * @param type 类型 图片（image）、语音（voice)
     * @return WechatOpenUploadResponse
     */
    WechatOpenUploadResponse openMediaUpload(MultipartFile file, String type);

    /**
     * 微信公众号分享配置
     */
    WechatPublicShareResponse getPublicShare();
}
