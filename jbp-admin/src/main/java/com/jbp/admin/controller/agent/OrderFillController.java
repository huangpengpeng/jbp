package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrderFillSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.UserService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/agent/order/fill")
@Api(tags = "订单补单管理")
public class OrderFillController {

    @Autowired
    private OrderFillService orderFillService;
    @Autowired
    private UserService userService;

//    @PreAuthorize("hasAuthority('agent:order:fill:page')")
    @GetMapping("/page")
    @ApiOperation("订单补单列表")
    public CommonResult<CommonPage<OrderFill>> page(OrderFillSearchRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getOAccount())) {
            User user = userService.getByAccount(request.getOAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(orderFillService.getList(uid,request.getONickname(),request.getOrderNo(),pageParamRequest)));

    }
}
