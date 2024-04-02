package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.condition.ConditionEnum;
import com.jbp.service.dao.agent.UserCapaXsDao;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaXsService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserCapaXsSnapshotService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserCapaXsServiceImpl extends ServiceImpl<UserCapaXsDao, UserCapaXs> implements UserCapaXsService {

    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UserCapaXsSnapshotService snapshotService;
    @Resource
    private UserService userService;
    @Resource
    private ConditionChain conditionChain;
    @Resource
    private UserCapaXsDao userCapaXsDao;
    @Resource
    private OrderService orderService;

    @Override
    public UserCapaXs getByUser(Integer uid) {
        UserCapaXs userCapaXs = getOne(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
        if (userCapaXs != null) {
            CapaXs capaXs = capaXsService.getById(userCapaXs.getCapaId());
            userCapaXs.setCapaName(capaXs.getName());
            userCapaXs.setCapaUrl(capaXs.getIconUrl());
        }
        return userCapaXs;
    }

	@Override
	public UserCapaXs saveOrUpdateCapa(Integer uid, Long capaXsId, Boolean ifFake, String remark, String description) {
		if (capaXsId == null) {
			remove(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
			// 记录快照
			UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type("删除")
					.remark(remark).description(description).build();
			snapshotService.save(snapshot);
			return getByUser(uid);
		}
		UserCapaXs userCapaXs = getByUser(uid);
		// 等级相同无须处理
		if (userCapaXs != null && NumberUtil.compare(capaXsId, userCapaXs.getCapaId()) == 0) {
            userCapaXs.setIfFake(ifFake);
            saveOrUpdate(userCapaXs);
            return getByUser(uid);
		}
		String type = "";
		// 新增等级
		if (userCapaXs == null) {
			userCapaXs = UserCapaXs.builder().uid(uid).capaId(capaXsId).ifFake(ifFake).build();
			type = UserCapaXsSnapshot.Constants.升级.toString();
		} else {
			type = NumberUtil.compare(userCapaXs.getCapaId(), capaXsId) > 0 ? UserCapaXsSnapshot.Constants.降级.toString()
					: UserCapaXsSnapshot.Constants.升级.toString();
            userCapaXs.setIfFake(ifFake);
			userCapaXs.setCapaId(capaXsId);
		}
		saveOrUpdate(userCapaXs);
		// 记录快照
		UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(capaXsId).type(type).remark(remark)
				.description(description).build();
		snapshotService.save(snapshot);
		return getByUser(uid);
	}

    @Override
    public void del(Integer uid, String description, String remark) {
        UserCapaXs userCapaXs = userCapaXsDao.selectOne(new LambdaQueryWrapper<UserCapaXs>().eq(UserCapaXs::getUid, uid));
        remove(new QueryWrapper<UserCapaXs>().lambda().eq(UserCapaXs::getUid, uid));
            // 记录快照
            UserCapaXsSnapshot snapshot = UserCapaXsSnapshot.builder().uid(uid).capaId(userCapaXs.getCapaId()).type("删除")
                    .remark(remark).description(description).build();
            snapshotService.save(snapshot);
    }

    @Override
    public PageInfo<UserCapaXs> pageList(Integer uid, Long capaId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserCapaXs> userCapaXsLambdaQueryWrapper = new LambdaQueryWrapper<UserCapaXs>();
        userCapaXsLambdaQueryWrapper.eq(!ObjectUtil.isNull(uid), UserCapaXs::getUid, uid);
        userCapaXsLambdaQueryWrapper.eq(!ObjectUtil.isNull(capaId), UserCapaXs::getCapaId, capaId);
        Page<UserCapaXs> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserCapaXs> list = list(userCapaXsLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        Map<Integer, User> userMap = userService.getUidMapList(list.stream().map(UserCapaXs::getUid).collect(Collectors.toList()));
        Map<Long, CapaXs> capaXsMap = capaXsService.getCapaXsMap();
        list.forEach(e -> {
            e.setAccount(userMap.get(e.getUid()).getAccount());
            CapaXs capaXs = capaXsMap.get(e.getCapaId());
            e.setCapaName(capaXs.getName());
            e.setCapaUrl(capaXs.getIconUrl());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Async
    @Override
    public void riseCapaXs(Integer uid) {
        UserCapaXs userCapaXs = getByUser(uid);// 用户星级
        Map<Long, CapaXs> capaXsMap = capaXsService.getCapaXsMap();
        Long capaId = userCapaXs == null ? capaXsService.getMinCapa().getId() : userCapaXs.getCapaId();
        Long riseCapaId = null;
        Map<String, Boolean> riseMap = Maps.newConcurrentMap();
        do {
            // 检查当前等级满足 更新非虚拟  不满足自己等级存在就是虚拟
            CapaXs capaXs = capaXsMap.get(capaId);
            // 升级条件
            List<RiseCondition> conditionList = capaXs.getConditionList();
            Map<String, Boolean> map = Maps.newConcurrentMap();
            if(CollectionUtils.isNotEmpty(conditionList)){
                for (RiseCondition riseCondition : conditionList) {
                    Boolean ok = conditionChain.isOk(uid, riseCondition);
                    map.put(riseCondition.getName(), ok);
                }
            }
            // 是否满足升级条件
            Boolean ifRise = capaXs.parser(map);
            if (BooleanUtils.isTrue(ifRise)) {
                riseCapaId = capaXs.getId();
                riseMap = map;
            }
            capaId = capaXs.getPCapaId();
            if (capaId == null) {
                break;
            }
        } while (true);

        // 如果有满足的等级
        if (riseCapaId == null) {
            return;
        }
        // 升级[等级相同也不是虚拟等级退出]
        if(userCapaXs != null && riseCapaId.compareTo(userCapaXs.getCapaId()) == 0 && BooleanUtils.isFalse(userCapaXs.getIfFake())){
            return;
        }
        if (userCapaXs == null || riseCapaId.compareTo(userCapaXs.getCapaId()) >= 0) {
            String orderNo = "";
            Boolean b = riseMap.get(ConditionEnum.单笔金额升级.getName());
            if(b != null && BooleanUtils.isTrue(b)){
                Order order = orderService.getLastOne(uid, "");
                if(order != null){
                    orderNo = order.getOrderNo();
                }
            }
            b = riseMap.get(ConditionEnum.补差金额升级.getName());
            if(b != null && BooleanUtils.isTrue(b)) {
                Order order = orderService.getLastOne(uid, "报单");
                if (order != null) {
                    orderNo = order.getOrderNo();
                }
            }
            Set<String> strings = riseMap.keySet();
            String join = StringUtils.join(strings, ",");
            saveOrUpdateCapa(uid, riseCapaId, false, join, orderNo);
        }
    }

    @Override
    public Map<Integer, UserCapaXs> getUidMap(List<Integer> uIdList) {
        LambdaQueryWrapper<UserCapaXs> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserCapaXs::getUid, uIdList);
        List<UserCapaXs> userList = list(lqw);
        Map<Long, CapaXs> capaXsMap = capaXsService.getCapaXsMap();
        userList.forEach(e -> {
            e.setCapaName(capaXsMap.get(e.getCapaId()).getName());
        });
        return FunctionUtil.keyValueMap(userList,UserCapaXs::getUid );
    }

    @Override
    public List<UserCapaXs> getRelationUnder(Integer uid, Long capaId) {
        return userCapaXsDao.getRelationUnder(uid, capaId);
    }

    @Override
    public List<UserCapaXs> getInvitationUnder(Integer uid, Long capaId) {
        return userCapaXsDao.getInvitationUnder(uid, capaId);
    }
}
