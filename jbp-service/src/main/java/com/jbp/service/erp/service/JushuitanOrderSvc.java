package com.jbp.service.erp.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.system.JushuitanConfig;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JushuitanOrderSvc {

    public void sync(String orderNo) {
        JSONArray error = new JSONArray();
        JushuitanConfig jushuitanConfig = jushuitanConfigService.def();
        if (jushuitanConfig == null || StringUtils.isBlank(jushuitanConfig.getAccessToken())) {
            return;
        }
        List<Order> orders = new ArrayList<>();
        if (StringUtils.isEmpty(orderNo)) {
            orders = orderService.getWaitPullList();
        } else {
            LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
            lqw.in(Order::getOrderNo, StrUtil.split(orderNo, ','));
            orders = orderService.list(lqw);
        }


        for (Order o : orders) {
            if (o.getIfPull()) {
                continue;
            }

            JSONObject jsonObject = new JSONObject();
            {

                User user = userService.getById(o.getUid());
                jsonObject.put("shop_id", Integer.parseInt(jushuitanConfig.getShopId()));
                jsonObject.put("so_id", o.getPlatOrderNo());
                jsonObject.put("order_date",
                        DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
                                DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
                jsonObject.put("shop_status", "WAIT_SELLER_SEND_GOODS");

                jsonObject.put("shop_buyer_id", user.getNickname() + "/" + user.getPhone());
                MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(o.getPlatOrderNo());
                jsonObject.put("receiver_state", merchantOrder.getProvince());
                jsonObject.put("receiver_city", merchantOrder.getCity());
                jsonObject.put("receiver_district", merchantOrder.getDistrict());
                jsonObject.put("receiver_address", merchantOrder.getStreet() + merchantOrder.getAddress());

                jsonObject.put("receiver_name", merchantOrder.getRealName());
                jsonObject.put("receiver_phone", merchantOrder.getUserPhone());
                jsonObject.put("pay_amount", o.getPayPrice());
                jsonObject.put("freight", o.getTotalPostage());
                JSONObject mark = isJSONValidate(merchantOrder.getUserRemark());
                jsonObject.put("buyer_message", merchantOrder.getUserRemark());
                JSONArray items = new JSONArray();

                List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(o.getPlatOrderNo());
                for (OrderDetail g : orderDetailList) {
                    JSONObject item = new JSONObject();
                    item.put("sku_id", g.getBarCode());
                    item.put("shop_sku_id", g.getProductId());
                    item.put("amount", g.getPayPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                    item.put("base_price", g.getPrice());
                    item.put("qty", g.getPayNum());
                    item.put("name", g.getProductName());
                    item.put("outer_oi_id", g.getId().toString());
                    items.add(item);
                }
                jsonObject.put("items", items);

                Integer pid = userInvitationService.getPid(user.getId());
                JSONObject pay = new JSONObject();
                pay.put("outer_pay_id", o.getPlatOrderNo());
                pay.put("pay_date", DateTimeUtils.format(o.getPayTime() == null ? o.getCreateTime() : o.getPayTime(),
                        DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
                pay.put("payment", "线上支付");
                pay.put("seller_account", o.getUid().toString());
                pay.put("buyer_account", pid == null ? "-1" : pid.toString());
                pay.put("amount", o.getPayPrice());
                jsonObject.put("pay", pay);
            }
            {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (Exception e) {
                }
            }
            JSONArray upload = new JSONArray();
            upload.add(jsonObject);
            try {
                {
                    log.info("upload:{}", upload.toJSONString());
                }
                callSvc.orderUpload(upload);
                o.setIfPull(true);
                orderService.updateById(o);
            } catch (Exception e) {
                error.add(o.getPlatOrderNo());
                log.error(e.getMessage(), e);
            }
        }
        if (!error.isEmpty()) {
            throw new RuntimeException(error.toJSONString() + " 订单同步失败");
        }
    }


    public static JSONObject isJSONValidate(String log) {
        try {
            return JSONObject.parseObject(log);
        } catch (JSONException e) {
            return null;
        }
    }


    @Autowired
    private JushuitanConfigService jushuitanConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private JushuitanCallSvc callSvc;

    @Autowired
    private MerchantOrderService merchantOrderService;

    @Autowired
    private UserInvitationService userInvitationService;
}
