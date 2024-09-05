package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.enums.OrderFillType;
import com.jbp.common.enums.SupplyRuleEnum;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.product.ProductRef;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.OrderFillVo;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.ProductRefService;
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
    @Autowired
    private ProductRefService productRefService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;


    @ApiOperation(value = "获取当前待补单")
    @RequestMapping(value = "/getFill", method = RequestMethod.GET)
    public CommonResult<List<OrderFillVo>> getFill() {

        List<OrderFill> orderFillList = orderFillService.list(new QueryWrapper<OrderFill>().lambda().eq(OrderFill::getUid, userService.getUserId()).eq(OrderFill::getStatus, OrderFillType.待补单.getName()));

        List<OrderFillVo> orderFillVos = new ArrayList<>();


        for (OrderFill orderFill : orderFillList) {
            Order order = orderService.getByOrderNo(orderFill.getOrderNo());

            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());

            for (OrderDetail detail : orderDetailList) {
                List<ProductRef> refs = productRefService.getList(detail.getProductId());

                if (refs.isEmpty()) {
                    Product product = productService.getById(detail.getProductId());
                    if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                        continue;
                    }
                    ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getUid, userService.getUserId()).eq(ProductRepertory::getProductId, detail.getProductId()));
                    OrderFillVo orderFillVo = new OrderFillVo();
                    orderFillVo.setCount(detail.getPayNum());
                    orderFillVo.setGoodsCount(productRepertory == null ? 0 : productRepertory.getCount());
                    orderFillVo.setName(detail.getProductName());
                    orderFillVo.setPicUrl(detail.getImage());
                    orderFillVo.setProductId(detail.getProductId());
                    orderFillVo.setAttrValueId(detail.getAttrValueId());
                    orderFillVos.add(orderFillVo);
                } else {
                    for (ProductRef ref : refs) {
                        Product product = productService.getById(ref.getProductId());
                        if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                            continue;
                        }

                        List<ProductAttrValue> productAttrValueList = productAttrValueService.list(new QueryWrapper<ProductAttrValue>().lambda().eq(ProductAttrValue::getProductId, ref.getProductId()));
                        ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getUid, userService.getUserId()).eq(ProductRepertory::getProductId, ref.getProductId()));
                        OrderFillVo orderFillVo = new OrderFillVo();
                        orderFillVo.setCount(detail.getPayNum() * ref.getCount());
                        orderFillVo.setGoodsCount(productRepertory == null ? 0 : productRepertory.getCount());
                        orderFillVo.setName(product.getName());
                        orderFillVo.setPicUrl(product.getImage());
                        orderFillVo.setProductId(product.getId());
                        orderFillVo.setAttrValueId(productAttrValueList.get(0).getId());
                        orderFillVos.add(orderFillVo);
                    }
                }

            }
        }

        return CommonResult.success(orderFillVos);
    }


}



