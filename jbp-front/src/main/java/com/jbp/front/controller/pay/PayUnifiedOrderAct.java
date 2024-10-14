package com.jbp.front.controller.pay;

import com.jbp.common.model.cat.Cart;
import com.jbp.common.model.user.User;
import com.jbp.common.request.CartRequest;
import com.jbp.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
@RequestMapping("api/front/pay")
@Api(tags = "前台支付控制器")
public class PayUnifiedOrderAct {
    @ApiOperation(value = "支付下单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<String> create(String token) {

        return CommonResult.failed();
    }

}
