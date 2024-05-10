package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingVipUserListRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingVipUserService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/clearingVipUser")
@Api(tags = "结算vip用户")
public class ClearingVipUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ClearingVipUserService clearingVipUserService;

    @PreAuthorize("hasAuthority('agent:clearing:Vip:User:page')")
    @ApiOperation(value = "结算vip用户列表")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<ClearingVipUser>> page(ClearingVipUserListRequest request, PageParamRequest pageParamRequest) {
        //用户账户
        Integer uid = null;
        if(StringUtils.isNotEmpty(request.getAccountNo())){
            User user = userService.getByAccount(request.getAccountNo());
            if(user == null){
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(clearingVipUserService.pageList(uid,request.getStatus(),request.getLevel(),
                request.getLevelName(),request.getCommType(),pageParamRequest)));
    }
}
