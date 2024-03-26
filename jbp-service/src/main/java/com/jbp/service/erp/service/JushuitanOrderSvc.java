//package com.jbp.service.erp.service;
//
//import com.Jwebmall.erp.entity.JushuitanConfig;
//import com.Jwebmall.erp.manager.JushuitanConfigMng;
//import com.Jwebmall.goods.entity.Goods;
//import com.Jwebmall.goods.manager.GoodsMng;
//import com.Jwebmall.order.entity.OrderGoods;
//import com.Jwebmall.order.entity.Orders;
//import com.Jwebmall.order.manager.OrderGoodsMng;
//import com.Jwebmall.order.manager.OrdersMng;
//import com.Jwebmall.tools.OrderUtils;
//import com.Jwebmall.user.entity.User;
//import com.Jwebmall.user.manager.UserMng;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONException;
//import com.alibaba.fastjson.JSONObject;
//import com.common.jdbc.template.TxMng;
//import com.common.util.ArrayUtils;
//import com.common.util.DateTimeUtils;
//import com.common.util.StringUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//public class JushuitanOrderSvc {
//
//	public void sync(String... orderSn) {
//		JSONArray error = new JSONArray();
//		JushuitanConfig jushuitanConfig = jushuitanConfigMng.def();
//		if (jushuitanConfig == null || StringUtils.isBlank(jushuitanConfig.getAccessToken())) {
//			return;
//		}
//		List<Orders> orders = ordersMng.getWaitShip();
//		for (Orders o : orders) {
//			if (org.apache.commons.lang3.StringUtils.contains(o.getPlatformMsg(), "发货已经同步聚水潭")) {
//				continue;
//			}
//			JSONObject jsonObject = new JSONObject();
//			{
//				if (!org.apache.commons.lang3.ArrayUtils.isEmpty(orderSn)
//						&& !org.apache.commons.lang3.ArrayUtils.contains(orderSn, o.getOrderSn())) {
//					continue;
//				}
//				User user = userMng.get(o.getUserId());
//				jsonObject.put("shop_id", Integer.parseInt(jushuitanConfig.getShopId()));
//				jsonObject.put("so_id", o.getOrderSn());
//				jsonObject.put("order_date",
//						DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
//								DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
//				jsonObject.put("shop_status", "WAIT_SELLER_SEND_GOODS");
//				// 如果已经退款，则状态变更
//				if (OrderUtils.isRefundConfirmStatus(o) || OrderUtils.isRefundStatus(o)
//						|| OrderUtils.isShipRefundStatus(o)) {
//					jsonObject.put("shop_status", "TRADE_CLOSED");
//				}
//				jsonObject.put("shop_buyer_id", user.getUsername() + "/" + user.getMobile());
//				String address = o.getAddress();
//				String[] addressList = StringUtils.split(address, " ");
//				try {
//					jsonObject.put("receiver_state", addressList[0]);
//					jsonObject.put("receiver_city", addressList[1]);
//					jsonObject.put("receiver_district", addressList[2]);
//					jsonObject.put("receiver_address",
//							addressList.length > 3
//									? StringUtils.join(ArrayUtils.subarray(addressList, 3, addressList.length))
//									: StringUtils.join(ArrayUtils.subarray(addressList, 2, addressList.length)));
//				} catch (Exception e) {
//					log.error(e.getMessage(), address);
//				}
//				jsonObject.put("receiver_name", o.getReceiveName());
//				jsonObject.put("receiver_phone", o.getMobile());
//				jsonObject.put("pay_amount", o.getPayPrice());
//				jsonObject.put("freight", o.getFreightPrice());
//				jsonObject.put("freight", o.getFreightPrice());
//				JSONObject mark = isJSONValidate(o.getMark());
//				if (mark != null) {
//					jsonObject.put("buyer_message", mark.getString("remark"));
//				} else {
//					jsonObject.put("buyer_message", o.getMark());
//				}
//				JSONArray items = new JSONArray();
//
//				List<OrderGoods> goods = ordreGoodsMng.getOrdersId(o.getId());
//				for (OrderGoods g : goods) {
//					Goods good = goodsMng.get(g.getGoodsId());
//					JSONObject item = new JSONObject();
//					item.put("sku_id", g.getSpecificationsNumCode());
//					item.put("shop_sku_id", g.getGoodsSn());
//					item.put("amount", g.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
//					item.put("base_price", good.getRetailPrice());
//					item.put("qty", g.getNumber());
//					item.put("name", g.getGoodsName());
//					item.put("outer_oi_id", g.getId().toString());
//					items.add(item);
//				}
//				jsonObject.put("items", items);
//
//				JSONObject pay = new JSONObject();
//				pay.put("outer_pay_id",
//						StringUtils.isBlank(o.getPayId())
//								? (StringUtils.isBlank(o.getPayOrderSn()) ? o.getOrderSn() : o.getPayOrderSn())
//								: o.getPayId());
//				pay.put("pay_date", DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
//						DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
//				pay.put("payment", "线上支付");
//				pay.put("seller_account", o.getUserId().toString());
//				pay.put("buyer_account", user.getParentId() == null ? "-1" : user.getParentId().toString());
//				pay.put("amount", o.getPayPrice());
//				jsonObject.put("pay", pay);
//			}
//			{
//				try {
//					TimeUnit.MILLISECONDS.sleep(300);
//				} catch (Exception e) {
//				}
//			}
//			JSONArray upload = new JSONArray();
//			upload.add(jsonObject);
//			try {
//				{
//					log.info("upload:{}", upload.toJSONString());
//				}
//				callSvc.orderUpload(upload);
//				o.setPlatformMsg((StringUtils.isBlank(o.getPlatformMsg()) ? "" : o.getPlatformMsg()) + " > 发货已经同步聚水潭");
//				ordersMng.updateByUpdater(o);
//			} catch (Exception e) {
//				error.add(o.getOrderSn());
//				log.error(e.getMessage(), e);
//			}
//		}
//		if (!error.isEmpty()) {
//			throw new RuntimeException(error.toJSONString() + " 订单同步失败");
//		}
//	}
//
//
//	/**
//	 * 水母逻辑当前页面才同步
//	 * @param orderSn
//	 */
//	public void sync2(String orderSn) {
//		JSONArray error = new JSONArray();
//		JushuitanConfig jushuitanConfig = jushuitanConfigMng.def();
//		if (jushuitanConfig == null || StringUtils.isBlank(jushuitanConfig.getAccessToken())) {
//			return;
//		}
//		List<Orders> orders = new ArrayList<>();
//
//		JSONArray jsonArray =  JSONArray.parseArray(orderSn);
//		for (Object object : jsonArray) {
//			JSONObject jsonObject = (JSONObject) object;
//			String orderNo = jsonObject.getString("orderNo");
//			orders.add(ordersMng.getByOrderSn(orderNo));
//		}
//
//		for (Orders o : orders) {
//			if (org.apache.commons.lang3.StringUtils.contains(o.getPlatformMsg(), "发货已经同步聚水潭")) {
//				continue;
//			}
//			JSONObject jsonObject = new JSONObject();
//			{
//
//				User user = userMng.get(o.getUserId());
//				jsonObject.put("shop_id", Integer.parseInt(jushuitanConfig.getShopId()));
//				jsonObject.put("so_id", o.getOrderSn());
//				jsonObject.put("order_date",
//						DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
//								DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
//				jsonObject.put("shop_status", "WAIT_SELLER_SEND_GOODS");
//				// 如果已经退款，则状态变更
//				if (OrderUtils.isRefundConfirmStatus(o) || OrderUtils.isRefundStatus(o)
//						|| OrderUtils.isShipRefundStatus(o)) {
//					jsonObject.put("shop_status", "TRADE_CLOSED");
//				}
//				jsonObject.put("shop_buyer_id", user.getUsername() + "/" + user.getMobile());
//				String address = o.getAddress();
//				String[] addressList = StringUtils.split(address, " ");
//				try {
//					jsonObject.put("receiver_state", addressList[0]);
//					jsonObject.put("receiver_city", addressList[1]);
//					jsonObject.put("receiver_district", addressList[2]);
//					jsonObject.put("receiver_address",
//							addressList.length > 3
//									? StringUtils.join(ArrayUtils.subarray(addressList, 3, addressList.length))
//									: StringUtils.join(ArrayUtils.subarray(addressList, 2, addressList.length)));
//				} catch (Exception e) {
//					log.error(e.getMessage(), address);
//				}
//				jsonObject.put("receiver_name", o.getReceiveName());
//				jsonObject.put("receiver_phone", o.getMobile());
//				jsonObject.put("pay_amount", o.getPayPrice());
//				jsonObject.put("freight", o.getFreightPrice());
//				jsonObject.put("freight", o.getFreightPrice());
//				JSONObject mark = isJSONValidate(o.getMark());
//				if (mark != null) {
//					jsonObject.put("buyer_message", mark.getString("remark"));
//				} else {
//					jsonObject.put("buyer_message", o.getMark());
//				}
//				JSONArray items = new JSONArray();
//
//				List<OrderGoods> goods = ordreGoodsMng.getOrdersId(o.getId());
//				for (OrderGoods g : goods) {
//					Goods good = goodsMng.get(g.getGoodsId());
//					JSONObject item = new JSONObject();
//					item.put("sku_id", g.getSpecificationsNumCode());
//					item.put("shop_sku_id", g.getGoodsSn());
//					item.put("amount", g.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
//					item.put("base_price", good.getRetailPrice());
//					item.put("qty", g.getNumber());
//					item.put("name", g.getGoodsName());
//					item.put("outer_oi_id", g.getId().toString());
//					items.add(item);
//				}
//				jsonObject.put("items", items);
//
//				JSONObject pay = new JSONObject();
//				pay.put("outer_pay_id",
//						StringUtils.isBlank(o.getPayId())
//								? (StringUtils.isBlank(o.getPayOrderSn()) ? o.getOrderSn() : o.getPayOrderSn())
//								: o.getPayId());
//				pay.put("pay_date", DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
//						DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
//				pay.put("payment", "线上支付");
//				pay.put("seller_account", o.getUserId().toString());
//				pay.put("buyer_account", user.getParentId() == null ? "-1" : user.getParentId().toString());
//				pay.put("amount", o.getPayPrice());
//				jsonObject.put("pay", pay);
//			}
//			{
//				try {
//					TimeUnit.MILLISECONDS.sleep(300);
//				} catch (Exception e) {
//				}
//			}
//			JSONArray upload = new JSONArray();
//			upload.add(jsonObject);
//			try {
//				{
//					log.info("upload:{}", upload.toJSONString());
//				}
//				callSvc.orderUpload(upload);
//				o.setPlatformMsg((StringUtils.isBlank(o.getPlatformMsg()) ? "" : o.getPlatformMsg()) + " > 发货已经同步聚水潭");
//				ordersMng.updateByUpdater(o);
//			} catch (Exception e) {
//				error.add(o.getOrderSn());
//				log.error(e.getMessage(), e);
//			}
//		}
//		if (!error.isEmpty()) {
//			throw new RuntimeException(error.toJSONString() + " 订单同步失败");
//		}
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	public static JSONObject isJSONValidate(String log) {
//		try {
//			return JSONObject.parseObject(log);
//		} catch (JSONException e) {
//			return null;
//		}
//	}
//
//	@Autowired
//	private TxMng txMng;
//	@Autowired
//	private JushuitanConfigMng jushuitanConfigMng;
//	@Autowired
//	private UserMng userMng;
//	@Autowired
//	private GoodsMng goodsMng;
//	@Autowired
//	private OrderGoodsMng ordreGoodsMng;
//	@Autowired
//	private OrdersMng ordersMng;
//	@Autowired
//	private JushuitanCallSvc callSvc;
//}
