package com.jbp.admin.controller.publicly;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.jbp.common.annotation.CustomResponseAnnotation;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.model.express.Express;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.OrderSendRequest;
import com.jbp.common.request.SplitOrderSendDetailRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.erp.service.JushuitanCallSvc;
import com.jbp.service.erp.service.JushuitanOrderSvc;
import com.jbp.service.erp.tools.Constants;
import com.jbp.service.erp.tools.SignUtil;
import com.jbp.service.service.ExpressService;
import com.jbp.service.service.MerchantOrderService;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/publicly/jushuitan")
@Api(tags = "聚水潭控制器")
@CustomResponseAnnotation
@EncryptIgnore
public class AuthorizeAct {

	@ApiOperation(value = "erp授权")
	@RequestMapping(value = "/auth/go", method = RequestMethod.GET)
	public CommonResult<String> go() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("app_key",  environment.getProperty("jushuitan.appKey"));
	    params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		params.put("charset", Constants.CHARSET);
		String state = environment.getProperty("jushuitan.url");
		params.put("state", state);
		String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
		/**
		 * 拼接跳转地址
		 */
		String url = Constants.AUTH_URL.replace("[app_key]", (String)params.get("app_key"))
				.replace("[timestamp]", (String)params.get("timestamp")).replace("[charset]", (String)params.get("charset"))
				.replace("[sign]", sign).replace("[state]", state);
		return CommonResult.success(url);
	}





	@ApiOperation(value = "同步订单", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@RequestMapping(value = { "/sync" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public CommonResult<String> sync() {
		jushuitanOrderSvc.sync("");
		return CommonResult.success("同步成功");
	}




	@Autowired
	private OrderDetailService orderDetailService;
	@Autowired
	private MerchantOrderService merchantOrderService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private JushuitanCallSvc callSvc;
	@Autowired
	private Environment environment;
	@Autowired
	private ExpressService expressService;
	@Autowired
	private JushuitanOrderSvc jushuitanOrderSvc;

}
