package com.jbp.admin.controller.publicly;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.jbp.common.constants.OrderConstants;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class AuthorizeAct {
	
	
	@ApiOperation(value = "erp 授权", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/auth/go", produces = MediaType.APPLICATION_JSON_VALUE)
	public String go(HttpServletRequest request, ModelMap model) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("app_key",  environment.getProperty("jushuitan.appKey"));
	    params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		params.put("charset", Constants.CHARSET);
		String state = JushuitanCallSvc.getLocation(request).replace("go", "callApi");
		params.put("state", state);
		String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
		/**
		 * 拼接跳转地址
		 */
		String url = Constants.AUTH_URL.replace("[app_key]", (String)params.get("app_key"))
				.replace("[timestamp]", (String)params.get("timestamp")).replace("[charset]", (String)params.get("charset"))
				.replace("[sign]", sign).replace("[state]", state);
		return "redirect:"+url;
	}

	@ApiOperation(value = "erp 授权", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/auth/callApi", produces = MediaType.APPLICATION_JSON_VALUE)
	public String callApi(String code, HttpServletResponse response, ModelMap model) {
		{
			log.info("code:", code);
		}
		{
			callSvc.getAccessToken(code);
		}
		return  "授权成功";

	}
	

	@ApiOperation(value = "erp 聚水潭 消息推送")
	@RequestMapping(value = "/jushuitan/callApi1")
	public void erpcallApi1(String jsonObject,HttpServletResponse response) {


		if(StringUtils.isBlank(jsonObject)){
			try {
				response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		{
			log.info("jushuitancallApi:{} jsonObject:{}", JushuitanCallSvc.getLocation(JushuitanCallSvc.getNativeRequest()),
					jsonObject);
		}
			JSONObject jsonObject1  =JSONObject.parseObject(jsonObject);

		{
			String orderSn = jsonObject1.getString("so_id");
			String shipName = jsonObject1.getString("logistics_company");
			String shipSn = jsonObject1.getString("l_id");
			Order orders = orderService.getByOrderNo(orderSn);
			if (!orders.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_SHIPPING)) {
				throw new RuntimeException(orders.getOrderNo() + "状态错误");
			}
			Express express =  expressService.getByName(shipName);

			OrderSendRequest orderSendRequest = new OrderSendRequest();
			orderSendRequest.setOrderNo(orders.getOrderNo());
			orderSendRequest.setDeliveryType("express");
			orderSendRequest.setExpressCode(express.getCode());
			orderSendRequest.setIsSplit(false);
			orderSendRequest.setExpressNumber(shipSn);
			orderSendRequest.setExpressRecordType(1);
			MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orders.getOrderNo());
			orderSendRequest.setToName(merchantOrder.getRealName());
			orderSendRequest.setToTel(merchantOrder.getUserPhone());
			orderSendRequest.setToAddr(merchantOrder.getUserAddress());
			List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orders.getOrderNo());
			List<SplitOrderSendDetailRequest> list = new ArrayList<>();
			for (OrderDetail orderDetail : orderDetailList) {
				SplitOrderSendDetailRequest splitOrderSendDetailRequest = new SplitOrderSendDetailRequest();
				splitOrderSendDetailRequest.setNum(orderDetail.getPayNum());
				splitOrderSendDetailRequest.setOrderDetailId(orderDetail.getId());
				list.add(splitOrderSendDetailRequest);
			}
			orderSendRequest.setDetailList(list);
			orderSendRequest.setExpressTempId(shipSn);
			try {
				orderService.send(orderSendRequest);
			response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	@ApiOperation(value = "erp 聚水潭 消息推送", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/jushuitan/callApi2", produces = MediaType.APPLICATION_JSON_VALUE)
	public void erpcallApi2(String jsonObject,HttpServletResponse response) {
		{
			log.info("jushuitancallApi:{}",JushuitanCallSvc.getLocation(JushuitanCallSvc.getNativeRequest()));
		}


		if(StringUtils.isBlank(jsonObject)){
			try {
				response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JSONObject jsonObject1  =JSONObject.parseObject(jsonObject);

		String orderSn = jsonObject1.getString("so_id");
		String refundMark= jsonObject1.getString("remark");

		Order orders = orderService.getByOrderNo(orderSn);
		//退款
//		ordersMng.refund(orders.getId(), refundMark, "聚水潭", "聚水潭通知");
		try {
			response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@ApiOperation(value = "erp 聚水潭 消息推送", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/jushuitan/callApi3", produces = MediaType.APPLICATION_JSON_VALUE)
	public void erpcallApi3(HttpServletResponse response) {
		{
			log.info("jushuitancallApi:{}",JushuitanCallSvc.getLocation(JushuitanCallSvc.getNativeRequest()));
		}
		try {
			response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@ApiOperation(value = "erp 聚水潭 消息推送", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/jushuitan/callApi4", produces = MediaType.APPLICATION_JSON_VALUE)
	public void erpcallApi4( String jsonObject,HttpServletResponse response) {
		{
			log.info("jushuitancallApi:{}",JushuitanCallSvc.getLocation(JushuitanCallSvc.getNativeRequest()));
		}
		if(StringUtils.isBlank(jsonObject)){
			try {
				response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JSONObject jsonObject1  =JSONObject.parseObject(jsonObject);
		String orderSn = jsonObject1.getString("so_id");
		String refundMark= jsonObject1.getString("remark");

		//退款
//		Orders orders=ordersMng.getByOrderSn(orderSn);
//		ordersMng.refund(orders.getId(), refundMark, "聚水潭", "聚水潭通知");
		try {
			response.getWriter().write( "{\"code\":\"0\",\"msg\":\"执行成功\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
