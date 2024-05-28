package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserCapaXsDelectRequest;
import com.jbp.common.request.agent.UserCapaRequest;
import com.jbp.common.request.agent.UserCapaXsAddRequest;
import com.jbp.common.response.UserUpgradeListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/agent/user/capa/xs")
@Api(tags = "用户星级")
public class UserCapaXsController {
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserInvitationFlowService invitationFlowService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private SelfScoreService selfScoreService;


    @PreAuthorize("hasAuthority('agent:user:capa:xs:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<UserCapaXs>> getList(UserCapaRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userCapaXsService.pageList(uid, request.getCapaId(),request.getIfFake(), request.getPhone(),pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:capa:xs:add')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加")
    @PostMapping("/add")
    @ApiOperation("添加")
    public CommonResult add(@Validated  @RequestBody UserCapaXsAddRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            return CommonResult.failed("账户信息错误");
        }
        userCapaXsService.saveOrUpdateCapa(user.getId(), request.getCapaId(), request.getIfFake(), request.getRemark(), request.getDescription());
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:user:capa:xs:delect')")
    @GetMapping("/delect")
    @ApiOperation("删除")
    public CommonResult delect(UserCapaXsDelectRequest request) {
        userCapaXsService.del(request.getUid(),request.getDescription(),request.getRemark());
        return CommonResult.success();
    }


    @ApiOperation(value = "升星查看")
    @ResponseBody
    @GetMapping(value = "/user/upgradeList")
    public CommonResult<List<UserInvitationFlow>> upgradeList(Integer uid,String uAccount) {
        // 返回对象
        if (uid == null) {
            uid = -1;
        }
        if (StringUtils.isNotEmpty(uAccount)) {
            User user = userService.getByAccount(uAccount);
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        List<UserInvitationFlow> result = Lists.newArrayList();
        List<CapaXs> capaXsList = capaXsService.list();
        capaXsList = capaXsList.stream().sorted(Comparator.comparing(CapaXs::getId).reversed()).collect(Collectors.toList());
        List<UserInvitation> nextList = invitationService.getNextList2(uid);
        // 查询直属下级最高等级用户【星级最高，业绩最好】
        for (UserInvitation invitation : nextList) {
            UserCapaXs userCapaXs = userCapaXsService.getByUser(invitation.getUId());
            // 下级星级最高的用户列表
            List<UserInvitationFlow> xsUnderList = Lists.newArrayList();
            for (CapaXs capaXs : capaXsList) {
                xsUnderList = invitationFlowService.getXsUnderList(invitation.getUId(), capaXs.getId());
                if (userCapaXs != null && NumberUtils.compare(userCapaXs.getCapaId(), capaXs.getId()) >= 0) {
                    UserInvitationFlow flow = new UserInvitationFlow();
                    flow.setUId(invitation.getUId());
                    flow.setCapaXsId(userCapaXs.getCapaId());
                    flow.setUCapaXsName(userCapaXs.getCapaName());
                    xsUnderList.add(flow);
                }
                if (CollectionUtils.isNotEmpty(xsUnderList)) {
                    break;
                }
            }
            if (CollectionUtils.isEmpty(xsUnderList)) {
                continue;
            }
            // 最大业绩的人找出来
            for (UserInvitationFlow flow : xsUnderList) {
                BigDecimal teamAmt = selfScoreService.getUserNext(flow.getUId(), true);
                flow.setTeamAmt(teamAmt);
            }
            UserInvitationFlow flow = xsUnderList.stream().sorted(Comparator.comparing(UserInvitationFlow::getTeamAmt).reversed()).findFirst().get();
            result.add(flow);
        }
        for (UserInvitationFlow flow : result) {
            User user = userService.getById(flow.getUId());
            String nickname = user.getNickname();
            String account = user.getAccount();
            flow.setUNickName(nickname);
            flow.setUAccount(account);
        }
        return CommonResult.success(result);
    }


}
