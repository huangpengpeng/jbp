package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;
import com.jbp.common.response.UserMonthActiveResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.product.comm.PingTaiCommHandler;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingBonusFlowService;
import com.jbp.service.service.agent.ClearingBonusService;
import com.jbp.service.service.agent.ClearingVipUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 文章控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/clearingVipUser")
@Api(tags = "结算vip用户")
public class ClearingVipUserController {

    @Autowired
    private ClearingBonusService clearingBonusService;
    @Autowired
    private ClearingVipUserService clearingVipUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private PingTaiCommHandler pingTaiCommHandler;
    @Autowired
    private ClearingBonusFlowService clearingBonusFlowService;

    @ApiOperation(value = "收入汇总")
    @RequestMapping(value = "/getTotal", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getTotal() {
        Map<String, Object> map = new HashMap<>();
        ClearingBonus clearingBonus = clearingBonusService.getOne(new QueryWrapper<ClearingBonus>().select(" sum(commAmt) as commAmt ").lambda().eq(ClearingBonus::getUid, userService.getUserId()));
        List<ClearingVipUser> list = clearingVipUserService.list(new QueryWrapper<ClearingVipUser>().lambda().eq(ClearingVipUser::getUid, userService.getUserId()).orderByAsc(ClearingVipUser::getLevel));
        map.put("total", clearingBonus);
        map.put("income", list);
        return CommonResult.success(map);
    }

    @ApiOperation(value = "收入每日汇总")
    @RequestMapping(value = "/getTotalDay", method = RequestMethod.GET)
    public CommonPage<ClearingBonusListResponse> getTotalDay(PageParamRequest pageParamRequest) {
        PageInfo<ClearingBonusListResponse> listResponses = clearingBonusService.getClearingList(userService.getUserId(), pageParamRequest);
        return CommonPage.restPage(listResponses);
    }

    @ApiOperation(value = "收入每日明细")
    @RequestMapping(value = "/getTotalInfoDay", method = RequestMethod.GET)
    public CommonResult<List<ClearingBonusListResponse>> getTotalInfoDay(String day) {
        List<ClearingBonusListResponse> clearingList = clearingBonusFlowService.getClearingList(userService.getUserId(), day);
        for (ClearingBonusListResponse bonus : clearingList) {
            if (StringUtils.isNotEmpty(bonus.getPostscript()) && bonus.getPostscript().contains("额度上限")) {
                if (org.apache.commons.lang3.StringUtils.equals(bonus.getLevelName(), "店1")) {
                    bonus.setLevelName("V1");
                }
                if (org.apache.commons.lang3.StringUtils.equals(bonus.getLevelName(), "店2")) {
                    bonus.setLevelName("V2");
                }
                if (org.apache.commons.lang3.StringUtils.equals(bonus.getLevelName(), "店3")) {
                    bonus.setLevelName("V3");
                }
            }
        }
        return CommonResult.success(clearingList);
    }

    @ApiOperation(value = "购买平台校验 platform =【直通V1 直通V2 直通V3】 num =  购买合计数量")
    @RequestMapping(value = "/valid4Platform", method = RequestMethod.GET)
    public CommonResult<Boolean> valid(String platform, int num) {
        if (StringUtils.isEmpty(platform)) {
            throw new CrmebException("下单平台不能为空");
        }
        Integer uid = userService.getUserId();
        List<PingTaiCommHandler.Rule> ruleList = pingTaiCommHandler.getRule(null);
        Map<String, PingTaiCommHandler.Rule> ruleMap = FunctionUtil.keyValueMap(ruleList, PingTaiCommHandler.Rule::getLevelName);

        PingTaiCommHandler.Rule rule = ruleMap.get(platform.equals("直通V1") ? "V1" : platform.equals("直通V2") ? "V2" : platform.equals("直通V3") ? "V3" : "");
        BigDecimal maxFee = rule.getMaxFee().multiply(BigDecimal.valueOf(rule.getMaxNum()));

        ClearingVipUser clearingVipUser = clearingVipUserService.getByUser(uid, rule.getRefLevel(), pingTaiCommHandler.getType());
        BigDecimal usedMaxFee = BigDecimal.ZERO;
        if (clearingVipUser == null) {
            usedMaxFee = rule.getMaxFee().multiply(BigDecimal.valueOf(num));
        } else {
            usedMaxFee = rule.getMaxFee().multiply(BigDecimal.valueOf(num)).add(clearingVipUser.getMaxAmount());
        }
        if (ArithmeticUtils.gt(usedMaxFee, maxFee)) {
            throw new CrmebException("购买份额超出限制");
        }
        if (StringUtils.equals(platform, "直通V1")) {

            if (1 != num) {
                throw new CrmebException("一次性购买1件商品");
            }
        }
        if (StringUtils.equals(platform, "直通V2")) {
            if (2 != num) {
                throw new CrmebException("一次性购买2件商品");
            }
            // 验证是否购买V1
            PingTaiCommHandler.Rule rule1 = ruleMap.get("V1");
            ClearingVipUser clearingVipUser1 = clearingVipUserService.getByUser(uid, rule1.getRefLevel(), pingTaiCommHandler.getType());
            if (clearingVipUser1 == null) {
                throw new CrmebException("未购买直通VIP1礼包");
            }
        }
        if (StringUtils.equals(platform, "直通V3")) {
            if (3 != num) {
                throw new CrmebException("一次性购买3件商品");
            }
            // 验证是否购买V2
            PingTaiCommHandler.Rule rule2 = ruleMap.get("V2");
            ClearingVipUser clearingVipUser2 = clearingVipUserService.getByUser(uid, rule2.getRefLevel(), pingTaiCommHandler.getType());
            if (clearingVipUser2 == null) {
                throw new CrmebException("未购买直通VIP2礼包");
            }
        }
        return CommonResult.success(true);
    }

    @ApiOperation(value = "月度活跃展示")
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public CommonResult<UserMonthActiveResponse> active() {
        return CommonResult.success(clearingVipUserService.getActive(userService.getUserId()));
    }

}

