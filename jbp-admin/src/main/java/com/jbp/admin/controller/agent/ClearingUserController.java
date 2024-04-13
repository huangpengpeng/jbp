package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ClearingUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/clearing/user")
@Api(tags = "结算管理")
public class ClearingUserController {

    @Resource
    private ClearingUserService clearingUserService;

    @GetMapping("/page")
    @ApiOperation("结算名单列表")
    public CommonResult<CommonPage<ClearingUser>> getList(Integer uid, String account, Long clearingId,
                                                          PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(clearingUserService.pageList(uid, account, clearingId, pageParamRequest)));
    }



    

}
