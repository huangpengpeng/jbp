package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserMonthActiveResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.ClearingVipUserDao;
import com.jbp.service.product.comm.MonthPyCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.agent.ClearingVipUserService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserCapaService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingVipUserServiceImpl extends UnifiedServiceImpl<ClearingVipUserDao, ClearingVipUser> implements ClearingVipUserService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ProductCommService productCommService;
    @Autowired
    private MonthPyCommHandler monthPyCommHandler;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private ProductCommConfigService productCommConfigService;
    @Autowired
    private TeamUserService teamUserService;
    @Autowired
    private TeamService teamService;

    @Override
    public ClearingVipUser create(Integer uid, String accountNo, Long level, String levelName,
                                  Integer commType, String commName, BigDecimal maxAmount, String rule, String description) {
        ClearingVipUser user = new ClearingVipUser(uid, accountNo, level, levelName, commType, commName, maxAmount, rule, description);
        save(user);
        return user;
    }

    @Override
    public ClearingVipUser getByUser(Integer uid, Long level, Integer commType) {
        return getOne(new LambdaQueryWrapper<ClearingVipUser>().eq(ClearingVipUser::getUid, uid).eq(ClearingVipUser::getLevel, level).eq(ClearingVipUser::getCommType, commType));
    }

    @Override
    public PageInfo<ClearingVipUser> pageList(Integer uid, Integer status, Long level, Integer commType, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ClearingVipUser> queryWrapper = new LambdaQueryWrapper<ClearingVipUser>()
                .eq(!ObjectUtil.isNull(uid), ClearingVipUser::getUid, uid)
                .eq(!ObjectUtil.isNull(status), ClearingVipUser::getStatus, status)
                .eq(!ObjectUtil.isNull(level), ClearingVipUser::getLevel, level)
                .eq(!ObjectUtil.isNull(commType), ClearingVipUser::getCommType, commType)
                .orderByDesc(ClearingVipUser::getId);
        Page<ClearingVipUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ClearingVipUser> list = list(queryWrapper);
        return CommonPage.copyPageInfo(page, list);

    }

    @Override
    public UserMonthActiveResponse getActive(Integer uid) {
        ProductCommConfig config = productCommConfigService.getByType(ProductCommEnum.培育佣金.getType());
        UserMonthActiveResponse response = new UserMonthActiveResponse();
        if (config == null || !config.getIfOpen() || StringUtils.isEmpty(config.getRatioJson())) {
            return response.setMsg("未开启活跃设置").setIsActive(null);
        }
        //获取当前月份的第一天和最后一天
        Date now = DateTimeUtils.getNow();
        Date startTime = DateTimeUtils.getMonthStart(now);
        Date endTime = DateTimeUtils.getMonthEnd(now);
        // 查询购买成功的订单里面包含培育商品计算出当前用户购买金额汇总
        List<Order> successList = orderService.getSuccessList(uid, startTime, endTime);
        // 个人支付金额汇总
        BigDecimal fee = BigDecimal.ZERO;
        for (Order order : successList) {
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (OrderDetail orderDetail : orderDetailList) {
                ProductComm productComm = productCommService.getByProduct(orderDetail.getProductId(), ProductCommEnum.培育佣金.getType());
                // 佣金不存在或者关闭直接忽略
                if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                    continue;
                }
                BigDecimal realScore = orderDetailService.getRealScore(orderDetail);
                realScore = realScore.multiply(productComm.getScale());
                fee = realScore.add(fee);
            }
        }

        Map<Long, MonthPyCommHandler.Rule> ruleMap = monthPyCommHandler.getRule(null);
        List<MonthPyCommHandler.Rule> ruleList = ruleMap.values().stream().sorted(Comparator.comparing(MonthPyCommHandler.Rule::getLevel)).collect(Collectors.toList());
        UserCapa userCapa = userCapaService.getByUser(uid);
        Long level = 0L;
        if (userCapa != null) {
            for (MonthPyCommHandler.Rule rule : ruleList) {
                if (NumberUtils.compare(userCapa.getCapaId(), rule.getCapaId()) >= 0 && ArithmeticUtils.gte(fee, rule.getPayPrice())) {
                    if (NumberUtils.compare(rule.getLevel(), level) > 0) {
                        level = rule.getLevel();
                    }
                }
            }
        }
        MonthPyCommHandler.Rule rule = ruleList.get(0);
        BigDecimal hundred = rule.getPayPrice();
        BigDecimal subPrice = hundred.subtract(fee);

        if (userCapa == null || NumberUtils.compare(userCapa.getCapaId(), rule.getCapaId()) < 0) {
            return response.setIsActive(null).setSubPrice(subPrice).setPayPrice(fee).setMsg("等级未达到要求");
        }
        //汇成团队成员活跃展示
        TeamUser teamUser = teamUserService.getOne(new QueryWrapper<TeamUser>().lambda().eq(TeamUser::getUid, uid));
        if (teamUser != null) {
            Team team = teamService.getOne(new QueryWrapper<Team>().lambda().eq(Team::getId, teamUser.getTid()));
            if (team != null && team.getName().equals("汇成")) {
                if (level < 3L) {
                    return response.setIsActive(false).setSubPrice(subPrice).setPayPrice(fee).setMsg("请及时复购");
                } else {
                    return response.setIsActive(true).setSubPrice(subPrice).setPayPrice(fee).setMsg(ruleList.get(2).getPayPrice().toString());
                }
            }
        }
        //其他成员活跃展示
        if (level == 0L) {
            return response.setMsg("请及时复购").setIsActive(false).setSubPrice(subPrice).setPayPrice(fee);
        } else if (level == 1L || level == 2L) {
            return response.setMsg(ruleList.get(0).getPayPrice().toString()).setPayPrice(fee).setSubPrice(subPrice).setIsActive(true);
        } else {
            return response.setMsg(ruleList.get(2).getPayPrice().toString()).setPayPrice(fee).setSubPrice(subPrice).setIsActive(true);
        }
    }
}
