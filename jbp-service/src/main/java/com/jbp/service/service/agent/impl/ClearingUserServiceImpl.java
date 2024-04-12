package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.ClearingUserDao;
import com.jbp.service.product.comm.MonthPyCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingUserService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingUserServiceImpl extends UnifiedServiceImpl<ClearingUserDao, ClearingUser> implements ClearingUserService {

    @Resource
    private UserCapaService userCapaService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderService orderService;
    @Resource
    private ClearingUserDao clearingUserDao;
    @Resource
    private UserService userService;
    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private MonthPyCommHandler monthPyCommHandler;


    @Override
    public Boolean importUserList(Long clearingId, List<ClearingUserImportDto> list) {
        // 检查名单
        if (clearingId == null) {
            throw new CrmebException("结算信息不能为空");
        }
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if (clearingFinal.getCommName().equals(ProductCommEnum.培育佣金.getName())) {
            throw new CrmebException("暂不支持名单导入请初始化结算名单");
        }

        if (clearingFinal == null || !clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())) {
            throw new CrmebException("名单只能在待结算状态下导入");
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("名单不能为空");
        }
        // 批量插入用户
        List<ClearingUser> insetBatchList = Lists.newArrayList();
        Map<String, ClearingUserImportDto> userMap = Maps.newConcurrentMap();
        for (ClearingUserImportDto dto : list) {
            if (StringUtils.isAnyEmpty(dto.getAccount(), dto.getLevelName()) || dto.getLevel() == null) {
                throw new CrmebException("表格不能有空格");
            }
            if (userMap.get(dto.getAccount()) != null) {
                throw new CrmebException(dto.getAccount() + "重复");
            }
            User user = userService.getByAccount(dto.getAccount());
            if (user == null) {
                throw new CrmebException(dto.getAccount() + "账号不存在");
            }
            ClearingUser clearingUser = ClearingUser.builder().clearingId(clearingId).uid(user.getId()).level(1L).levelName("默认").build();
            if (clearingFinal.getCommName().equals(ProductCommEnum.拓展佣金.getName())) {
                if (dto.getWeight() == null || ArithmeticUtils.lessEquals(dto.getWeight(), BigDecimal.ZERO)) {
                    throw new CrmebException(dto.getAccount() + ProductCommEnum.拓展佣金.getName() + "权重不能为空");
                }
                JSONObject rule = new JSONObject();
                rule.put("weight", dto.getWeight());
                clearingUser.setRule(rule.toJSONString());
            }
            // todo 其他佣金如果导入名单可以根据级别配置系统规则保存  ProductCommConfig 规则进行系统配置
            insetBatchList.add(clearingUser);
        }

        List<List<ClearingUser>> partition = Lists.partition(insetBatchList, 2000);
        for (List<ClearingUser> clearingUsers : partition) {
            clearingUserDao.insertBatch(clearingUsers);
        }
        return true;
    }

    @Override
    public Boolean create(Long clearingId) {
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if (clearingFinal == null || !clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())) {
            throw new CrmebException("结算状态不是待结算不允许生成名单");
        }

        // 获取上一次的名单
        ClearingFinal lastOne = clearingFinalService.getLastOne(clearingId, clearingFinal.getCommType());
        Date startTime = DateTimeUtils.parseDate(clearingFinal.getStartTime());
        Date endTime = DateTimeUtils.parseDate(clearingFinal.getEndTime());
        if (clearingFinal.getCommName().equals(ProductCommEnum.拓展佣金.getName())) {
            if (lastOne == null) {
                throw new CrmebException("拓展佣金首次结算请导入名单");
            }
            List<ClearingUser> clearingUsers = getByClearing(clearingId);
            if (CollectionUtils.isEmpty(clearingUsers)) {
                throw new CrmebException("历史结算名单为空请重新导入结算名单");
            }
            List<ClearingUser> insertBatchList = Lists.newArrayList();
            for (ClearingUser clearingUser : clearingUsers) {
                ClearingUser newUser = new ClearingUser();
                BeanUtils.copyProperties(clearingUser, newUser, "id", "clearingId");
                newUser.setClearingId(clearingId);
                insertBatchList.add(newUser);
            }
            clearingUserDao.insertBatch(insertBatchList);
        }
        if (clearingFinal.getCommName().equals(ProductCommEnum.培育佣金.getName())) {
            // 查询购买成功的订单里面包含培育商品计算出每个用户购买金额汇总
            List<Order> successList = orderService.getSuccessList(startTime, endTime);
            // 个人支付金额汇总
            Map<Integer, BigDecimal> userPayMap = Maps.newConcurrentMap();
            Map<Integer, ProductComm> map = Maps.newConcurrentMap();
            for (Order order : successList) {
                List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
                for (OrderDetail orderDetail : orderDetailList) {
                    ProductComm productComm = map.get(orderDetail.getProductId());
                    if (productComm == null) {
                        productComm = productCommService.getByProduct(orderDetail.getProductId(), ProductCommEnum.培育佣金.getType());
                        map.put(orderDetail.getProductId(), productComm);
                    }
                    // 佣金不存在或者关闭直接忽略
                    if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                        continue;
                    }
                    BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee());
                    BigDecimal fee = payPrice.add(BigDecimal.valueOf(MapUtils.getDouble(userPayMap, order.getUid(), 0.0)));
                    userPayMap.put(order.getUid(), fee);
                }
            }
            Map<Long, MonthPyCommHandler.Rule> ruleMap = monthPyCommHandler.getRule(null);
            List<MonthPyCommHandler.Rule> ruleList = ruleMap.values().stream().collect(Collectors.toList());
            List<ClearingUser> clearingUserList = Lists.newArrayList();
            // 遍历业绩
            userPayMap.forEach((k, v) -> {
                UserCapa userCapa = userCapaService.getByUser(k);
                if (userCapa != null) {
                    Long level = 0L;
                    for (MonthPyCommHandler.Rule rule : ruleList) {
                        if (NumberUtils.compare(userCapa.getCapaId(), rule.getCapaId()) >= 0 && ArithmeticUtils.gte(v, rule.getPayPrice())) {
                            if (NumberUtils.compare(rule.getLevel(), level) > 0) {
                                level = rule.getLevel();
                            }
                        }
                        if (NumberUtils.compare(level, 0L) > 0) {
                            User user = userService.getById(k);
                            MonthPyCommHandler.Rule hasRule = ruleMap.get(level);
                            ClearingUser clearingUser = new ClearingUser(clearingId, user.getId(), user.getAccount(),
                                    level, hasRule.getLevelName(), JSONObject.toJSONString(hasRule));
                            clearingUserList.add(clearingUser);
                        }
                    }
                }
            });
            clearingUserDao.insertBatch(clearingUserList);
        }
        return true;
    }

    @Override
    public Boolean del4Clearing(Long clearingId) {
        QueryWrapper<ClearingUser> w = new QueryWrapper<>();
        w.lambda().eq(ClearingUser::getClearingId, clearingId);
        return remove(w);
    }

    @Override
    public Boolean del(Long id) {
        return removeById(id);
    }

    @Override
    public Boolean add(String account, Long clearingId, Long level, String levelName, BigDecimal weight) {
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        String ruleStr = "";
        if (clearingFinal.getCommType().intValue() == ProductCommEnum.拓展佣金.getType()) {
            if (weight == null || ArithmeticUtils.lessEquals(weight, BigDecimal.ZERO)) {
                throw new CrmebException("扩展佣金权重必须大于0");
            }
            level = 1L;
            levelName = "默认";
            JSONObject rule = new JSONObject();
            rule.put("weight", weight);
            ruleStr = rule.toJSONString();
        }
        if (clearingFinal.getCommType().intValue() == ProductCommEnum.拓展佣金.getType()) {
            if (level == null || StringUtils.isEmpty(levelName)) {
                throw new CrmebException("结算级别不能为空");
            }
            Map<Long, MonthPyCommHandler.Rule> ruleMap = monthPyCommHandler.getRule(null);
            if (ruleMap.get(level) == null) {
                throw new CrmebException("结算级别不存在");
            }
            ruleStr = JSONObject.toJSONString(ruleMap.get(level));
        }
        User user = userService.getByAccount(account);
        if (user == null) {
            throw new CrmebException("账户不存在");
        }
        ClearingUser clearingUser = new ClearingUser(clearingId, user.getId(), user.getAccount(),
                level, levelName, ruleStr);

        //  删除历史
        remove(new QueryWrapper<ClearingUser>().lambda().eq(ClearingUser::getClearingId, clearingId).eq(ClearingUser::getUid, user.getId()));
        save(clearingUser);
        return true;
    }

    @Override
    public List<ClearingUser> getByClearing(Long clearingId) {
        return list(new QueryWrapper<ClearingUser>().lambda().eq(ClearingUser::getClearingId, clearingId));
    }
}
