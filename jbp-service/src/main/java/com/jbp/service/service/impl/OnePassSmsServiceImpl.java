package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.sms.SmsTemplate;
import com.jbp.common.model.system.SystemNotification;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SmsApplyTempRequest;
import com.jbp.common.request.SmsModifySignRequest;
import com.jbp.common.utils.*;
import com.jbp.common.vo.*;
import com.jbp.service.service.*;
import com.jbp.service.util.OnePassUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 行为service实现类
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
@Service
public class OnePassSmsServiceImpl implements OnePassSmsService, SmsService {

    private static final Logger logger = LoggerFactory.getLogger(OnePassSmsServiceImpl.class);

    @Autowired
    private OnePassUtil onePassUtil;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private SystemNotificationService systemNotificationService;


    @Override
    public void checkValidateCode(String phone, String code) {
        String value = systemConfigService.getValueByKey("sms_code_valid_open");
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(value) && "0".equals(value)){
            return;
        }
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        //删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    /**
     * 修改签名
     */
    @Override
    public Boolean modifySign(SmsModifySignRequest request) {
        String token = getOnePassToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("sign", request.getSign());
        map.add("phone", request.getPhone());
        map.add("verify_code", request.getCode());
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_SMS_MODIFY_URI, map, header);
        return Boolean.TRUE;
    }

    /**
     * 短信模板
     *
     * @return OnePassSmsTempsListVo
     */
    @Override
    public OnePassSmsTempsListVo temps(PageParamRequest pageParamRequest) {
        String token = getOnePassToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("page", pageParamRequest.getPage());
        param.add("limit", pageParamRequest.getLimit());
        param.add("temp_type", 0);// 查询所有类型模板
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_TEMP_LIST_URI, param, header);
        JSONObject jsonObject = post.getJSONObject("data");
        logger.warn("短信模板响应JsonObject = {}", jsonObject);
        return post.getObject("data", OnePassSmsTempsListVo.class);
    }

    /**
     * 申请模板消息
     */
    @Override
    public Boolean applyTempMessage(SmsApplyTempRequest request) {
        String token = getOnePassToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("title", request.getTitle());
        param.add("content", request.getContent());
        param.add("type", request.getType());

        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_TEMP_APPLY_URI, param, header);
        return Boolean.TRUE;
    }

    /**
     * 发短信之前的校验
     */
    private void beforeSendMessage() {
        OnePassUserInfoVo userInfoVo = onePassService.info();
        OnePassUserSmsVo userSmsVo = userInfoVo.getSms();
        Integer open = userSmsVo.getOpen();
        if (!open.equals(1)) {
            logger.error("发送短信请先开通一号通账号服务");
            throw new CrmebException("发送短信请先开通一号通账号服务");
        }
        if (userSmsVo.getNum() <= 0) {
            logger.error("一号通账号服务余量不足");
            throw new CrmebException("一号通账号服务余量不足");
        }
    }

    /**
     * 发送公共验证码
     *
     * @param phone 手机号
     * @return Boolean
     * 1.校验后台是否配置一号通
     * 2.一号通是否剩余短信条数
     * 3.发送短信
     */
    @Override
    public Boolean sendCommonCode(String phone) {
        ValidateFormUtil.isPhone(phone, "手机号码错误");
//        beforeSendMessage();
//        DateTime dateTime = DateUtil.date();

//        String clientIp = RequestUtil.getClientIp();
//        beforeSendCommonCodeCheck(phone, null, dateTime);

        //获取短信验证码过期时间
        String codeExpireStr = systemConfigService.getValueByKey(SmsConstants.CONFIG_KEY_SMS_CODE_EXPIRE);
        if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
            codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
        }
        Integer code = CrmebUtil.randomCount(111111, 999999);
        Boolean aBoolean = false;
        String ytxOpenStatus = systemConfigService.getValueByKey("ytxOpenStatus");
        if (StringUtils.isNotEmpty(ytxOpenStatus) && "1".equals(ytxOpenStatus)) {
            String msgTempId = systemConfigService.getValueByKey("ytxMsgCodeTempId");
            aBoolean = push4Ytx(phone, msgTempId, new String[]{code.toString()});
        } else {
            HashMap<String, Object> justPram = new HashMap<>();
            justPram.put("code", code);
            justPram.put("time", codeExpireStr);
            aBoolean = push(phone, SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID, justPram);
        }
        if (!aBoolean) {
            throw new CrmebException("发送短信失败，请联系后台管理员");
        }
        // 将验证码存入redis
        redisUtil.set(SmsConstants.SMS_VALIDATE_PHONE + phone, code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
        redisUtil.set(SmsConstants.SMS_VALIDATE_PHONE_NUM + phone, 1, 60L);
        return aBoolean;
    }

    /**
     * 发送公共验证码前校验
     * @param phone 手机号
     * @param clientIp IP
     * @param dateTime 时间
     */
    private void beforeSendCommonCodeCheck(String phone, String clientIp, DateTime dateTime) {
        if (redisUtil.exists(SmsConstants.SMS_VALIDATE_PHONE_NUM + phone)) {
            throw new CrmebException("您的短信发送过于频繁，请稍后再试");
        }
//        Object phoneHourNumObject = redisUtil.get(StrUtil.format(SmsConstants.SMS_PHONE_HOUR_NUM, phone, dateTime.toDateStr() + dateTime.hour(true)));
//        if (ObjectUtil.isNotNull(phoneHourNumObject)) {
//            Integer phoneHourNum = (Integer) phoneHourNumObject;
//            if (phoneHourNum >= 7) {
//                throw new CrmebException(StrUtil.format("同一手机号1小时最多发送{}条短信，您的短信发送过于频繁，请稍后再试", 7));
//            }
//        }
//        Object phoneDayNumObject = redisUtil.get(StrUtil.format(SmsConstants.SMS_PHONE_DAY_NUM, phone, DateUtil.date().toDateStr()));
//        if (ObjectUtil.isNotNull(phoneDayNumObject)) {
//            Integer phoneDayNum = (Integer) phoneDayNumObject;
//            if (phoneDayNum >= 24) {
//                throw new CrmebException(StrUtil.format("同一手机号一天最多发送{}条短信，您的短信发送过于频繁，请明天再试", 24));
//            }
//        }
//        Object ipHourNumObject = redisUtil.get(StrUtil.format(SmsConstants.SMS_IP_HOUR_NUM, clientIp, dateTime.toDateStr() + dateTime.hour(true)));
//        if (ObjectUtil.isNotNull(ipHourNumObject)) {
//            Integer ipHourNum = (Integer) ipHourNumObject;
//            if (ipHourNum >= 10) {
//                throw new CrmebException(StrUtil.format("同一IP地址1小时最多发送{}条短信，您的短信发送过于频繁，请稍后再试", 10));
//            }
//        }
    }

    /**
     * 发送支付成功短信
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param payPrice 支付金额
     * @return Boolean
     */
    @Override
    public Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.PAY_SUCCESS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("pay_price", payPrice);
        map.put("order_id", orderNo);
        return push(phone, tempId, map);
    }

    /**
     * 发送订单发货提醒短信
     *
     * @param phone     手机号
     * @param nickName  用户昵称
     * @param storeName 商品名称
     * @param orderNo   订单编号
     */
    @Override
    public Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.DELIVER_GOODS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("nickname", nickName);
        map.put("store_name", storeName);
        map.put("order_id", orderNo);
        return push(phone, tempId, map);
    }

    /**
     * 发送入驻审核通过通知短信
     * @param phone 手机号
     * @param date 日期，yyyy-MM-dd
     * @param merName 商户名
     * @param merPhone 商户店长手机号
     * @param pwd 商户店长密码
     * @param siteName 站点名称
     * @return Boolean
     */
    @Override
    public Boolean sendMerchantAuditSuccessNotice(String phone, String date, String merName, String merPhone, String pwd, String siteName) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.AUDIT_SUCCESS_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("date", date);
        map.put("mer", merName);
        map.put("phone", merPhone);
        map.put("pwd", pwd);
        map.put("site_name", siteName);
        return push(phone, tempId, map);
    }

    /**
     * 发送入驻审核未通过通知短信
     * @param phone 手机号
     * @param date 日期，yyyy-MM-dd
     * @param merName 商户名
     * @param siteName 站点名称
     * @return Boolean
     */
    @Override
    public Boolean sendMerchantFileSuccessNotice(String phone, String date, String merName, String siteName) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.AUDIT_FAIL_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("date", date);
        map.put("mer", merName);
        map.put("site", siteName);
        return push(phone, tempId, map);
    }

    /**
     * 发送生日礼短信
     * @param phone 手机号
     * @param name 祝福内容
     * @return Boolean
     */
    @Override
    public Boolean sendBirthdayPresent(String phone, String name) {
        Integer tempId;
        try {
            beforeSendMessage();
            tempId = getSmsTempId(NotifyConstants.BIRTHDAY_PRESENT_MARK);
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
            return false;
        }
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("name", name);
        return push(phone, tempId, map);
    }

    private String getOnePassToken() {
        OnePassLoginVo loginVo = onePassService.getLoginVo();
        return onePassUtil.getToken(loginVo);
    }


    /**
     * 获取短信模板ID
     * @param mark 消息通知标识
     * @return tempId
     */
    private Integer getSmsTempId(String mark) {
        SystemNotification notification = systemNotificationService.getByMark(mark);
        if (ObjectUtil.isNull(notification)) {
            throw new CrmebException("未找到消息通知配置");
        }
        if (!notification.getIsSms().equals(NotifyConstants.SWITCH_OPEN)) {
            throw new CrmebException(notification.getDescription() + "未配置短信相关或已关闭");
        }
        SmsTemplate smsTemplate = smsTemplateService.getDetail(notification.getSmsId());
        return Integer.valueOf(smsTemplate.getTempId());
    }

    /**
     * 组装发送对象
     *
     * @param phone     手机号
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || msgTempId <= 0) {
            return false;
        }
        OnePassLoginVo loginVo = onePassService.getLoginVo();
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setUid(loginVo.getAccount());
        smsVo.setToken(loginVo.getSecret());
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendMessage(smsVo);
    }

    public static void main(String[] args) {
//        push4Ytx("15871898210", "2593107", new String[]{"123456"});
    }

    private  Boolean push4Ytx(String phone, String msgTempId, String[] datas) {
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String appId = systemConfigService.getValueByKey("ytxAppId");
        String accountSId = systemConfigService.getValueByKey("ytxAccountSId");
        String accountToken = systemConfigService.getValueByKey("ytxAuthToken");

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        HashMap<String, Object> result = sdk.sendTemplateSMS(phone, msgTempId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                logger.info("云通讯短信发送结果:" + key + " = " + object);
            }
        } else {
            logger.error("云通讯短信发送失败:" + "错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            //异常返回输出错误码和错误信息
        }
        return true;

    }
    /**
     * 发送短信
     *
     * @param sendSmsVo 短信参数
     */
    private Boolean sendMessage(SendSmsVo sendSmsVo) {
        String result;
        try {
            String token = getOnePassToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = JSONObject.parseObject(sendSmsVo.getParam());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.forEach((key, value) -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, key), value));
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
            checkResult(result);
        } catch (Exception e) {
            //接口请求异常，需要重新发送
            e.printStackTrace();
            logger.error("发送短信失败,{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 检测结构请求返回的数据
     *
     * @param result 接口返回的结果
     * @return JSONObject
     */
    private JSONObject checkResult(String result) {
        if (StrUtil.isBlank(result)) {
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("短信平台接口异常！");
        }
        if (SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))) {
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }
        return jsonObject;
    }



}
