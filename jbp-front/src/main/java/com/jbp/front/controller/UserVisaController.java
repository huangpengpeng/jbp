package com.jbp.front.controller;

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
import com.fasc.open.api.v5_1.req.signtask.AddActorsTempInfo;
import com.fasc.open.api.v5_1.req.signtask.CreateWithTemplateReq;
import com.fasc.open.api.v5_1.req.signtask.SignTaskActorGetUrlReq;
import com.fasc.open.api.v5_1.req.signtask.TemplateSignConfigInfoReq;
import com.fasc.open.api.v5_1.res.service.AccessTokenRes;
import com.fasc.open.api.v5_1.res.signtask.CreateSignTaskRes;
import com.fasc.open.api.v5_1.res.signtask.SignTaskActorGetUrlRes;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.response.UserVisaResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserVisaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/front/user")
@Api(tags = "用户控制器")
public class UserVisaController {



	@Autowired
	private UserVisaService userVisaService;
	@Autowired
	private UserService userService;



	@ApiOperation(value = "获取用户是否签署法大大", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = "/getUserVisa", produces = MediaType.APPLICATION_JSON_VALUE)
	public CommonResult<Boolean> getUserVisa(ModelMap model) {

		UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid,userService.getUserId()));
		if(userVisa == null ){
			return CommonResult.success(true);
		}
		return CommonResult.success(userVisa.getVisa());
	}



	// 双生国际
	@ApiOperation(value = "法大大创建元气舱经营公约声明-签署任务", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = "/createWithTemplate", method = {
			RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public CommonResult<String> createWithTemplate(HttpServletRequest req, String realName, String idCard ,String signTemplateId,String signTaskSubject)
			throws Exception {

		signTemplateId = "1709904070308155948";
		signTaskSubject= "元气舱经营公约声明";
		Integer userId = userService.getUserId();
		UserVisa userVisa = userVisaService.getOne(new QueryWrapper<UserVisa>().lambda().eq(UserVisa::getUid,userService.getUserId()));
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
			createWithTemplateReq.setSignTaskSubject(signTaskSubject);
			createWithTemplateReq.setInitiator(openId);
			createWithTemplateReq.setSignTemplateId(signTemplateId);
			createWithTemplateReq.setAutoStart(true);
			createWithTemplateReq.setAutoFillFinalize(true);

			List<AddActorsTempInfo> list = new ArrayList<>();
			AddActorsTempInfo addActorsTempInfo = new AddActorsTempInfo();
			Actor actor = new Actor();
			actor.setActorId("乙方");
			actor.setActorType("person");
			actor.setActorName(realName);
			actor.setPermissions(permissions);
			actor.setIdentNameForMatch(realName);
			actor.setCertType("id_card");
			actor.setCertNoForMatch(idCard);
			actor.setClientUserId(userId.toString());
//			actor.setSendNotification(true);
//			List<String> notifyType =new ArrayList<>();
//			notifyType.add("finish");
//			actor.setNotifyType(notifyType);
//			actor.setNotifyAddress(req.getServletPath());


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


	private String getAccessToken(OpenApiClient openApiClient) throws ApiException {
		ServiceClient serviceClient = new ServiceClient(openApiClient);
		BaseRes<AccessTokenRes> res = serviceClient.getAccessToken();
		return res.getData().getAccessToken();
	}



	@ApiOperation(value = "法大大回调", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = "/user/userVisaCallback", produces = MediaType.APPLICATION_JSON_VALUE)
	public CommonResult<String>  getUserVisa(String bizContent) {

		if(bizContent == null){
			  return CommonResult.success("SUCCESS");
		}

		log.info("法大大回调 {}",bizContent);
		JSONObject jsonObject = JSONObject.parseObject(bizContent);

		if(jsonObject.getString("signTaskStatus") != null && jsonObject.getString("signTaskStatus").equals("task_finished")){
			UserVisaResponse userVisa = userVisaService.getVisaTask(jsonObject.getString("signTaskId"));
			if(userVisa != null) {
				String platfrom = "";
				if (userVisa.getPlatfrom().equals("sm")) {
					platfrom = "wkp42271043176625";
				} else if (userVisa.getPlatfrom().equals("yk")) {
					platfrom = "jymall";
				}
				userVisaService.updateVisa(userVisa.getId(), platfrom);
			}
		};

		return CommonResult.success("SUCCESS");
	}




}
