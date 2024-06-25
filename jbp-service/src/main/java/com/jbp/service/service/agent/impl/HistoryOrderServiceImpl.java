package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.HistoryOrderEditRequest;
import com.jbp.common.request.HistoryOrderRequest;
import com.jbp.common.request.HistoryOrderShipRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.HistoryOrderDetailResponse;
import com.jbp.common.response.HistoryOrderResponse;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.erp.service.JushuitanCallSvc;
import com.jbp.service.service.agent.HistoryOrderService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class HistoryOrderServiceImpl implements HistoryOrderService {

    public  static  Map<String, String> SHOP_TOKEN = Maps.newConcurrentMap();

    public  static LinkedHashMap<String, String> DB_NAME_MAP = new LinkedHashMap<String, String>(){{
        put("wkp42271043176625", "12980053");
        put("tf138940740527575", "12980074");
        put("xcsmall", "14343407");
        put("jymall", "13990361");
    }};

    public  static  LinkedHashMap<String, String> SHOP_MAP = new LinkedHashMap<String, String>(){{
        put("12980053", "wkp42271043176625");
        put("12980074", "tf138940740527575");
        put("14343407", "xcsmall");
        put("13990361", "jymall");
    }};


    @Autowired
    private JushuitanCallSvc jushuitanCallSvc;

    @Override
    public PageInfo<HistoryOrderResponse> pageList(HistoryOrderRequest request, PageParamRequest pageParamRequest) {

        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        String sql = "  SELECT o.userId as uid, u.username as nickname, u.numberCode as account, " +
                "                o.orderSn as orderNo, o.status , o.payPrice, o.freightPrice, o.goodsPrice, " +
                "                o.receiveName, o.mobile as receiveMobile, o.province, o.city, o.area, o.address, " +
                "                o.shipName, o.shipSn, o.createTime, o.payTime, o.shipTime, o.id as orderId ,o.shipTime " +
                "        FROM " + request.getDbName() + ".orders AS o " +
                "        LEFT JOIN " + request.getDbName() + ".user AS u ON u.`id` = o.userId " +
                "        WHERE 1 =1 ";


        if (request.getUid() != null && request.getUid().intValue() > 0) {
            sql = sql + " and u.id = " + request.getUid();
        }
        if (StringUtils.isNotEmpty(request.getAccount())) {
            sql = sql + " and u.numberCode = '" + request.getAccount() + "'";
        }
        if (StringUtils.isNotEmpty(request.getOrderNo())) {
            sql = sql + " and o.orderSn = '" + request.getOrderNo() + "'";
        }
        if (StringUtils.isNotEmpty(request.getStatus())) {
            sql = sql + " and o.status in (" + request.getStatus() + " )";
        }
        if (request.getStartPayTime() != null) {
            sql = sql + " and o.payTime >= '" + DateTimeUtils.format(request.getStartPayTime(), "yyyy-MM-dd") + "'";
        }
        if (request.getEndPayTime() != null) {
            sql = sql + " and o.payTime <= '" + DateTimeUtils.format(request.getEndPayTime(), "yyyy-MM-dd") + "'";
        }
        sql = sql + " ORDER BY o.id DESC";
        List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
        if (CollectionUtils.isEmpty(maps)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<HistoryOrderResponse> list = JSONArray.parseArray(JSONArray.toJSONString(maps), HistoryOrderResponse.class);
        String orderIdStr = list.stream().map(s -> String.valueOf(s.getOrderId())).collect(Collectors.joining(","));
        List<Map<String, Object>> goodsList = SqlRunner.db().selectList("select * from " + request.getDbName() + ".ordergoods where orderId in({0})", orderIdStr);
        List<HistoryOrderDetailResponse> details = JSONArray.parseArray(JSONArray.toJSONString(goodsList), HistoryOrderDetailResponse.class);
        Map<Long, List<HistoryOrderDetailResponse>> detailsMap = FunctionUtil.valueMap(details, HistoryOrderDetailResponse::getOrderId);
        for (HistoryOrderResponse order : list) {
            order.setGoodsDetails(detailsMap.get(order.getOrderId()));
        }
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void edit(HistoryOrderEditRequest request) {
        String dbName = request.getDbName();
        if (StringUtils.isEmpty(dbName)) {
            return;
        }
        if (CollectionUtils.isEmpty(request.getOrderNoList())) {
            return;
        }
        if (request.getType() == null) {
            return;
        }
        StringBuffer b = new StringBuffer();
        int index = request.getOrderNoList().size() - 1;
        for (int i = 0; i < request.getOrderNoList().size(); i++) {
            b.append("'").append(request.getOrderNoList().get(i)).append("'");
            if (i != index) {
                b.append(",");
            }
        }
        String sql = "  SELECT o.userId as uid, u.username as nickname, u.numberCode as account,  " +
                "                o.orderSn as orderNo, o.status , o.payPrice, o.freightPrice, o.goodsPrice, " +
                "                o.receiveName, o.mobile as receiveMobile, o.province, o.city, o.area, o.address, " +
                "                o.shipName, o.shipSn, o.createTime, o.payTime, o.shipTime, o.id as orderId " +
                "        FROM " + request.getDbName() + ".orders AS o " +
                "        LEFT JOIN " + request.getDbName() + ".user AS u ON u.`id` = o.userId " +
                "        WHERE 1 =1 ";
        sql = sql + " and o.orderSn in(" + b.toString() + ") ";
        if (request.getType().intValue() == 0) {
            sql = sql + " and o.status=201 ";
        }
        List<Map<String, Object>> orderList = SqlRunner.db().selectList(sql);
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }
        // 历史订单
        List<HistoryOrderResponse> list = JSONArray.parseArray(JSONArray.toJSONString(orderList), HistoryOrderResponse.class);
        String orderIdStr = list.stream().map(s -> String.valueOf(s.getOrderId())).collect(Collectors.joining(","));
        List<Map<String, Object>> goodsList = SqlRunner.db().selectList("select * from " + request.getDbName() + ".ordergoods where orderId in({0})", orderIdStr);
        List<HistoryOrderDetailResponse> details = JSONArray.parseArray(JSONArray.toJSONString(goodsList), HistoryOrderDetailResponse.class);
        Map<Long, List<HistoryOrderDetailResponse>> detailsMap = FunctionUtil.valueMap(details, HistoryOrderDetailResponse::getOrderId);
        for (HistoryOrderResponse order : list) {
            order.setGoodsDetails(detailsMap.get(order.getOrderId()));
        }

        // 批量退款
        if (request.getType().intValue() == 1) {
            SqlRunner.db().update("update " + request.getDbName() + ".orders set status = {0}, refundTime={1} where id in(" + orderIdStr + ")", "203", DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            return;
        }
        // 发货
        if (request.getType().intValue() == 0) {
            for (HistoryOrderResponse order : list) {
                JSONObject jsonObject = new JSONObject();
                {
                    jsonObject.put("shop_id", Integer.parseInt( request.getShopId()));
                    jsonObject.put("so_id", order.getOrderNo());
                    jsonObject.put("order_date",
                            DateTimeUtils.format(order.getPayTime() == null ? order.getCreateTime() : order.getPayTime(),
                                    DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
                    jsonObject.put("shop_status", "WAIT_SELLER_SEND_GOODS");

                    jsonObject.put("shop_buyer_id", order.getNickname() + "/" + order.getReceiveMobile());
                    jsonObject.put("receiver_state", order.getProvince());
                    jsonObject.put("receiver_city", order.getCity());
                    jsonObject.put("receiver_district", order.getArea());
                    jsonObject.put("receiver_address", order.getAddress());

                    jsonObject.put("receiver_name", order.getReceiveName());
                    jsonObject.put("receiver_phone", order.getReceiveMobile());
                    jsonObject.put("pay_amount", order.getPayPrice());
                    jsonObject.put("freight", order.getFreightPrice());
                    jsonObject.put("buyer_message", "");
                    JSONArray items = new JSONArray();

                    List<HistoryOrderDetailResponse> orderDetailList = order.getGoodsDetails();
                    for (HistoryOrderDetailResponse g : orderDetailList) {
                        JSONObject item = new JSONObject();
                        item.put("sku_id", g.getGoodsSn());
                        item.put("shop_sku_id", g.getGoodsId().toString());
                        item.put("amount", g.getPrice().multiply(BigDecimal.valueOf(g.getNumber())).setScale(2, BigDecimal.ROUND_HALF_UP));
                        item.put("base_price", g.getPrice());
                        item.put("qty", g.getNumber());
                        item.put("name", g.getGoodsName());
                        item.put("outer_oi_id", g.getId().toString());
                        items.add(item);
                    }
                    jsonObject.put("items", items);

                    JSONObject pay = new JSONObject();
                    pay.put("outer_pay_id", order.getOrderNo());
                    pay.put("pay_date", DateTimeUtils.format(order.getPayTime() == null ? order.getCreateTime() : order.getPayTime(),
                            DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
                    pay.put("payment", "线上支付");
                    pay.put("seller_account", order.getUid().toString());
                    pay.put("buyer_account", "1");
                    pay.put("amount", order.getPayPrice());
                    jsonObject.put("pay", pay);
                }

                // 单订单推送
                JSONArray upload = new JSONArray();
                upload.add(jsonObject);
                try {
                    {
                        log.info("upload:{}", upload.toJSONString());
                    }
                    Set<String> keys = SHOP_MAP.keySet();
                    List<String> keyList = new ArrayList<>(keys);

                    jushuitanCallSvc.historyOrderUpload(upload,  keyList.indexOf(request.getShopId())+2 );
                    SqlRunner.db().update("update " + request.getDbName() + ".orders set platformMsg = {0} where id={1} ", "发货已经同步聚水潭", order.getOrderId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return;
        }
    }


    @Override
    public Boolean jstCall(String dbName, String orderSn, String shipName, String shipNo) {
        boolean update = SqlRunner.db().update("update " + dbName + ".orders set status = {0}, shipTime={1}, shipName={2}, shipSn={3} where orderSn ={4}",
                "301", DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN), shipName, shipNo, orderSn);
        return update;
    }

    @Override
    public void ship(HistoryOrderShipRequest request) {
          SqlRunner.db().update("update " + request.getDbName() + ".orders set status = {0}, shipTime={1}, shipName={2}, shipSn={3} where orderSn ={4}",
                "301", DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN), request.getShopNme(), request.getShipNo(), request.getOrderNo());

    }
}
