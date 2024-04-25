package com.jbp.admin.controller.agent;

import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserVisaOrderRequest;
import com.jbp.common.response.UserVisaOrderRecordResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserVisaOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/visa/order")
@Api(tags = "法大大签约订单")
public class UserVisaOrderController {
    @Resource
    private UserVisaOrderService userVisaOrderService;

    @Resource
    private UserService userService;


    @PreAuthorize("hasAuthority('agent:user:visa:order:page')")
    @ApiOperation(value = "用户签署法大大列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<UserVisaOrderRecordResponse>> getList(String account, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userVisaOrderService.pageList(account, pageParamRequest)));
    }



    @PreAuthorize("hasAuthority('agent:user:visa:order:update')")
    @ApiOperation(value = "修改签约订单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult update(UserVisaOrderRequest request) {

        UserVisaOrder userVisaOrder = userVisaOrderService.getById(request.getId());

        userVisaOrder.setStatus(request.getStatus());
        userVisaOrder.setMark(request.getMark());
        userVisaOrder.setInterview(request.getInterview());
        userVisaOrder.setExamination(request.getExamination());
        userVisaOrder.setVitalityId(request.getVitalityId());

        userVisaOrder.setApplyTime(request.getApplyTime());
        userVisaOrder.setInterviewTime(request.getInterviewTime());
        userVisaOrder.setExaminationTime(request.getExaminationTime());
        userVisaOrderService.updateById(userVisaOrder);
        return CommonResult.success();
    }


}
