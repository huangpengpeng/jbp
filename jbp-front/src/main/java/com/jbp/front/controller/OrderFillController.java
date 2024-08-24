package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.OrderFillVo;
import com.jbp.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/front/order/fill")
@Api(tags = "补单控制器")
public class OrderFillController {

    @Autowired
    private OrderFillService orderFillService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepertoryService productRepertoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @ApiOperation(value = "获取当前待补单")
    @RequestMapping(value = "/getFill", method = RequestMethod.GET)
    public CommonResult<List<OrderFillVo>> getFill() {

        List<OrderFill> orderFillList = orderFillService.list(new QueryWrapper<OrderFill>().lambda().eq(OrderFill::getUid, userService.getUserId()));

        List<OrderFillVo> orderFillVos = new ArrayList<>();


        for (OrderFill orderFill : orderFillList) {
            Order order = orderService.getByOrderNo(orderFill.getOrderNo());

            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());

            for (OrderDetail detail : orderDetailList) {
                ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getUid, userService.getUserId()).eq(ProductRepertory::getProductId, detail.getProductId()));
                OrderFillVo orderFillVo = new OrderFillVo();
                orderFillVo.setCount(detail.getPayNum());
                orderFillVo.setGoodsCount(productRepertory == null ?0 : productRepertory.getCount());
                orderFillVo.setName(detail.getProductName());
                orderFillVo.setPicUrl(detail.getImage());
                orderFillVo.setProductId(detail.getProductId());
                orderFillVos.add(orderFillVo);

            }
        }

        return CommonResult.success(orderFillVos);
    }


}



