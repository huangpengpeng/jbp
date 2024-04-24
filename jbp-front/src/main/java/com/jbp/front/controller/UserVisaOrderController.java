package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.response.UserVisaOrderListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserVisaOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;



@Slf4j
@RestController
@RequestMapping("api/front/userVisaOrder")
@Api(tags = "签约订单控制器")
public class UserVisaOrderController {

    @Autowired
    private UserVisaOrderService userVisaOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;

    @ApiOperation(value = "签到记录列表")
    @RequestMapping(value = "/record/list", method = RequestMethod.GET)
    public CommonResult<List<UserVisaOrderListResponse>> getList(String status ) {
        List<UserVisaOrderListResponse> infoResponse=new ArrayList<>();
        List<UserVisaOrder> list =   userVisaOrderService.list(new QueryWrapper<UserVisaOrder>().lambda().eq(UserVisaOrder::getUid,userService.getUserId()).eq(status != "全部",UserVisaOrder::getStatus,status).orderByDesc(UserVisaOrder::getGmtCreated));
        for(UserVisaOrder userVisaOrder : list){
            UserVisaOrderListResponse userVisaOrderListResponse =new UserVisaOrderListResponse();
            BeanUtils.copyProperties(userVisaOrder, userVisaOrderListResponse);
            infoResponse.add(userVisaOrderListResponse);
        }

        infoResponse.forEach(e ->{
          List<OrderDetail> o = orderDetailService.getByOrderNo(e.getOrderNo());
            e.setNumber(o.get(0).getPayNum());
        });
        return CommonResult.success(infoResponse);
    }


}



