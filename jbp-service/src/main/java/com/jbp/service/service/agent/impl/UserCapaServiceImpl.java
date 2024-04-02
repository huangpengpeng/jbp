package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.condition.ConditionEnum;
import com.jbp.service.dao.agent.UserCapaDao;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaServiceImpl extends ServiceImpl<UserCapaDao, UserCapa> implements UserCapaService {

    @Resource
    private OrderService orderService;
    @Resource
    private CapaService capaService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserCapaSnapshotService snapshotService;
    @Resource
    private UserService userService;
    @Resource
    private ConditionChain conditionChain;
    @Resource
    private UserCapaDao dao;

    @Override
    public UserCapa getByUser(Integer uid) {
        UserCapa userCapa = getOne(new QueryWrapper<UserCapa>().lambda().eq(UserCapa::getUid, uid));
        if (userCapa != null) {
            Capa capa = capaService.getById(userCapa.getCapaId());
            userCapa.setCapaName(capa.getName());
            userCapa.setCapaUrl(capa.getIconUrl());
        }
        return userCapa;
    }

    @Override
    public UserCapa saveOrUpdateCapa(Integer uid, Long capaId, String remark, String description) {
        UserCapa userCapa = getByUser(uid);
        // 等级相同无须处理
        if (userCapa != null && NumberUtil.compare(capaId, userCapa.getCapaId()) == 0) {
            return userCapa;
        }
        // 新增等级
        String type = "";
        if (userCapa == null) {
            userCapa = UserCapa.builder().uid(uid).capaId(capaId).build();
            type = UserCapaSnapshot.Constants.升级.toString();
        } else {
            type = NumberUtil.compare(userCapa.getCapaId(), capaId) > 0 ? UserCapaSnapshot.Constants.降级.toString()
                    : UserCapaSnapshot.Constants.升级.toString();
            userCapa.setCapaId(capaId);
        }
        saveOrUpdate(userCapa);
        // 记录快照
        UserCapaSnapshot snapshot = UserCapaSnapshot.builder().uid(uid).capaId(capaId).type(type).remark(remark)
                .description(description).build();
        snapshotService.save(snapshot);

        return getByUser(uid);
    }

    @Override
    public List<UserCapa> getUpperList(Integer uid, List<Long> capaIds, Integer num) {
        List<UserCapa> list = Lists.newArrayList();
        List<UserUpperDto> allUpper = userInvitationService.getAllUpper(uid);
        if (CollectionUtils.isEmpty(allUpper)) {
            return list;
        }
        Map<Integer, UserCapa> uidMap = getUidMap(allUpper.stream().filter(s -> s.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        for (UserUpperDto upper : allUpper) {
            if (list.size() == num) {
                return list;
            }
            if (upper.getPId() == null) {
                return list;
            }
            UserCapa userCapa = uidMap.get(upper.getPId());
            if (userCapa != null && capaIds.contains(userCapa.getCapaId())) {
                list.add(userCapa);
            }
        }
        return list;
    }

    @Override
    public List<UserCapa> getInvitationUnder(Integer uid, Long capaId) {
        return dao.getInvitationUnder(uid, capaId);
    }

    @Override
    public PageInfo<UserCapa> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapa> userCapaLambdaQueryWrapper = new LambdaQueryWrapper<UserCapa>();
        userCapaLambdaQueryWrapper.eq(!ObjectUtil.isNull(uid), UserCapa::getUid, uid);
        userCapaLambdaQueryWrapper.eq(!ObjectUtil.isNull(capaId), UserCapa::getCapaId, capaId);
        Page<UserCapa> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapa> list = list(userCapaLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        Map<Integer, User> userMap = userService.getUidMapList(list.stream().map(UserCapa::getUid).collect(Collectors.toList()));
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        list.forEach(e -> {
            e.setAccount(userMap.get(e.getUid()).getAccount());
            Capa capa = capaMap.get(e.getCapaId());
            e.setCapaName(capa.getName());
            e.setCapaUrl(capa.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);

    }

    @Async
    @Override
    public void riseCapa(Integer uid) {
        UserCapa userCapa = getByUser(uid);// 用户等级
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        Long capaId = userCapa == null ? capaService.getMinCapa().getId() : userCapa.getCapaId();
        Long riseCapaId = null;
        Map<String, Boolean> riseMap = Maps.newConcurrentMap();
        do {
            // 检查当前等级满足
            Capa capa = capaMap.get(capaId);
            // 升级条件
            List<RiseCondition> conditionList = capa.getConditionList();

            Map<String, Boolean> map = Maps.newConcurrentMap();
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(conditionList)){
                for (RiseCondition riseCondition : conditionList) {
                    Boolean ok = conditionChain.isOk(uid, riseCondition);
                    map.put(riseCondition.getName(), ok);
                }
            }
            // 是否满足升级条件
            Boolean ifRise = capa.parser(map);
            if(BooleanUtils.isTrue(ifRise)){
                riseCapaId = capa.getId();
                riseMap = map;
            }
            capaId = capa.getPCapaId();
            if (capaId == null) {
                break;
            }
        } while (true);

        // 如果有满足的等级
        if (riseCapaId == null) {
            return;
        }
        // 升级
        if (userCapa == null || riseCapaId.compareTo(userCapa.getCapaId()) > 0) {
            String orderNo = "";
            Boolean b = riseMap.get(ConditionEnum.单笔金额升级.getName());
            if (b != null && BooleanUtils.isTrue(b)) {
                Order order = orderService.getLastOne(uid, "");
                if (order != null) {
                    orderNo = order.getOrderNo();
                }
            }
            b = riseMap.get(ConditionEnum.补差金额升级.getName());
            if (b != null && BooleanUtils.isTrue(b)) {
                Order order = orderService.getLastOne(uid, "报单");
                if (order != null) {
                    orderNo = order.getOrderNo();
                }
            }
            Set<String> strings = riseMap.keySet();
            String join = StringUtils.join(strings, ",");
            saveOrUpdateCapa(uid, riseCapaId, join, orderNo);
        }
    }

    @Override
    public Map<Integer, UserCapa> getUidMap(List<Integer> uIdList) {
        LambdaQueryWrapper<UserCapa> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserCapa::getUid, uIdList);
        List<UserCapa> userList = dao.selectList(lqw);
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        userList.forEach(e -> {
            e.setCapaName(capaMap.get(e.getCapaId()).getName());
        });
        return FunctionUtil.keyValueMap(userList,UserCapa::getUid );
    }
}
