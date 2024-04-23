package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingPreUserRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.ClearingUserDao;
import com.jbp.service.product.comm.MonthPyCommHandler;
import com.jbp.service.product.comm.PingTaiCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
    private FundClearingService fundClearingService;
    @Resource
    private UserCapaSnapshotService userCapaSnapshotService;
    @Resource
    private CapaService capaService;
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
    private RedisTemplate redisTemplate;
    @Resource
    private MonthPyCommHandler monthPyCommHandler;
    @Resource
    private PingTaiCommHandler pingTaiCommHandler;


    @Override
    public Boolean importUserList(Long clearingId, List<ClearingUserImportDto> list) {
        // 检查名单
        if (clearingId == null) {
            throw new CrmebException("结算信息不能为空");
        }
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if (clearingFinal == null || !clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())) {
            throw new CrmebException("名单只能在待结算状态下导入");
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("名单不能为空");
        }
        // 导入名单结算不需要预设
        delPerUser();
        // 批量插入用户
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
        }
        // 导入名单设置
        importUserSet(list,  clearingId, clearingFinal.getCommType());
        return true;
    }


    @Override
    public Boolean preImportUser(ClearingPreUserRequest request) {
        Boolean task = redisTemplate.opsForValue().setIfAbsent("ClearingFinalRunning", 1); // 正在结算
        if(!task){
            throw new RuntimeException("正在结算中请勿设置名单");
        }
        if (request.getCommType() == null || StringUtils.isEmpty(request.getCommName())) {
            throw new CrmebException("佣金信息不能为空");
        }
        List<ClearingUserImportDto> list = request.getUserList();
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("名单不能为空");
        }
        // 删除历史的预设名单
        delPerUser();
        // 培育佣金规则
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
        }
        // 设置导入用户信息
        importUserSet(list, -1L, request.getCommType());

        redisTemplate.delete("ClearingFinalRunning");
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
        // 获取预设名单
        List<ClearingUser> perUserList = getPerUserList();
        Map<Integer, ClearingUser> perMap = FunctionUtil.keyValueMap(perUserList, ClearingUser::getUid);
        // 扩展结算名单
        createKuoZhanUser(clearingId, clearingFinal, lastOne, perMap);
        // 培育结算名单
        createPeiYuUser(clearingId, clearingFinal, startTime, endTime, perMap);
        // 平台分红名单
        createPingTaiUser(clearingId, clearingFinal, endTime, perMap);
        // 删除预设名单
        delPerUser();
        return true;
    }


    @Override
    public Boolean del4Clearing(Long clearingId) {
        QueryWrapper<ClearingUser> w = new QueryWrapper<>();
        w.lambda().eq(ClearingUser::getClearingId, clearingId);
        return remove(w);
    }

    @Override
    public Boolean delPerUser() {
        QueryWrapper<ClearingUser> w = new QueryWrapper<>();
        w.lambda().eq(ClearingUser::getClearingId, -1L);
        return remove(w);
    }

    @Override
    public List<ClearingUser> getPerUserList() {
        QueryWrapper<ClearingUser> w = new QueryWrapper<>();
        w.lambda().eq(ClearingUser::getClearingId, -1L);
        return list(w);
    }

    @Override
    public Boolean del(Long id) {
        return removeById(id);
    }

    @Override
    public List<ClearingUser> getByClearing(Long clearingId) {
        return list(new QueryWrapper<ClearingUser>().lambda().eq(ClearingUser::getClearingId, clearingId));
    }

    @Override
    public PageInfo<ClearingUser> pageList(Integer uid, String account, Long clearingId, PageParamRequest pageParamRequest) {
        Page<ClearingUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ClearingUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(clearingId != null, ClearingUser::getClearingId, clearingId);
        lqw.eq(uid != null, ClearingUser::getUid, uid);
        lqw.eq(StringUtils.isNotEmpty(account), ClearingUser::getAccountNo, account);
        lqw.orderByDesc(ClearingUser::getId);
        return CommonPage.copyPageInfo(page, list(lqw));
    }


    private void createPingTaiUser(Long clearingId, ClearingFinal clearingFinal,  Date endTime, Map<Integer, ClearingUser> perMap) {
        if (clearingFinal.getCommName().equals(ProductCommEnum.平台分红.getName())) {
            Capa maxCapa = capaService.getMaxCapa();
            List<PingTaiCommHandler.Rule> ruleList = pingTaiCommHandler.getRule(null);
            if(CollectionUtils.isEmpty(ruleList)){
               throw new CrmebException("平台分红规则为空请联系管理员");
            }
            ruleList = ruleList.stream().filter(s->s.getRefLevel()== null).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(ruleList)){
                throw new CrmebException("平台分红规则为空请联系管理员2");
            }
            List<UserCapa> userCapaList = userCapaService.list(new LambdaQueryWrapper<UserCapa>().eq(UserCapa::getCapaId, maxCapa.getId()));
            List<ClearingUser> clearingUserList = Lists.newArrayList();
            for (UserCapa userCapa : userCapaList) {
                ClearingUser perUser = perMap.get(userCapa.getUid());
                if (perUser != null) {
                    continue;
                }
                Date scoreStart = null;
                UserCapaSnapshot snapshot = userCapaSnapshotService.getByFirst(userCapa.getUid(), userCapa.getCapaId());
                if (snapshot == null) {
                    User user = userService.getById(snapshot.getUid());
                    scoreStart = user.getCreateTime();
                } else {
                    scoreStart = snapshot.getGmtCreated();
                }
                BigDecimal sendCommAmt = fundClearingService.getSendCommAmt(userCapa.getUid(), scoreStart, endTime, ProductCommEnum.报单佣金.getName(), ProductCommEnum.零售佣金.getName());
                Long level = 0L;
                PingTaiCommHandler.Rule tagRule = null;
                for (PingTaiCommHandler.Rule rule : ruleList) {
                    if (ArithmeticUtils.gt(sendCommAmt, rule.getMinScore()) && NumberUtils.compare(rule.getLevel(), level) > 0) {
                        level = rule.getLevel();
                        tagRule = rule;
                    }
                }
                // 说明满足结算条件
                if (NumberUtils.compare(level, 0L) > 0) {
                    User user = userService.getById(userCapa.getUid());
                    ClearingUser clearingUser = new ClearingUser(clearingId, userCapa.getUid(), user.getAccount(),
                            level, tagRule.getLevelName(), JSONObject.toJSONString(tagRule));
                    clearingUserList.add(clearingUser);
                }
            }
            // 处理预设名单
            if (!perMap.isEmpty()) {
                perMap.forEach((uid, perUser) -> {
                    try {
                        JSONObject rule = JSONObject.parseObject(perUser.getRule());
                        if (rule == null) {
                            throw new RuntimeException("预设名单不是当前佣金结算，请清空再次结算");
                        }
                        rule.toJavaObject(MonthPyCommHandler.Rule.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    ClearingUser newUser = new ClearingUser();
                    BeanUtils.copyProperties(perUser, newUser, "id", "clearingId");
                    newUser.setClearingId(clearingId);
                    clearingUserList.add(newUser);
                });
            }
            clearingUserDao.insertBatch(clearingUserList);
        }
    }

    private void createPeiYuUser(Long clearingId, ClearingFinal clearingFinal, Date startTime, Date endTime, Map<Integer, ClearingUser> perMap) {
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
                        if(productComm != null){
                            map.put(orderDetail.getProductId(), productComm);
                        }
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
                ClearingUser perUser = perMap.get(k);
                if (perUser == null && userCapa != null) {
                    Long level = 0L;
                    for (MonthPyCommHandler.Rule rule : ruleList) {
                        if (NumberUtils.compare(userCapa.getCapaId(), rule.getCapaId()) >= 0 && ArithmeticUtils.gte(v, rule.getPayPrice())) {
                            if (NumberUtils.compare(rule.getLevel(), level) > 0) {
                                level = rule.getLevel();
                            }
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
            });

            if(!perMap.isEmpty()) {
                perMap.forEach((uid, perUser) -> {
                    try {
                        JSONObject rule = JSONObject.parseObject(perUser.getRule());
                        if (rule == null) {
                            throw new RuntimeException("预设名单不是当前佣金结算，请清空再次结算");
                        }
                        rule.toJavaObject(MonthPyCommHandler.Rule.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    ClearingUser newUser = new ClearingUser();
                    BeanUtils.copyProperties(perUser, newUser, "id", "clearingId");
                    newUser.setClearingId(clearingId);
                    clearingUserList.add(newUser);
                });
            }
            clearingUserDao.insertBatch(clearingUserList);
        }
    }

    private void createKuoZhanUser(Long clearingId, ClearingFinal clearingFinal, ClearingFinal lastOne, Map<Integer, ClearingUser> perMap) {
        if (clearingFinal.getCommName().equals(ProductCommEnum.拓展佣金.getName())) {
            if (lastOne == null) {
                throw new CrmebException("拓展佣金首次结算请导入名单");
            }
            List<ClearingUser> clearingUsers = getByClearing(lastOne.getId());
            if (CollectionUtils.isEmpty(clearingUsers)) {
                throw new CrmebException("历史结算名单为空请重新导入结算名单");
            }
            List<ClearingUser> insertBatchList = Lists.newArrayList();

            for (ClearingUser clearingUser : clearingUsers) {
                ClearingUser perUser = perMap.get(clearingUser.getUid());
                if(perUser == null){
                    ClearingUser newUser = new ClearingUser();
                    BeanUtils.copyProperties(clearingUser, newUser, "id", "clearingId");
                    newUser.setClearingId(clearingId);
                    insertBatchList.add(newUser);
                }
            }

            if(!perMap.isEmpty()) {
                perMap.forEach((uid, perUser) -> {
                    try {
                        JSONObject rule = JSONObject.parseObject(perUser.getRule());
                        if (!rule.containsKey("weight") || rule.getBigDecimal("weight") == null || ArithmeticUtils.lessEquals(rule.getBigDecimal("weight"), BigDecimal.ZERO)) {
                            throw new RuntimeException("预设名单不是当前佣金结算，请清空再次结算");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    ClearingUser newUser = new ClearingUser();
                    BeanUtils.copyProperties(perUser, newUser, "id", "clearingId");
                    newUser.setClearingId(clearingId);
                    insertBatchList.add(newUser);
                });
            }
            clearingUserDao.insertBatch(insertBatchList);
        }
    }

    private void importUserSet(List<ClearingUserImportDto> userList, Long clearingId, Integer commType) {
        List<ClearingUser> insetBatchList = Lists.newArrayList();
        if (commType.equals(ProductCommEnum.拓展佣金.getType())) {
            for (ClearingUserImportDto dto : userList) {
                User user = userService.getByAccount(dto.getAccount());
                ClearingUser clearingUser = ClearingUser.builder().clearingId(clearingId).uid(user.getId()).level(1L).levelName("默认").build();
                if (dto.getWeight() == null || ArithmeticUtils.lessEquals(dto.getWeight(), BigDecimal.ZERO)) {
                    throw new CrmebException(dto.getAccount() + ProductCommEnum.拓展佣金.getName() + "权重不能为空");
                }
                JSONObject rule = new JSONObject();
                rule.put("weight", dto.getWeight());
                clearingUser.setRule(rule.toJSONString());
                insetBatchList.add(clearingUser);
            }
        }
        if (commType.equals(ProductCommEnum.培育佣金.getType())) {
            Map<Long, MonthPyCommHandler.Rule> ruleMap = monthPyCommHandler.getRule(null);
            if (ruleMap == null || ruleMap.isEmpty()) {
                throw new CrmebException("请联系管理员设置培育佣金结算级别规则");
            }
            for (ClearingUserImportDto dto : userList) {
                User user = userService.getByAccount(dto.getAccount());
                ClearingUser clearingUser = ClearingUser.builder().clearingId(clearingId).uid(user.getId()).level(dto.getLevel()).levelName(dto.getLevelName()).build();
                MonthPyCommHandler.Rule rule = ruleMap.get(dto.getLevel());
                if (rule == null) {
                    throw new CrmebException("培育佣金结算级编号不存在");
                }
                clearingUser.setRule(JSONObject.toJSONString(rule));
                insetBatchList.add(clearingUser);
            }
        }

        if (commType.equals(ProductCommEnum.平台分红.getType())) {
            List<PingTaiCommHandler.Rule> ruleList = pingTaiCommHandler.getRule(null);
            if (CollectionUtils.isEmpty(ruleList)) {
                throw new CrmebException("请联系管理员设置平台分红结算级别规则");
            }
            Map<Long, PingTaiCommHandler.Rule> ruleMap = FunctionUtil.keyValueMap(ruleList, PingTaiCommHandler.Rule::getLevel);
            for (ClearingUserImportDto dto : userList) {
                User user = userService.getByAccount(dto.getAccount());
                ClearingUser clearingUser = ClearingUser.builder().clearingId(clearingId).uid(user.getId()).level(dto.getLevel()).levelName(dto.getLevelName()).build();
                PingTaiCommHandler.Rule rule = ruleMap.get(dto.getLevel());
                if (rule == null) {
                    throw new CrmebException("培育佣金结算级编号不存在");
                }
                clearingUser.setRule(JSONObject.toJSONString(rule));
                insetBatchList.add(clearingUser);
            }
        }
        // 保存数据
        List<List<ClearingUser>> partition = Lists.partition(insetBatchList, 2000);
        for (List<ClearingUser> clearingUsers : partition) {
            clearingUserDao.insertBatch(clearingUsers);
        }
    }
}
