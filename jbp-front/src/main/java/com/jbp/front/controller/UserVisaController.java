package com.jbp.front.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasc.open.api.bean.base.BaseRes;
import com.fasc.open.api.bean.common.Actor;
import com.fasc.open.api.bean.common.OpenId;
import com.fasc.open.api.config.HttpConfig;
import com.fasc.open.api.exception.ApiException;
import com.fasc.open.api.v5_1.client.OpenApiClient;
import com.fasc.open.api.v5_1.client.ServiceClient;
import com.fasc.open.api.v5_1.client.SignTaskClient;
import com.fasc.open.api.v5_1.req.signtask.*;
import com.fasc.open.api.v5_1.res.service.AccessTokenRes;
import com.fasc.open.api.v5_1.res.signtask.CreateSignTaskRes;
import com.fasc.open.api.v5_1.res.signtask.SignTaskActorGetUrlRes;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserSkin;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.request.UserViseRequest;
import com.jbp.common.request.UserViseSaveRequest;
import com.jbp.common.request.YzhSignRequest;
import com.jbp.common.response.UserVisaResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.ChannelIdentityService;
import com.jbp.service.util.StringUtils;
import com.yunzhanghu.sdk.apiusersign.ApiUserSignServiceClient;
import com.yunzhanghu.sdk.apiusersign.domain.ApiUserSignContractRequest;
import com.yunzhanghu.sdk.apiusersign.domain.ApiUserSignContractResponse;
import com.yunzhanghu.sdk.apiusersign.domain.ApiUserSignRequest;
import com.yunzhanghu.sdk.apiusersign.domain.ApiUserSignResponse;
import com.yunzhanghu.sdk.base.YzhConfig;
import com.yunzhanghu.sdk.base.YzhRequest;
import com.yunzhanghu.sdk.base.YzhResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/front/user/visa")
@Api(tags = "用户控制器")
public class UserVisaController {


    @Autowired
    private UserVisaService userVisaService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVisaOrderService userVisaOrderService;
    @Autowired
    private ChannelIdentityService channelIdentityService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private Environment environment;
    @Autowired
    private UserSkinService userSkinService;


