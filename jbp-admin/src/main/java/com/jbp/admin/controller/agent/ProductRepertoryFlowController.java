package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ProductRepertoryFlowSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ProductRepertoryFlowService;
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
@RequestMapping("api/admin/agent/platform/product/repertory/flow")
@Api(tags = "库存明细")
public class ProductRepertoryFlowController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepertoryFlowService productRepertoryFlowService;

//    @PreAuthorize("hasAuthority('agent:platform:product:repertory:flow:page')")
    @GetMapping("/list")
    @ApiOperation("库存明细分页列表")
    public CommonResult<CommonPage<ProductRepertoryFlow>> page(ProductRepertoryFlowSearchRequest request, PageParamRequest pageParamRequest){
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(productRepertoryFlowService.getList(uid,request.getNickname(),pageParamRequest)));
    }
}
