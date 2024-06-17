package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.UserRelationGplotVo;
import com.jbp.service.dao.agent.UserRelationFlowDao;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserRelationFlowServiceImpl extends ServiceImpl<UserRelationFlowDao, UserRelationFlow> implements UserRelationFlowService {

    @Resource
    private UserRelationService userRelationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private CapaService capaService;


    @Override
    public void clear(Integer uid) {
        List<UserRelationFlow> list = list(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid));
        List<Integer> userIdList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        userIdList.add(uid);
        remove(new LambdaQueryWrapper<UserRelationFlow>().in(UserRelationFlow::getUId, userIdList));
    }

    @Override
    public void refresh(Integer uId) {
        // 查询出所有的上级
        List<UserUpperDto> upperList = userRelationService.getAllUpper(uId);
        // 如果上级没有就直接返回
        if (CollectionUtils.isEmpty(upperList)) {
            return;
        }
        // 获取所有的上级添加关系
        List<UserRelationFlow> list = Lists.newArrayList();
        int i = 1;
        for (UserUpperDto upper : upperList) {
            if (upper.getPId() != null && upper.getPId() > 0) {
                UserRelationFlow flow = new UserRelationFlow(uId, upper.getPId(), upper.getLevel(), upper.getNode());
                list.add(flow);
            }
            log.info("增在处理用户:{} 的服务层级关系:{}, 总关系:{}", uId, i++, upperList.size());
        }
        // 保存 list空 mybatis自带剔除
        if (CollectionUtils.isNotEmpty(list)) {
            List<List<UserRelationFlow>> partition = Lists.partition(list, 500);
            for (List<UserRelationFlow> userRelationFlows : partition) {
                saveBatch(userRelationFlows);
            }
        }

    }

    @Override
    public PageInfo<UserRelationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserRelationFlow> lqw = new LambdaQueryWrapper<UserRelationFlow>()
                .eq(!ObjectUtil.isNull(uid), UserRelationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserRelationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(level), UserRelationFlow::getLevel, level)
                .orderByDesc(UserRelationFlow::getId)
                .orderByAsc(UserRelationFlow::getNode);
        Page<UserRelationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserRelationFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        uIdList.addAll(list.stream().map(UserRelationFlow::getPId).collect(Collectors.toList()));
        Map<Integer, User> userMap = userService.getUidMapList(uIdList);
        //等级
        Map<Integer, UserCapa> capaMapList = userCapaService.getUidMap(uIdList);
        Map<Integer, UserCapaXs> capaXsMapList = userCapaXsService.getUidMap(uIdList);
        //团队
        Map<Integer, TeamUser> teamUserMapList = teamUserService.getUidMapList(uIdList);
        list.forEach(e -> {
            User uUser = userMap.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            UserCapa uUserCapa = capaMapList.get(e.getUId());
            e.setUCapaName(uUserCapa != null ? uUserCapa.getCapaName() : "");
            UserCapaXs uUserCapaXs = capaXsMapList.get(e.getUId());
            e.setUCapaXsName(uUserCapaXs != null ? uUserCapaXs.getCapaName() : "");

            User pUser = userMap.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ? pUser.getNickname() : "");
            UserCapa pUserCapa = capaMapList.get(e.getPId());
            e.setPCapaName(pUserCapa != null ? pUserCapa.getCapaName() : "");
            UserCapaXs pUserCapaXs = capaXsMapList.get(e.getPId());
            e.setPCapaXsName(pUserCapaXs != null ? pUserCapaXs.getCapaName() : "");
            //团队
            TeamUser teamUser = teamUserMapList.get(e.getUId());
            e.setTeamName(teamUser!=null?teamUser.getName():"");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public UserRelationGplotVo gplot(Integer uid,Integer uid0,Integer uid1,Map<Integer, User> uidMapList,Map<Integer, UserCapa> uidCapaMap) {
        UserRelationGplotVo vo = new UserRelationGplotVo();
        User user = uidMapList.get(uid);
        vo.setUAccount(user.getAccount());
        vo.setUNickName(user.getNickname());
        vo.setCreateTime(user.getCreateTime());
        UserCapa userCapa = uidCapaMap.get(uid);
        vo.setUcapaId(userCapa.getCapaId());

        //左右区各等级人数
        vo.setCapaSumMap(selectByCapa(uid0));
        vo.setCapaSumMap2(selectByCapa(uid1));

        //当前用户左右区总业绩
        BigDecimal tenThousand = new BigDecimal("10000");
        RelationScore relationScore = relationScoreService.getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, uid).eq(RelationScore::getNode, 0));
        vo.setTotalScore(relationScore == null ? BigDecimal.ZERO : (relationScore.getUsableScore().add(relationScore.getUsedScore())).divide(tenThousand, 2, RoundingMode.HALF_UP));
        RelationScore relationScore2 = relationScoreService.getOne(new QueryWrapper<RelationScore>().lambda().eq(RelationScore::getUid, uid).eq(RelationScore::getNode, 1));
        vo.setTotalScore2(relationScore2 == null ? BigDecimal.ZERO : (relationScore2.getUsableScore().add(relationScore2.getUsedScore())).divide(tenThousand, 2, RoundingMode.HALF_UP));
        return vo;
    }

    @Override
    public UserRelationGplotVo gplotInfo(Integer uid) {
        if (uid == null) {
            return null;
        }

        List<UserRelationFlow> list = list(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid).in(UserRelationFlow::getLevel, 1, 2, 3));
        List<Integer> uidList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        uidList.add(uid);
        Map<Integer, User> uidMapList = userService.getUidMapList(uidList);
        Map<Integer, UserCapa> uidCapaMap = userCapaService.getUidMap(uidList);

        List<Integer> uidFlowList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        uidFlowList.add(uid);
        List<UserRelation> userRelationList = userRelationService.list(new QueryWrapper<UserRelation>().lambda().in(UserRelation::getPId, uidFlowList));
        Map<String,UserRelation> map = new HashMap<>();
        userRelationList.forEach(e->{
            map.put(e.getPId()+"_"+e.getNode(),e);
        });
        //第一层
        UserRelation userRelation = map.get(uid + "_0");
        UserRelation userRelation1 = map.get(uid + "_1");
        UserRelationGplotVo top = gplot(uid,userRelation!=null ? userRelation.getUId() : 0,userRelation1!=null ? userRelation1.getUId() : 0,uidMapList,uidCapaMap);
        top.setLevel(0);
        List<UserRelationGplotVo> topList = new ArrayList<>();
        if (userRelation != null) {
            //第二层
            UserRelation userRelation00 = map.get(userRelation.getUId() + "_0");
            UserRelation userRelation01 = map.get(userRelation.getUId() + "_1");
            UserRelationGplotVo relation0 = gplot(userRelation.getUId(),userRelation00!=null ? userRelation00.getUId() : 0,userRelation01!=null ? userRelation01.getUId() : 0,uidMapList,uidCapaMap);
            relation0.setLevel(1);
            relation0.setNode(0);
            topList.add(relation0);
            List<UserRelationGplotVo> topList1 = new ArrayList<>();
            if (userRelation00 != null) {
                //第三层
                UserRelation userRelation000 = map.get(userRelation00.getUId() + "_0");
                UserRelation userRelation001 = map.get(userRelation00.getUId() + "_1");
                UserRelationGplotVo relation00 = gplot(userRelation00.getUId(),userRelation000!=null ? userRelation000.getUId() : 0,userRelation001!=null ? userRelation001.getUId() : 0,uidMapList,uidCapaMap);
                relation00.setLevel(2);
                relation00.setNode(0);
                topList1.add(relation00);
                List<UserRelationGplotVo> topList21 = new ArrayList<>();
                if (userRelation000 != null) {
                    //第四层
                    UserRelation userRelation0000 = map.get(userRelation000.getUId() + "_0");
                    UserRelation userRelation0001 = map.get(userRelation000.getUId() + "_1");
                    UserRelationGplotVo relation000 = gplot(userRelation000.getUId(),userRelation0000!=null ? userRelation0000.getUId() : 0,userRelation0001!=null ? userRelation0001.getUId() : 0,uidMapList,uidCapaMap);
                    relation000.setLevel(3);
                    relation000.setNode(0);
                    topList21.add(relation000);
                } else {
                    UserRelationGplotVo relation000 = new UserRelationGplotVo();
                    topList21.add(relation000);
                }
                if (userRelation001 != null) {
                    //第四层
                    UserRelation userRelation0010 = map.get(userRelation001.getUId() + "_0");
                    UserRelation userRelation0011 = map.get(userRelation001.getUId() + "_1");
                    UserRelationGplotVo relation001 = gplot(userRelation001.getUId(),userRelation0010!=null ? userRelation0010.getUId() : 0,userRelation0011!=null ? userRelation0011.getUId() : 0,uidMapList,uidCapaMap);
                    relation001.setLevel(3);
                    relation001.setNode(1);
                    topList21.add(relation001);
                } else {
                    UserRelationGplotVo relation001 = new UserRelationGplotVo();
                    topList21.add(relation001);
                }
                relation00.setChildren(topList21);
            } else {
                UserRelationGplotVo relation10 = new UserRelationGplotVo();
                topList1.add(relation10);
            }
            if (userRelation01 != null) {
                //第三层
                UserRelation userRelation010 = map.get(userRelation01.getUId() + "_0");
                UserRelation userRelation011 = map.get(userRelation01.getUId() + "_1");
                UserRelationGplotVo relation01 = gplot(userRelation01.getUId(),userRelation010!=null ? userRelation010.getUId() : 0,userRelation011!=null ? userRelation011.getUId() : 0,uidMapList,uidCapaMap);
                relation01.setLevel(2);
                relation01.setNode(1);
                topList1.add(relation01);
                List<UserRelationGplotVo> topList22 = new ArrayList<>();
                if (userRelation010 != null) {
                    //第四层
                    UserRelation userRelation0100 = map.get(userRelation010.getUId() + "_0");
                    UserRelation userRelation0101 = map.get(userRelation010.getUId() + "_1");
                    UserRelationGplotVo relation010 = gplot(userRelation010.getUId(),userRelation0100!=null ? userRelation0100.getUId() : 0,userRelation0101!=null ? userRelation0101.getUId() : 0,uidMapList,uidCapaMap);
                    relation010.setLevel(3);
                    relation010.setNode(0);
                    topList22.add(relation010);
                } else {
                    UserRelationGplotVo relation010 = new UserRelationGplotVo();
                    topList22.add(relation010);
                }
                if (userRelation011 != null) {
                    //第四层
                    UserRelation userRelation0110 = map.get(userRelation011.getUId() + "_0");
                    UserRelation userRelation0111 = map.get(userRelation011.getUId() + "_1");
                    UserRelationGplotVo relation011 = gplot(userRelation011.getUId(),userRelation0110!=null ? userRelation0110.getUId() : 0,userRelation0111!=null ? userRelation0111.getUId() : 0,uidMapList,uidCapaMap);
                    relation011.setLevel(3);
                    relation011.setNode(1);
                    topList22.add(relation011);
                } else {
                    UserRelationGplotVo relation011 = new UserRelationGplotVo();
                    topList22.add(relation011);
                }
                relation01.setChildren(topList22);
            } else {
                UserRelationGplotVo relation01 = new UserRelationGplotVo();
                topList1.add(relation01);
            }
            relation0.setChildren(topList1);
        } else {
            UserRelationGplotVo relation0 = new UserRelationGplotVo();
            topList.add(relation0);
        }

        if (userRelation1 != null) {
            //第二层
            UserRelation userRelation10 = map.get(userRelation1.getUId() + "_0");
            UserRelation userRelation11 = map.get(userRelation1.getUId() + "_1");
            UserRelationGplotVo relation1 = gplot(userRelation1.getUId(),userRelation10!=null ? userRelation10.getUId() : 0,userRelation11!=null ? userRelation11.getUId() : 0,uidMapList,uidCapaMap);
            relation1.setLevel(1);
            relation1.setNode(1);
            topList.add(relation1);
            List<UserRelationGplotVo> topList2 = new ArrayList<>();
            if (userRelation10 != null) {
                //第三层
                UserRelation userRelation100 = map.get(userRelation10.getUId() + "_0");
                UserRelation userRelation101 = map.get(userRelation10.getUId() + "_1");
                UserRelationGplotVo relation10 = gplot(userRelation10.getUId(),userRelation100!=null ? userRelation100.getUId() : 0,userRelation101!=null ? userRelation101.getUId() : 0,uidMapList,uidCapaMap);
                relation10.setLevel(2);
                relation10.setNode(0);
                topList2.add(relation10);
                List<UserRelationGplotVo> topList23 = new ArrayList<>();
                if (userRelation100 != null) {
                    //第四层
                    UserRelation userRelation1000 = map.get(userRelation100.getUId() + "_0");
                    UserRelation userRelation1001 = map.get(userRelation100.getUId() + "_1");
                    UserRelationGplotVo relation100 = gplot(userRelation100.getUId(),userRelation1000!=null ? userRelation1000.getUId() : 0,userRelation1001!=null ? userRelation1001.getUId() : 0,uidMapList,uidCapaMap);
                    relation100.setLevel(3);
                    relation100.setNode(0);
                    topList23.add(relation100);
                } else {
                    UserRelationGplotVo relation100 = new UserRelationGplotVo();
                    topList23.add(relation100);
                }
                if (userRelation101 != null) {
                    //第四层
                    UserRelation userRelation1010 = map.get(userRelation101.getUId() + "_0");
                    UserRelation userRelation1011 = map.get(userRelation101.getUId() + "_1");
                    UserRelationGplotVo relation101 = gplot(userRelation101.getUId(),userRelation1010!=null ? userRelation1010.getUId() : 0,userRelation1011!=null ? userRelation1011.getUId() : 0,uidMapList,uidCapaMap);
                    relation101.setLevel(3);
                    relation101.setNode(1);
                    topList23.add(relation101);
                } else {
                    UserRelationGplotVo relation101 = new UserRelationGplotVo();
                    topList23.add(relation101);
                }
                relation10.setChildren(topList23);
            } else {
                UserRelationGplotVo relation20 = new UserRelationGplotVo();
                topList2.add(relation20);
            }
            if (userRelation11 != null) {
                //第三层
                UserRelation userRelation110 = map.get(userRelation11.getUId() + "_0");
                UserRelation userRelation111 = map.get(userRelation11.getUId() + "_1");
                UserRelationGplotVo relation11 = gplot(userRelation11.getUId(),userRelation110!=null ? userRelation110.getUId() : 0,userRelation111!=null ? userRelation111.getUId() : 0,uidMapList,uidCapaMap);
                relation11.setLevel(2);
                relation11.setNode(1);
                topList2.add(relation11);
                List<UserRelationGplotVo> topList24 = new ArrayList<>();
                if (userRelation110 != null) {
                    //第四层
                    UserRelation userRelation1100 = map.get(userRelation110.getUId() + "_0");
                    UserRelation userRelation1101 = map.get(userRelation110.getUId() + "_1");
                    UserRelationGplotVo relation110 = gplot(userRelation110.getUId(),userRelation1100!=null ? userRelation1100.getUId() : 0,userRelation1101!=null ? userRelation1101.getUId() : 0,uidMapList,uidCapaMap);
                    relation110.setLevel(3);
                    relation110.setNode(0);
                    topList24.add(relation110);
                } else {
                    UserRelationGplotVo relation110 = new UserRelationGplotVo();
                    topList24.add(relation110);
                }
                if (userRelation111 != null) {
                    //第四层
                    UserRelation userRelation1110 = map.get(userRelation111.getUId() + "_0");
                    UserRelation userRelation1111 = map.get(userRelation111.getUId() + "_1");
                    UserRelationGplotVo relation111 = gplot(userRelation111.getUId(),userRelation1110!=null ? userRelation1110.getUId() : 0,userRelation1111!=null ? userRelation1111.getUId() : 0,uidMapList,uidCapaMap);
                    relation111.setLevel(3);
                    relation111.setNode(1);
                    topList24.add(relation111);
                } else {
                    UserRelationGplotVo relation111 = new UserRelationGplotVo();
                    topList24.add(relation111);
                }
                relation11.setChildren(topList24);
            } else {
                UserRelationGplotVo relation11 = new UserRelationGplotVo();
                topList2.add(relation11);
            }
            relation1.setChildren(topList2);
        } else {
            UserRelationGplotVo relation1 = new UserRelationGplotVo();
            topList.add(relation1);
        }
        top.setChildren(topList);
        return top;
    }

    @Override
    public List<Integer> selectByCapa(Integer uId) {
        List<Integer> countList = new ArrayList<>();
        if (uId==0) {
            countList.addAll(Arrays.asList(0,0,0,0,0,0));
            return countList;
        }
        List<UserRelationFlow> list = list(new LambdaQueryWrapper<UserRelationFlow>().eq(UserRelationFlow::getPId, uId));
        List<Integer> uidList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        uidList.add(uId);
        List<UserCapa> userCapaList = new ArrayList<>(userCapaService.getUidMap(uidList).values());
        List<Capa> capaList = capaService.getList();
        capaList.forEach(e->{
            List<UserCapa> capas = userCapaList.stream().filter(f -> Objects.equals(f.getCapaId(), e.getId())).collect(Collectors.toList());
            countList.add(capas.size());

        });
        return countList;
    }
}