    @ApiOperation(value = "增加用户签署法大大", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult sava(String phone, String contract, String orderNo) {


        UserVisaOrder userVisaOrders = userVisaOrderService.getOne(new QueryWrapper<UserVisaOrder>().lambda().eq(UserVisaOrder::getUid, userService.getUserId()).eq(UserVisaOrder::getOrderNo, orderNo));

        if (userVisaOrders != null) {
            return CommonResult.success();
        }

        UserVisa userVisa = new UserVisa();
        userVisa.setContract(contract);
        userVisa.setUid(userService.getUserId());
        userVisa.setPhone(phone);
        userVisa.setVisa(false);
        userVisaService.save(userVisa);

        UserVisaOrder userVisaOrder = new UserVisaOrder();
        userVisaOrder.setStatus("待签订");
        userVisaOrder.setOrderNo(orderNo);
        userVisaOrder.setUid(userService.getUserId());
        userVisaOrder.setVisaId(userVisa.getId());
        userVisaOrderService.save(userVisaOrder);


        return CommonResult.success();
    }


    @ApiOperation(value = "获取用户是否签署法大大", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getUserVisa", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Boolean> getUserVisa(String contract) {

        UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid, userService.getUserId()).eq(UserVisa::getContract, contract));
        if (userVisa == null) {
            return CommonResult.success(false);
        }
        return CommonResult.success(!userVisa.getVisa());
    }


    @ApiOperation(value = "法大大创建-签署任务元气舱经营公约声明", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/createWithTemplate", method = {
            RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> createWithTemplate(@RequestBody UserViseSaveRequest userViseSaveRequest)
            throws Exception {


        Integer userId = userService.getUserId();
        UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid, userService.getUserId()).eq(UserVisa::getContract, userViseSaveRequest.getSignTaskSubject()));

        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = StringUtils.isEmpty(channelName) ? "平台" : channelName;
        ChannelIdentity channelIdentity = channelIdentityService.getByUser(userService.getUserId(), channelName);
        if (channelIdentity == null) {
            throw new CrmebException("未开户，请先去开户");
        }
        channelIdentity.setRealName(userViseSaveRequest.getRealName());
        channelIdentity.setIdCardNo(userViseSaveRequest.getIdCard());
        channelIdentityService.updateById(channelIdentity);


        OpenApiClient openApiClient = new OpenApiClient("00001068", "NADTNIHUU0DQSENC95LGM2GGZUJSFGOM", "https://api.fadada.com/api/v5/");
        try {
            //获取签章
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.setConnectTimeout(1000000);
            httpConfig.setReadTimeout(1000000);
            openApiClient.setHttpConfig(httpConfig);
            SignTaskClient signTaskClient = new SignTaskClient(openApiClient);

            String token = getAccessToken(openApiClient);

            List<String> permissions = new ArrayList<>();
            permissions.add("sign");

            CreateWithTemplateReq createWithTemplateReq = new CreateWithTemplateReq();
            createWithTemplateReq.setAccessToken(token);
            OpenId openId = new OpenId();
            openId.setIdType("corp");
            openId.setOpenId("d9557276be92474a905d99a818182209");
            createWithTemplateReq.setSignTaskSubject(userViseSaveRequest.getSignTaskSubject());
            createWithTemplateReq.setInitiator(openId);
            createWithTemplateReq.setSignTemplateId(userViseSaveRequest.getSignTemplateId());
            createWithTemplateReq.setAutoStart(true);
            createWithTemplateReq.setAutoFillFinalize(true);

            List<AddActorsTempInfo> list = new ArrayList<>();
            AddActorsTempInfo addActorsTempInfo = new AddActorsTempInfo();
            Actor actor = new Actor();
            actor.setActorId("乙方");
            actor.setActorType("person");
            actor.setActorName(userViseSaveRequest.getRealName());
            actor.setPermissions(permissions);
            actor.setIdentNameForMatch(userViseSaveRequest.getRealName());
            actor.setCertType("id_card");
            actor.setCertNoForMatch(userViseSaveRequest.getIdCard());
            actor.setClientUserId(userId.toString());

            addActorsTempInfo.setActor(actor);

            TemplateSignConfigInfoReq templateSignConfigInfoReq = new TemplateSignConfigInfoReq();
            List<String> verifyMethods = new ArrayList<>();
            verifyMethods.add("audio_video");
            templateSignConfigInfoReq.setVerifyMethods(verifyMethods);
            templateSignConfigInfoReq.setSignerSignMethod("ai_hand_write");

            List<String> audioVideoInfo = new ArrayList<>();
            audioVideoInfo.add("正在签署《元气舱经营公约》。您是否已全面理解及知悉元气舱不涉及任何疾病治疗功能，并承诺将遵守《广告法》宣传元气舱，确保真实、规范宣传，不对外宣传任何疾病预防和治疗效果");
            audioVideoInfo.add("您是否承诺在推广元气舱使用时，将先全面了解客户的身体情况，留下客户身体自测表，并全面、完整告知客户使用元气舱的禁忌和秘现反应的症状。如因您未了解客户情况，致使禁忌症人群使用元气舱导致的问题由您承担全部责任");
            audioVideoInfo.add("您是否知晓元气舱禁忌人群包括但不限于：危重疾病急性发作期患者 ，孕妇 ，重度精神病患者 ，大型手术后30天内 ，剖腹产产妇30天内 ，创口大出血患者 ，扭挫伤 48 小时内 ，重度高血压未服药控制者 ，支架、起搏器、心脏金属膜安装者 ，崩漏 ，传染病携带者");
            audioVideoInfo.add("您是否知晓元气舱禁忌人群包括但不限于：脏器血管瘤患者 ，心脏病患者 ，脑梗患者 ，脑出血后遗症患者，心脑血管疾病如脑梗患者，心梗患者 ，空腹 4小时以上或饭后半小时内，酒醉者 ，高烧不退患者 ，年龄超过75周岁体质虚弱人群");
            audioVideoInfo.add("您是否已仔细阅读上述文件确认内容无误，知悉电子签名与手写签名或盖章具有同等的法律效力并同意以电子签名形式签署该文件");

            templateSignConfigInfoReq.setAudioVideoInfo(audioVideoInfo);

            addActorsTempInfo.setSignConfigInfo(templateSignConfigInfoReq);

            list.add(addActorsTempInfo);
            createWithTemplateReq.setActors(list);

            log.info("法大大请求：{}", JSONObject.toJSONString(createWithTemplateReq));

            //创建签署任务
            BaseRes<CreateSignTaskRes> baseRes = signTaskClient.createWithTemplate(createWithTemplateReq);
            if (!baseRes.getCode().equals("100000")) {
                throw new RuntimeException(baseRes.getMsg());
            }

            userVisa.setTaskId(baseRes.getData().getSignTaskId());
            userVisaService.updateById(userVisa);

            //获取签署链接
            SignTaskActorGetUrlReq signTaskActorGetUrlReq = new SignTaskActorGetUrlReq();
            signTaskActorGetUrlReq.setAccessToken(token);
            signTaskActorGetUrlReq.setSignTaskId(baseRes.getData().getSignTaskId());
            signTaskActorGetUrlReq.setActorId("乙方");
            signTaskActorGetUrlReq.setRedirectMiniAppUrl("/pages/index/index");

            log.info("法大大请求2：{}", JSONObject.toJSONString(signTaskActorGetUrlReq));

            BaseRes<SignTaskActorGetUrlRes> signTaskActorGetUrlResBaseRes = signTaskClient.signTaskActorGetUrl(signTaskActorGetUrlReq);
            if (!signTaskActorGetUrlResBaseRes.getCode().equals("100000")) {
                throw new RuntimeException(signTaskActorGetUrlResBaseRes.getMsg());
            }

            log.info("法大大响应：{}", JSONObject.toJSONString(signTaskActorGetUrlResBaseRes.getData().getActorSignTaskEmbedUrl()));

            return CommonResult.success(signTaskActorGetUrlResBaseRes.getData().getActorSignTaskEmbedUrl());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return CommonResult.success();
    }


    @ApiOperation(value = "法大大创建-签署任务生物共振元气芯片舱机租赁合同", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/createWithTemplate2", method = {
            RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> createWithTemplate2(@RequestBody UserViseSaveRequest userViseSaveRequest)
            throws Exception {


        Integer userId = userService.getUserId();
        String channelName = systemConfigService.getValueByKey("pay_channel_name");
        channelName = StringUtils.isEmpty(channelName) ? "平台" : channelName;
        UserVisaOrder userVisaOrders = userVisaOrderService.getOne(new QueryWrapper<UserVisaOrder>().lambda().eq(UserVisaOrder::getUid, userService.getUserId()).eq(UserVisaOrder::getOrderNo, userViseSaveRequest.getOrderNo()));
        UserVisa userVisa = userVisaService.getById(userVisaOrders.getVisaId());

        ChannelIdentity channelIdentity = channelIdentityService.getByUser(userService.getUserId(), channelName);
        if (channelIdentity == null) {
            throw new CrmebException("未开户，请先去开户");
        }
        channelIdentity.setRealName(userViseSaveRequest.getRealName());
        channelIdentity.setIdCardNo(userViseSaveRequest.getIdCard());
        channelIdentityService.updateById(channelIdentity);

        OpenApiClient openApiClient = new OpenApiClient("00001068", "NADTNIHUU0DQSENC95LGM2GGZUJSFGOM", "https://api.fadada.com/api/v5/");
        try {
            //获取签章
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.setConnectTimeout(1000000);
            httpConfig.setReadTimeout(1000000);
            openApiClient.setHttpConfig(httpConfig);
            SignTaskClient signTaskClient = new SignTaskClient(openApiClient);

            String token = getAccessToken(openApiClient);

            List<String> permissions = new ArrayList<>();
            permissions.add("sign");

            CreateWithTemplateReq createWithTemplateReq = new CreateWithTemplateReq();
            createWithTemplateReq.setAccessToken(token);
            OpenId openId = new OpenId();
            openId.setIdType("corp");
            openId.setOpenId("d9557276be92474a905d99a818182209");
            createWithTemplateReq.setSignTaskSubject(userViseSaveRequest.getSignTaskSubject());
            createWithTemplateReq.setInitiator(openId);
            createWithTemplateReq.setSignTemplateId(userViseSaveRequest.getSignTemplateId());
            createWithTemplateReq.setAutoStart(true);
            createWithTemplateReq.setAutoFillFinalize(true);

            List<AddActorsTempInfo> list = new ArrayList<>();
            AddActorsTempInfo addActorsTempInfo = new AddActorsTempInfo();
            Actor actor = new Actor();
            actor.setActorId("乙方");
            actor.setActorType("person");
            actor.setActorName(userViseSaveRequest.getRealName());
            actor.setPermissions(permissions);
            actor.setIdentNameForMatch(userViseSaveRequest.getRealName());
            actor.setCertType("id_card");
            actor.setCertNoForMatch(userViseSaveRequest.getIdCard());
            actor.setClientUserId(userId.toString());

            addActorsTempInfo.setActor(actor);

            List<AddFillFieldInfo> fillFields = new ArrayList<>();
            AddFillFieldInfo fillField = new AddFillFieldInfo();
            fillField.setFieldDocId("32120153");
            fillField.setFieldId("5376155058");
            fillField.setFieldValue(userViseSaveRequest.getIdCard());
            fillFields.add(fillField);

            AddFillFieldInfo fillField2 = new AddFillFieldInfo();
            fillField2.setFieldDocId("32120153");
            fillField2.setFieldId("2345857392");
            fillField2.setFieldValue(userViseSaveRequest.getOrderNo());
            fillFields.add(fillField2);
            AddFillFieldInfo fillField3 = new AddFillFieldInfo();
            fillField3.setFieldDocId("32120153");
            fillField3.setFieldId("2636528450");
            fillField3.setFieldValue(userId.toString());
            fillFields.add(fillField3);

            addActorsTempInfo.setFillFields(fillFields);

            TemplateSignConfigInfoReq templateSignConfigInfoReq = new TemplateSignConfigInfoReq();
            List<String> verifyMethods = new ArrayList<>();
            verifyMethods.add("face");
            templateSignConfigInfoReq.setVerifyMethods(verifyMethods);
            templateSignConfigInfoReq.setSignerSignMethod("ai_hand_write");

            addActorsTempInfo.setSignConfigInfo(templateSignConfigInfoReq);

            list.add(addActorsTempInfo);
            createWithTemplateReq.setActors(list);

            log.info("法大大请求：{}", JSONObject.toJSONString(createWithTemplateReq));

            //创建签署任务
            BaseRes<CreateSignTaskRes> baseRes = signTaskClient.createWithTemplate(createWithTemplateReq);
            if (!baseRes.getCode().equals("100000")) {
                throw new RuntimeException(baseRes.getMsg());
            }

            userVisa.setTaskId(baseRes.getData().getSignTaskId());
            userVisaService.updateById(userVisa);

            //获取签署链接
            SignTaskActorGetUrlReq signTaskActorGetUrlReq = new SignTaskActorGetUrlReq();
            signTaskActorGetUrlReq.setAccessToken(token);
            signTaskActorGetUrlReq.setSignTaskId(baseRes.getData().getSignTaskId());
            signTaskActorGetUrlReq.setActorId("乙方");
            signTaskActorGetUrlReq.setRedirectMiniAppUrl("/pages/index/index");

            log.info("法大大请求2：{}", JSONObject.toJSONString(signTaskActorGetUrlReq));

            BaseRes<SignTaskActorGetUrlRes> signTaskActorGetUrlResBaseRes = signTaskClient.signTaskActorGetUrl(signTaskActorGetUrlReq);
            if (!signTaskActorGetUrlResBaseRes.getCode().equals("100000")) {
                throw new RuntimeException(signTaskActorGetUrlResBaseRes.getMsg());
            }

            log.info("法大大响应：{}", JSONObject.toJSONString(signTaskActorGetUrlResBaseRes.getData().getActorSignTaskEmbedUrl()));

            return CommonResult.success(signTaskActorGetUrlResBaseRes.getData().getActorSignTaskEmbedUrl());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return CommonResult.success();
    }


    private String getAccessToken(OpenApiClient openApiClient) throws ApiException {
        ServiceClient serviceClient = new ServiceClient(openApiClient);
        BaseRes<AccessTokenRes> res = serviceClient.getAccessToken();
        return res.getData().getAccessToken();
    }


    @EncryptIgnore
    @ApiOperation(value = "法大大回调", httpMethod = "POST")
    @ResponseBody
    @PostMapping(value = "/userVisaCallback")
    public String userVisaCallback(
            HttpServletRequest httpServletRequest) throws IOException {


        StringBuilder body = new StringBuilder();
        BufferedReader reader = httpServletRequest.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        JSONObject jsonObject = JSONObject.parseObject(URLDecoder.decode(URLDecoder.decode(body.toString(), "UTF-8"), "UTF-8").replaceAll("bizContent=", ""));

        log.info("法大大回调 {}", jsonObject);
        if (jsonObject == null) {
            return "success";
        }

        if (jsonObject.getString("signTaskId") == null) {
            return "success";
        }

        if (jsonObject.getString("signTaskStatus") != null && jsonObject.getString("signTaskStatus").equals("task_finished")) {
            UserVisaResponse userVisa = userVisaService.getVisaTask(jsonObject.getString("signTaskId"));
            if (userVisa != null) {
                String platfrom = "";
                if (userVisa.getPlatfrom().equals("sm")) {
                    platfrom = "sm";
                } else if (userVisa.getPlatfrom().equals("yk")) {
                    platfrom = "yk";
                }
                userVisaService.updateVisa(userVisa.getId(), platfrom);

                UserVisaOrder userVisaOrder = userVisaOrderService.getOne(new QueryWrapper<UserVisaOrder>().lambda().eq(UserVisaOrder::getVisaId, userVisa.getId()));
                if (userVisaOrder != null) {
                    userVisaOrder.setSignTime(DateTimeUtils.getNow());
                    userVisaOrderService.updateById(userVisaOrder);

                }

            }
        }
        ;

        return "success";
    }

//
//    public static void main(String[] args) {
//        OpenApiClient openApiClient = new OpenApiClient("00001068", "NADTNIHUU0DQSENC95LGM2GGZUJSFGOM", "https://api.fadada.com/api/v5/");
//        //获取签章
//        HttpConfig httpConfig = new HttpConfig();
//        httpConfig.setConnectTimeout(1000000);
//        httpConfig.setReadTimeout(1000000);
//        openApiClient.setHttpConfig(httpConfig);
//
//        OpenId openId = new OpenId();
//        openId.setIdType("corp");
//        openId.setOpenId("d9557276be92474a905d99a818182209");
//
//
//        BaseRes<AccessTokenRes> res2 = null;
//        try {
//            ServiceClient serviceClient = new ServiceClient(openApiClient);
//            res2 = serviceClient.getAccessToken();
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//
//
//        SignTemplateDetailReq signTemplateDetailReq = new SignTemplateDetailReq();
//        signTemplateDetailReq.setOwnerId(openId);
//        signTemplateDetailReq.setSignTemplateId("1713007108806169693");
//        signTemplateDetailReq.setAccessToken(res2.getData().getAccessToken());
//        TemplateClient serviceClient = new TemplateClient(openApiClient);
//        try {
//
//
//            BaseRes<SignTemplateDetailRes> res = serviceClient.getSignTemplateDetail(signTemplateDetailReq);
//            log.info("法大大响应：{}", JSONObject.toJSONString(res));
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//


    @ApiOperation(value = "获取用户是否签署云账户", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getUserYzhVisa", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Boolean> getUserYzhVisa() {

        UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid, userService.getUserId()).eq(UserVisa::getContract, "灵活用工云账户签约"));
        if (userVisa == null) {
            return CommonResult.success(false);
        }
        return CommonResult.success(true);
    }



    @ApiOperation(value = "灵活用工云账户-用户签约", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/yunzhanghuSign", method = {
            RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Boolean> yunzhanghuSign(@RequestBody YzhSignRequest yzhSignRequest)
            throws Exception {
        String publicSign = environment.getProperty("yunzhanghu.publicSign");
        String privateSign = environment.getProperty("yunzhanghu.privateSign");
        String dealerId = environment.getProperty("yunzhanghu.dealerId");
        String brokerId = environment.getProperty("yunzhanghu.brokerId");
        String yzhAppKey = environment.getProperty("yunzhanghu.yzhAppKey");
        String yzh3DesKey = environment.getProperty("yunzhanghu.yzh3DesKey");
// 配置基础信息
        YzhConfig config = new YzhConfig();
        config.setDealerId(dealerId);
        config.setBrokerId(brokerId);
        config.setYzhAppKey(yzhAppKey);
        config.setYzh3DesKey(yzh3DesKey);
        config.setYzhRsaPrivateKey(privateSign);
        config.setYzhRsaPublicKey(publicSign);
        config.setSignType(YzhConfig.SignType.RSA);
        config.setYzhUrl("https://api-service.yunzhanghu.com");
        ApiUserSignServiceClient client = new ApiUserSignServiceClient(config);
// 配置请求参数
        ApiUserSignRequest request = new ApiUserSignRequest();
        request.setDealerId(dealerId);
        request.setBrokerId(brokerId);
        request.setRealName(yzhSignRequest.getRealName());
        request.setIdCard(yzhSignRequest.getIdCard());
        request.setCardType("idcard");
        YzhResponse<ApiUserSignResponse> response = null;
        try {
            // request-id：请求ID，请求的唯一标识
            // 建议平台企业自定义 request-id，并记录在日志中，便于问题发现及排查
            // 如未自定义 request-id，将使用 SDK 中的 UUID 方法自动生成。注意：UUID 方法生成的 request-id 不能保证全局唯一，推荐自定义 request-id
            response = client.apiUserSign(YzhRequest.build("requestId", request));
            if (response.isSuccess()) {
                // 操作成功
                ApiUserSignResponse data = response.getData();

                UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid, userService.getUserId()).eq(UserVisa::getContract, "灵活用工云账户签约"));
                if(userVisa == null) {
                    userVisa = new UserVisa();
                    userVisa.setContract("灵活用工云账户签约");
                    userVisa.setUid(userService.getUserId());
                    userVisa.setVisa(true);
                    userVisaService.save(userVisa);
                }
                return CommonResult.success(true);
            } else {
                log.info("云账户响应：{}", response.getMessage());
            }
        } catch (Exception e) {
            // 发生异常
            e.printStackTrace();
        }

        return CommonResult.success(false);
    }


    @ApiOperation(value = "灵活用工云账户-协议URl", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/yunzhanghuUrl", method = {
            RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<ApiUserSignContractResponse> yunzhanghuUrl()
            throws Exception {
        String publicSign = environment.getProperty("yunzhanghu.publicSign");
        String privateSign = environment.getProperty("yunzhanghu.privateSign");
        String dealerId = environment.getProperty("yunzhanghu.dealerId");
        String brokerId = environment.getProperty("yunzhanghu.brokerId");
        String yzhAppKey = environment.getProperty("yunzhanghu.yzhAppKey");
        String yzh3DesKey = environment.getProperty("yunzhanghu.yzh3DesKey");
// 配置基础信息
        YzhConfig config = new YzhConfig();
        config.setDealerId(dealerId);
        config.setBrokerId(brokerId);
        config.setYzhAppKey(yzhAppKey);
        config.setYzh3DesKey(yzh3DesKey);
        config.setYzhRsaPrivateKey(privateSign);
        config.setYzhRsaPublicKey(publicSign);
        config.setSignType(YzhConfig.SignType.RSA);
        config.setYzhUrl("https://api-service.yunzhanghu.com");
        ApiUserSignServiceClient client = new ApiUserSignServiceClient(config);
        // 配置请求参数
        ApiUserSignContractRequest request = new ApiUserSignContractRequest();
        request.setDealerId(dealerId);
        request.setBrokerId(brokerId);
        YzhResponse<ApiUserSignContractResponse> response = null;
        try {
            // request-id：请求ID，请求的唯一标识
            // 建议平台企业自定义 request-id，并记录在日志中，便于问题发现及排查
            // 如未自定义 request-id，将使用 SDK 中的 UUID 方法自动生成。注意：UUID 方法生成的 request-id 不能保证全局唯一，推荐自定义 request-id
            response = client.apiUserSignContract(YzhRequest.build("requestId", request));
            if (response.isSuccess()) {
                // 操作成功
                ApiUserSignContractResponse data = response.getData();
                return CommonResult.success(data);
            }
        } catch (Exception e) {
            // 发生异常
            e.printStackTrace();
        }
        return CommonResult.success();
    }








    @ApiOperation(value = "悦人悦己-皮肤检测", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/skin", method = {
            RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<Boolean> skin() {

        User user = userService.getInfo();
        if (user == null) {
            return CommonResult.failed("获取当前用户信息失败！");
        }
        JSONObject data = new JSONObject();
        data.put("username", "useryryj");
        data.put("userpsw", "5fd490c08933");
        data.put("mobile", user.getPhone());

        String body = JSON.toJSONString(data);
        HttpRequest request = HttpRequest.post("https://fxftdev.114.fm/api/product/getUserReport");
        request.contentType("application/json");
        request.charset("utf-8");
        String response = request.body(body).send().bodyText();
        log.info(response);
        JSONObject result = JSON.parseObject(response);
        if (!result.get("code").equals(1) || result.getJSONArray("data").isEmpty()) {
            return CommonResult.success(false);
        }
        UserSkin userSkin = new UserSkin();
        userSkin.setUid(user.getId());
        userSkin.setSkinInfo(result.get("data").toString());
        userSkinService.save(userSkin);
        return CommonResult.success(true);
    }
}
