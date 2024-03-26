package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.UserRelationDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserRelationServiceImpl extends ServiceImpl<UserRelationDao, UserRelation> implements UserRelationService {

    @Resource
    private UserRelationDao dao;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserRelationFlowService userRelationFlowService;
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaXsService userCapaXsService;

    @Override
    public UserRelation getByUid(Integer uId) {
        return getOne(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getUId, uId));
    }

    @Override
    public Integer getPid(Integer uId) {
        UserRelation userRelation = getByUid(uId);
        return userRelation == null ? null : userRelation.getPId();
    }

    @Override
    public List<UserRelation> getByPid(Integer pId) {
        return list(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getPId, pId).orderByAsc(UserRelation::getNode));
    }

    @Override
    public UserRelation getByPid(Integer pId, Integer node) {
        return getOne(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getPId, pId).eq(UserRelation::getNode, node));
    }

    /**
     * 第一条记录是自己
     */
    @Override
    public List<UserUpperDto> getAllUpper(Integer uId) {
        return dao.getAllUpper(uId);
    }

    @Override
    public Boolean hasChild(Integer uId, Integer pId) {
        List<UserUpperDto> allUpper = getAllUpper(uId);
        return !ListUtils.emptyIfNull(allUpper).stream().filter(u -> pId.intValue() == u.getPId().intValue()).collect(Collectors.toList()).isEmpty();
    }

    @Override
    public void validBand(Integer uId, Integer pId, Integer operateId, Integer node) {
        if (uId.intValue() == pId.intValue()) {
            throw new RuntimeException("自己不能绑定自己");
        }
        UserRelation receiverUser = getByPid(pId, node);
        if (receiverUser != null) {
            throw new RuntimeException("服务人节点被占用:账号为" + pId + "点位:" + node);
        }
        if (hasChild(pId, uId)) {
            throw new RuntimeException("接受人不能是被安置人的下级, 被安置人:" + uId + "接受人:" + pId);
        }
        if (operateId != null) {
            if (!hasChild(pId, operateId)) {
                throw new RuntimeException("接受人不是当前操作用户的下级, 接受人:" + pId + "操作人:" + operateId);
            }
            if (!hasChild(uId, operateId)) {
                throw new RuntimeException("被安置人不是当前操作用户的下级, 被安置人:" + uId + "操作人:" + operateId);
            }
        }
    }

    @Override
    public UserRelation band(Integer uId, Integer pId, Integer operateId, Integer node) {
        validBand(uId, pId, operateId, node);
        UserRelation userRelation = getByUid(uId);
        if (userRelation == null) {
            userRelation = UserRelation.builder().uId(uId).pId(pId).node(node).build();
        }
        userRelation.setNode(node);
        userRelation.setUId(uId);
        userRelation.setPId(pId);

        // 执行更新
        saveOrUpdate(userRelation);
        // 删除关系留影
        userRelationFlowService.clear(uId);
        return userRelation;
    }

    @Override
    public List<UserRelation> getNoFlowList() {
        return dao.getNoFlowList();
    }

    @Override
    public UserRelation getLeftMost(Integer userId) {
        UserRelation userRelation = new UserRelation();
        // 往下查询大区
        do {
            List<UserRelation> nextRelation = getByPid(userId);
            if (nextRelation.isEmpty()) {
                userRelation.setPId(userId);
                userRelation.setNode(0);
                return userRelation;
            }
            // 安置有一个下级
            if (nextRelation.size() == 1) {
                userId = nextRelation.get(0).getUId();
            }
            // 安置有2个小计 比较业绩大小
            if (nextRelation.size() == 2) {
                Map<Integer, UserRelation> map = FunctionUtil.keyValueMap(nextRelation, UserRelation::getNode);
                // 左
                RelationScore left = relationScoreService.getByUser(map.get(0).getUId(), map.get(0).getNode());
                BigDecimal leftScore = left == null ? BigDecimal.ZERO : left.getUsableScore().add(left.getUsedScore());
                // 右
                RelationScore right = relationScoreService.getByUser(map.get(1).getUId(), map.get(1).getNode());
                BigDecimal rightScore = right == null ? BigDecimal.ZERO : right.getUsableScore().add(right.getUsedScore());
                // 返回大区 继续往下走
                userId = ArithmeticUtils.gte(leftScore, rightScore) ? map.get(0).getUId() : map.get(1).getUId();
            }
        } while (true);
    }

    @Override
    public PageInfo<UserRelation> pageList(Integer uid, Integer pid, Integer node, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserRelation> lqw = new LambdaQueryWrapper<UserRelation>()
                .eq(!ObjectUtil.isNull(uid), UserRelation::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserRelation::getPId, pid)
                .eq(!ObjectUtil.isNull(node), UserRelation::getNode, node)
                .orderByDesc(UserRelation::getId)
                .orderByAsc(UserRelation::getNode);
        Page<UserRelation> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserRelation> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserRelation::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<Integer> pIdList = list.stream().map(UserRelation::getPId).collect(Collectors.toList());
        Map<Integer, User> pidMapList = userService.getUidMapList(pIdList);
        //等级
        Map<Integer, UserCapa> capaUidMapList = userCapaService.getUidMap(uIdList);
        Map<Integer, UserCapa> capaPidMapList = userCapaService.getUidMap(pIdList);
        //星级
        Map<Integer, UserCapaXs> capaXsUidMapList = userCapaXsService.getUidMap(uIdList);
        Map<Integer, UserCapaXs> capaXsPidMapList = userCapaXsService.getUidMap(pIdList);
        list.forEach(e -> {
            User uUser = uidMapList.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            UserCapa uUserCapa = capaUidMapList.get(e.getUId());
            e.setUCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapa pUserCapa = capaPidMapList.get(e.getPId());
            e.setPCapaName(pUserCapa != null ? pUserCapa.getCapaName() : "");
            User pUser = pidMapList.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ?  pUser.getNickname() : "");
            UserCapaXs uUserCapaXs = capaXsUidMapList.get(e.getUId());
            e.setUCapaXsName(uUserCapaXs!=null?uUserCapaXs.getCapaName():"");
            UserCapaXs pUserCapaXs = capaXsPidMapList.get(e.getPId());
            e.setPCapaXsName(pUserCapaXs!=null?pUserCapaXs.getCapaName():"");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void del(Integer uid) {
        UserRelation userRelation = getByUid(uid);
        if(userRelation != null){
            removeById(userRelation.getId());
            userRelationFlowService.clear(uid);
        }
    }
}
