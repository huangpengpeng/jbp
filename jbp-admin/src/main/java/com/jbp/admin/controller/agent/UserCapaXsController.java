package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserCapaXsDelectRequest;
import com.jbp.common.request.agent.UserCapaRequest;
import com.jbp.common.request.agent.UserCapaXsAddRequest;
import com.jbp.common.response.UserUpgradeListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return CommonResult.success(CommonPage.restPage(userCapaXsService.pageList(uid, request.getCapaId(),request.getIfFake(), pageParamRequest)));
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
    public Object upgradeList(Integer uid) {
        // 返回对象
        List<UserUpgradeListResponse> result = Lists.newArrayList();
        if(uid == null){
            uid = -1;
        }
        // 查询直属下级最高等级用户【星级最高，业绩最好】
        for (User user : usersSub) {
            List<XsUserCapaResp> selfUserXsCapa = unifiedJDBCMng.query(new String[]{"userId"}, new String[]{user.getId().toString()}, "查询用户星级信息", XsUserCapaResp.class);
            // 查询出一阶下面所有下级
            List<XsUserCapaResp> allUserXsCapa = unifiedJDBCMng.query(new String[]{"userId"}, new String[]{user.getId().toString()}, "查询下级所有星级用户", XsUserCapaResp.class);
            allUserXsCapa.addAll(selfUserXsCapa);
            if(CollectionUtils.isEmpty(allUserXsCapa)){
                continue;
            }
            Map<Long, XsUserCapaResp> userCapaRespMap = FunctionUtil.keyValueMap(allUserXsCapa, XsUserCapaResp::getId);

            Map<Integer, List<XsUserCapaResp>> queryXsUserCapaRespMap = FunctionUtil.valueMap(allUserXsCapa, XsUserCapaResp::getRankNum);
            //根据key进行升序排序
            Map<Integer, List<XsUserCapaResp>> xsUserCapaRespMap = new LinkedHashMap<>();
            queryXsUserCapaRespMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> xsUserCapaRespMap.put(x.getKey(), x.getValue()));
            // 获取最大key
            List<Integer> mapkey = new ArrayList<>(xsUserCapaRespMap.keySet());
            Integer maxKey = mapkey.get(mapkey.size() - 1);
            // 独立先最高级别用户
            List<XsUserCapaResp> topRankNumUsers = xsUserCapaRespMap.get(maxKey);

            // 筛选最高星级并且团队业绩最大的用户
            Long topUserId = null;
            BigDecimal topTeamAmt = BigDecimal.ZERO;
            BigDecimal selfAmt = BigDecimal.ZERO;

            for (XsUserCapaResp xsUserCapaResp : topRankNumUsers) {
                // 线上业绩
                Number teamNum = unifiedJDBCMng.getNum(new String[]{"userId"}, new Object[]{xsUserCapaResp.getId()}, "用户团队业绩");
                // 历史业绩
                Number teamNum2 = 0d;
                if (com.common.util.StringUtils.isBlank((String) (params.get("startTime")))) {
                    teamNum2 = unifiedJDBCMng.getNum(new String[]{"userId"}, new Object[]{xsUserCapaResp.getId()}, "用户团队业绩2");
                }
                BigDecimal totalTeamNum = BigDecimal.valueOf(teamNum.doubleValue()).add(BigDecimal.valueOf(teamNum2.intValue()));
                if (ArithmeticUtils.gte(totalTeamNum, topTeamAmt)) {
                    topTeamAmt = totalTeamNum;
                    topUserId = xsUserCapaResp.getId();
                }
            }

            BigDecimal differenceAmt = BigDecimal.ZERO;
            if (topUserId != null) {
                XsUserCapaResp xsUserCapaResp = userCapaRespMap.get(topUserId);
                BigDecimal teamAmt = xsUserCapaResp.getTeamAmt();
                if(teamAmt == null){
                    log.info("########### id:{}", topUserId);
                }
                // 用户没有被转挂，并且转挂后任然属于自己的下级
                if(xsUserCapaResp.getMountUserId() == null || userCapaRespMap.get(xsUserCapaResp.getId()) != null || NumberUtils.compare(userId, xsUserCapaResp.getId()) == 0){
                    differenceAmt = teamAmt.subtract(topTeamAmt);
                    differenceAmt = ArithmeticUtils.less(differenceAmt, BigDecimal.ZERO) ? BigDecimal.ZERO : differenceAmt;

                    Number selfNum = unifiedJDBCMng.getNum(new String[]{"userId"}, new Object[]{topUserId}, "个人业绩");
                    selfAmt = BigDecimal.valueOf(selfNum.doubleValue());
                    UserUpgradeListRep rep = new UserUpgradeListRep(topUserId, xsUserCapaResp.getUsername(), xsUserCapaResp.getNumberCode(), xsUserCapaResp.getRankName(), topTeamAmt, differenceAmt, selfAmt);
                    result.add(rep);
                }
            }
        }
        List<UserUpgradeListRep> newResult = Lists.newArrayList();
        result.stream().filter(FunctionUtil.distinctByKey(p -> p.getId()))  //filter保留true的值
                .forEach(newResult::add);
        return new ResponseUtil<>(ResponseUtil.SUCCESS, ResponseUtil.MESSAGE, newResult.size(), newResult);
    }

}
