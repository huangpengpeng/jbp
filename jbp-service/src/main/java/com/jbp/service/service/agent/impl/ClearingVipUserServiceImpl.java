package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.dao.agent.ClearingVipUserDao;
import com.jbp.service.product.comm.MonthPyCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.ClearingVipUserService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserCapaService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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
    public PageInfo<ClearingVipUser> pageList(Integer uid,Integer status,Long level,Integer commType,PageParamRequest pageParamRequest) {
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
    public String getActive(Integer uid) {
        //获取当前月份的第一天和最后一天
        LocalDate today = LocalDate.now();
        Date startTime = Date.from(today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(today.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        // 查询购买成功的订单里面包含培育商品计算出当前用户购买金额汇总
        List<Order> list = orderService.getSuccessList(startTime, endTime);
        List<Order> successList = list.stream().filter(o -> uid.equals(o.getUid())).collect(Collectors.toList());
        // 个人支付金额汇总
        BigDecimal fee = BigDecimal.ZERO;
        Map<Integer, ProductComm> map = Maps.newConcurrentMap();
        for (Order order : successList) {
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (OrderDetail orderDetail : orderDetailList) {
                ProductComm productComm = map.get(orderDetail.getProductId());
                if (productComm == null) {
                    productComm = productCommService.getByProduct(orderDetail.getProductId(), ProductCommEnum.培育佣金.getType());
                    if (productComm != null) {
                        map.put(orderDetail.getProductId(), productComm);
                    }
                }
                // 佣金不存在或者关闭直接忽略
                if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                    continue;
                }
                BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee());
                fee = payPrice.add(fee);
            }
        }

        Map<Long, MonthPyCommHandler.Rule> ruleMap = monthPyCommHandler.getRule(null);
        List<MonthPyCommHandler.Rule> ruleList = ruleMap.values().stream().collect(Collectors.toList());
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
        BigDecimal hundred = new BigDecimal(100);
        BigDecimal balance = hundred.subtract(fee);
        if (level == 0L) {
            return "本月不活跃：复购金额差" + balance + "元可活跃";
        } else if (level == 1L || level == 2L) {
            return "本月活跃：已复购满100元";
        } else {
            return "本月活跃：已复购满300元";
        }

    }









}
